package org.lazan.t5.cometd.services.internal;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.services.Authorizer;
import org.lazan.t5.cometd.services.Authorizers;
import org.lazan.t5.cometd.services.CometdGlobals;

@UsesOrderedConfiguration(Authorizer.class)
public class AuthorizersImpl implements Authorizers {
	private final TopicMatchers<Authorizer> authorizers;
	private final CometdGlobals cometdGlobals;
	private final HttpServletRequest request;
	
	public AuthorizersImpl(List<Authorizer> authorizers, CometdGlobals cometdGlobals, HttpServletRequest request) {
		super();
		this.authorizers = new TopicMatchers<Authorizer>();
		for (Authorizer auth : authorizers) {
			addAuthorizer(auth);
		}
		this.cometdGlobals = cometdGlobals;
		this.request = request;
	}
	
	public void addAuthorizer(Authorizer auth) {
		authorizers.addMatcher(auth.getTopicPattern(), auth);
	}
	
	public boolean removeAuthorizer(Authorizer auth) {
		return authorizers.removeMatcher(auth.getTopicPattern(), auth);
	}

	public Result authorize(Operation operation, ChannelId channel, ServerSession serverSession, ServerMessage message) {
		if (operation == Operation.SUBSCRIBE) {
			Map<String, Object> data = message.getDataAsMap();

			String channelId = getRequiredString(data, "channelId");
			ClientContext clientContext = cometdGlobals.getClientContext(channelId);
			boolean firstClient = false;
			if (clientContext == null) {
				firstClient = true;
				clientContext = getClientContext(data);
			}
			String topic = clientContext.getTopic();
			for (Authorizer auth : authorizers.getMatches(topic)) {
				if (!auth.isAuthorized(clientContext)) {
					return Result.deny("Authorization failure");
				}
			}
			if (clientContext.isSession()) {
				WeakReference<HttpSession> sessionRef = new WeakReference<HttpSession>(request.getSession());
				serverSession.setAttribute("sessionRef", sessionRef);
			}
			if (firstClient) {
				// synchronization not required here, if it gets set twice that's fine
				cometdGlobals.setClientContext(clientContext.getTopic(), channelId, clientContext);
			}
		}
		return Result.grant();
	}
	
	protected ClientContext getClientContext(Map<String, Object> data) {

		String activePageName = getRequiredString(data, "activePageName");
		String containingPageName = getRequiredString(data, "containingPageName");
		String nestedComponentId = (String) data.get("nestedComponentId");
		if (nestedComponentId == null) {
			nestedComponentId = "";
		}
		String eventType = getRequiredString(data, "eventType");
		boolean session = "true".equals(getRequiredString(data, "session"));
		String topic = getRequiredString(data, "topic");

		return new ClientContext(session, activePageName, containingPageName, nestedComponentId, eventType, topic);
	}

	private String getRequiredString(Map<String, Object> data, String key) {
		String value = (String) data.get(key);
		if (value == null) {
			throw new IllegalStateException(String.format("Required attribute %s not present", key));
		}
		return value;
	}
}
