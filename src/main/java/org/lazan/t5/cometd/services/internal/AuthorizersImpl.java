package org.lazan.t5.cometd.services.internal;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.TopicMatchers;
import org.lazan.t5.cometd.services.Authorizer;
import org.lazan.t5.cometd.services.Authorizers;
import org.lazan.t5.cometd.services.CometdGlobals;
import org.lazan.t5.cometd.services.SubscriptionListeners;

@UsesConfiguration(Authorizer.class)
public class AuthorizersImpl implements Authorizers {
	private final TopicMatchers<Authorizer> authorizers;
	private final CometdGlobals cometdGlobals;
	private final HttpServletRequest request;
	private final SubscriptionListeners subscriptionListeners;
	
	public AuthorizersImpl(Collection<Authorizer> authorizerList, SubscriptionListeners subscriptionListeners, CometdGlobals cometdGlobals,
			HttpServletRequest request) {
		super();
		this.authorizers = creatTopicMatchers(authorizerList);
		this.subscriptionListeners = subscriptionListeners;
		this.cometdGlobals = cometdGlobals;
		this.request = request;
	}

	private TopicMatchers<Authorizer> creatTopicMatchers(Collection<Authorizer> list) {
		TopicMatchers<Authorizer> matchers = new TopicMatchers<Authorizer>();
		for (Authorizer auth : list) {
			matchers.add(auth.getTopicPattern(), auth);
		}
		return matchers;
	}


	public Result authorize(Operation operation, ChannelId channel, ServerSession serverSession, ServerMessage message) {
		if (operation == Operation.SUBSCRIBE) {
			Map<String, Object> data = message.getDataAsMap();
			System.err.println(String.format("%s %s %s", operation, channel, data));
			
			String channelId = getRequiredString(data, "channelId");
			String topic = getRequiredString(data, "topic");
			ClientContext clientContext = ClientContext.fromMessage(message);
			
			List<Authorizer> auths = authorizers.getMatches(topic);
			for (Authorizer auth : auths) {
				if (!auth.isAuthorized(topic, clientContext)) {
					return Result.deny("Authorization failure");
				}
			}
			if (clientContext.isSession()) {
				WeakReference<HttpSession> sessionRef = new WeakReference<HttpSession>(request.getSession());
				serverSession.setAttribute("sessionRef", sessionRef);
			}
			cometdGlobals.setClientContext(topic, channelId, clientContext);
		}
		return Result.grant();
	}
	
	private String getRequiredString(Map<String, Object> data, String key) {
		String value = (String) data.get(key);
		if (value == null) {
			throw new IllegalStateException(String.format("Required attribute %s not present", key));
		}
		return value;
	}
	

}
