package org.lazan.t5.cometd.services.internal;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.BayeuxServer.ChannelListener;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerChannel.MessageListener;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;
import org.lazan.t5.cometd.services.ComponentJsonRenderer;
import org.lazan.t5.cometd.services.PushManager;
import org.slf4j.Logger;

public class PushManagerImpl implements PushManager {
	private final BayeuxServer bayeuxServer;
	private final Logger logger;
	private final ComponentJsonRenderer componentStringRenderer;
	private final TypeCoercer typeCoercer;
	private final CometdGlobals cometdGlobals;
	private final HttpServletRequest request;
	private static final EventContext EMPTY_EVENT_CONTEXT = new EmptyEventContext();
	private static final String INIT_CHANNEL = "/service/pushInit";

	public PushManagerImpl(BayeuxServer bayeuxServer, Logger logger, ComponentJsonRenderer componentStringRenderer,
			TypeCoercer typeCoercer, HttpServletRequest request, CometdGlobals cometdGlobals) {
		this.bayeuxServer = bayeuxServer;
		this.bayeuxServer.createIfAbsent(INIT_CHANNEL);
		this.bayeuxServer.getChannel(INIT_CHANNEL).addListener(new PushInitListener());
		this.bayeuxServer.addListener(new DisconnectListener());
		this.logger = logger;
		this.componentStringRenderer = componentStringRenderer;
		this.typeCoercer = typeCoercer;
		this.request = request;
		this.cometdGlobals = cometdGlobals;
	}

	public void broadcast(final String topic, Object... context) {
		Collection<String> channelIds = cometdGlobals.getChannelIds(topic);
		if (channelIds != null) {
			for (String channelId : channelIds) {
				ServerChannel channel = bayeuxServer.getChannel(channelId);
				if (channel != null) {
					ClientContext clientContext = cometdGlobals.getClientContext(channelId);
					if (clientContext == null) {
						logger.error("ClientContext unknown for channelId {}", channelId);
					} else {
						ComponentEventRequestParameters eventParams = new ComponentEventRequestParameters(
								clientContext.getActivePageName(), clientContext.getContainingPageName(),
								clientContext.getNestedComponentId(), clientContext.getEventType(), EMPTY_EVENT_CONTEXT,
								new ArrayEventContext(typeCoercer, context));
						if (clientContext.isSession()) {
							deliverSessionMessages(channel, eventParams);
						} else {
							deliverNonSessionMessages(channel, eventParams);
						}
					}
				}
			}
		} else {
			logger.info("No channels for {}", topic);
		}
	}

	private void deliverNonSessionMessages(ServerChannel channel, ComponentEventRequestParameters eventParams) {
		// HttpSession not required, all subscribers share the same message
		JSONObject json = componentStringRenderer.render(eventParams);
		Map<String, String> message = new HashMap<String, String>();
		message.put("content", json.getString("content"));
		channel.publish(null, message, null);
	}

	private void deliverSessionMessages(ServerChannel channel, ComponentEventRequestParameters eventParams) {
		// component rendering requires the HttpSession so each subscriber
		// requires it's own render of the component
		for (ServerSession subscriber : channel.getSubscribers()) {
			WeakReference<HttpSession> sessionRef = (WeakReference<HttpSession>) subscriber.getAttribute("sessionRef");
			HttpSession session = sessionRef == null ? null : sessionRef.get();
			if (session == null) {
				logger.error("No session reference for channelId {}, serverSession {}", channel.getId(), subscriber.getId());
			} else {
				JSONObject json = componentStringRenderer.render(eventParams);
				Map<String, String> message = new HashMap<String, String>();
				message.put("content", json.getString("content"));
				subscriber.deliver(null, channel.getId(), message, null);
			}
		}
	}

	public void service(final String clientId, Object... context) {
		throw new UnsupportedOperationException();
	}

	private String getRequiredString(Map<String, Object> data, String key) {
		String value = (String) data.get(key);
		if (value == null) {
			throw new IllegalStateException(String.format("Required attribute %s not present", key));
		}
		return value;
	}

	public class PushInitListener implements MessageListener {
		public boolean onMessage(ServerSession serverSession, ServerChannel channel, Mutable message) {
			Map<String, Object> data = message.getDataAsMap();
			boolean session = "true".equals(getRequiredString(data, "session"));
			String channelId = getRequiredString(data, "channelId");
			String topic = getRequiredString(data, "topic");

			String activePageName = getRequiredString(data, "activePageName");
			String containingPageName = getRequiredString(data, "containingPageName");
			String nestedComponentId = (String) data.get("nestedComponentId");
			if (nestedComponentId == null) {
				nestedComponentId = "";
			}
			String eventType = getRequiredString(data, "eventType");

			ClientContext clientContext = new ClientContext(session, activePageName, containingPageName, nestedComponentId, eventType);
			cometdGlobals.setClientContext(topic, channelId, clientContext);

			if (session) {
				WeakReference<HttpSession> sessionRef = new WeakReference<HttpSession>(request.getSession());
				serverSession.setAttribute("sessionRef", sessionRef);
			}
			return true;
		}
	}

	public class DisconnectListener implements ChannelListener {
		public void channelAdded(ServerChannel channel) {
		}

		/**
		 * Cleans up maps to avoid memory leaks TODO: There are race conditions
		 * which could cause this to result in an invalid state
		 */
		public void channelRemoved(String channelId) {
			logger.info("Cleaning up channel {}", channelId);
			cometdGlobals.removeChannel(channelId);
		}

		public void configureChannel(ConfigurableServerChannel channel) {
		}
	}
}
