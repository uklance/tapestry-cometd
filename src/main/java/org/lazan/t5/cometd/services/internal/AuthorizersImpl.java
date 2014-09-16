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

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.BayeuxContext;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.transport.HttpTransport;
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
	private final ValueEncoderSource valueEncoderSource;
	private final BayeuxServer bayeuxServer;
	
	public AuthorizersImpl(List<AuthorizerContribution> contributions, CometdGlobals cometdGlobals, HttpServletRequest request, 
			ValueEncoderSource valueEncoderSource, BayeuxServer bayeuxServer)
	{
		super();
		this.authorizers = new TopicMatchers<Authorizer>();
		this.cometdGlobals = cometdGlobals;
		this.valueEncoderSource = valueEncoderSource;
		this.bayeuxServer = bayeuxServer;
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
			
			PushSession pushSession = new PushSessionImpl(serverSession, clientContext);
			String topic = clientContext.getTopic();
			for (Authorizer auth : authorizers.getMatches(topic)) {
				if (!auth.isAuthorized(pushSession)) {
					return Result.deny("Authorization failure");
				}
			}
			cometdGlobals.setClientContext(clientContext.getChannelId(), clientContext);
		}
		return Result.grant();
	}
	
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
		String[] activationContext = toStringArray(pageContextObj);
		EventContext pageContext = EMPTY_EVENT_CONTEXT;
		if (activationContext != null && activationContext.length > 0) {
			pageContext = new StringEventContext(valueEncoderSource, activationContext);
		}
		
		HttpTransport transport = (HttpTransport) bayeuxServer.getCurrentTransport();
		BayeuxContext bayeuxContext = transport.getContext();
		Session session = new BayeuxContextSession(bayeuxContext);

		return new ClientContext(channelId, activePageName, containingPageName, nestedComponentId, eventType, topic, session, pageContext);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String[] toStringArray(Object data) {
		if (data == null) {
			return null;
		}
		String[] stringArray;
		if (data instanceof Collection) {
			Collection pageContextColl = (Collection) data;
			stringArray = (String[]) pageContextColl.toArray(new String[pageContextColl.size()]);
		} else {
			Object[] pageContextArr = (Object[]) data;
			stringArray = new String[pageContextArr.length];
			System.arraycopy(data, 0, stringArray, 0, pageContextArr.length);
		}
		return stringArray;
	}

	private String getRequiredString(Map<String, Object> data, String key) {
		String value = (String) data.get(key);
		if (value == null) {
			throw new IllegalStateException(String.format("Required attribute %s not present", key));
		}
		return value;
	}
}
