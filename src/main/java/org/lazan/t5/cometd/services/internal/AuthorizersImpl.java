package org.lazan.t5.cometd.services.internal;

import static org.lazan.t5.cometd.services.CometdConstants.DATA_ACTIVE_PAGE_NAME;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_CHANNEL_ID;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_CONTAINING_PAGE_NAME;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_EVENT_TYPE;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_NESTED_COMPONENT_ID;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_PAGE_ACTIVATION_CONTEXT;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_TOPIC;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.services.Authorizer;
import org.lazan.t5.cometd.services.AuthorizerContribution;
import org.lazan.t5.cometd.services.Authorizers;
import org.lazan.t5.cometd.services.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;
import org.lazan.t5.cometd.services.PushSession;

@UsesOrderedConfiguration(AuthorizerContribution.class)
public class AuthorizersImpl implements Authorizers {
	private static final EventContext EMPTY_EVENT_CONTEXT = new EmptyEventContext();
	private final TopicMatchers<Authorizer> authorizers;
	private final CometdGlobals cometdGlobals;
	private final TypeCoercer typeCoercer;
	
	public AuthorizersImpl(List<AuthorizerContribution> contributions, CometdGlobals cometdGlobals, HttpServletRequest request, TypeCoercer typeCoercer) {
		super();
		this.authorizers = new TopicMatchers<Authorizer>();
		this.cometdGlobals = cometdGlobals;
		this.typeCoercer = typeCoercer;
		for (AuthorizerContribution contribution : contributions) {
			addAuthorizer(contribution.getTopic(), contribution.getAuthorizer());
		}
	}
	
	public void addAuthorizer(String topic, Authorizer auth) {
		authorizers.addMatcher(topic, auth);
	}
	
	public boolean removeAuthorizer(String topic, Authorizer auth) {
		return authorizers.removeMatcher(topic, auth);
	}

	public Result authorize(Operation operation, ChannelId channel, ServerSession serverSession, ServerMessage message) {
		if (operation == Operation.SUBSCRIBE) {
			Map<String, Object> data = message.getDataAsMap();

			ClientContext clientContext = parseClientContext(data);
			cometdGlobals.setClientContext(clientContext.getChannelId(), clientContext);
			
			PushSession pushSession = new PushSessionImpl(serverSession, clientContext);
			String topic = clientContext.getTopic();
			for (Authorizer auth : authorizers.getMatches(topic)) {
				if (!auth.isAuthorized(pushSession)) {
					return Result.deny("Authorization failure");
				}
			}
		}
		return Result.grant();
	}
	
	@SuppressWarnings("rawtypes")
	protected ClientContext parseClientContext(Map<String, Object> data) {
		String channelId = getRequiredString(data, DATA_CHANNEL_ID);
		String activePageName = getRequiredString(data, DATA_ACTIVE_PAGE_NAME);
		String containingPageName = getRequiredString(data, DATA_CONTAINING_PAGE_NAME);
		String nestedComponentId = (String) data.get(DATA_NESTED_COMPONENT_ID);
		if (nestedComponentId == null) {
			nestedComponentId = "";
		}
		String eventType = getRequiredString(data, DATA_EVENT_TYPE);
		String topic = getRequiredString(data, DATA_TOPIC);
		
		Object pageContextObj = data.get(DATA_PAGE_ACTIVATION_CONTEXT);
		Object[] pageContextArr = (pageContextObj instanceof Collection) ? ((Collection) pageContextObj).toArray() : (Object[]) pageContextObj;
		EventContext pageContext = EMPTY_EVENT_CONTEXT;
		if (pageContextArr != null && pageContextArr.length > 0) {
			pageContext = new ArrayEventContext(typeCoercer, pageContextArr);
		}
		
		// TODO: 
		// HttpTransport transport = (HttpTransport) bayeuxServer.getCurrentTransport();
		// BayeuxContext bayeuxContext = transport.getContext();
		// HttpSession httpSession = new CometdHttpSession(bayeuxContext)
		HttpSession httpSession = null;
		
		return new ClientContext(channelId, activePageName, containingPageName, nestedComponentId, eventType, topic, httpSession, pageContext);
	}

	private String getRequiredString(Map<String, Object> data, String key) {
		String value = (String) data.get(key);
		if (value == null) {
			throw new IllegalStateException(String.format("Required attribute %s not present", key));
		}
		return value;
	}
}
