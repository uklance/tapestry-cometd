package org.lazan.t5.cometd.services.internal;

import java.util.Collection;
import java.util.Map;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.BayeuxServer.ChannelListener;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.lazan.t5.cometd.services.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;
import org.lazan.t5.cometd.services.ComponentJSONRenderer;
import org.lazan.t5.cometd.services.PushManager;
import org.slf4j.Logger;

public class PushManagerImpl implements PushManager {
	private final BayeuxServer bayeuxServer;
	private final ComponentJSONRenderer componentStringRenderer;
	private final TypeCoercer typeCoercer;
	private final CometdGlobals cometdGlobals;
	private final Logger logger;

	public PushManagerImpl(BayeuxServer bayeuxServer, ComponentJSONRenderer componentStringRenderer, TypeCoercer typeCoercer, CometdGlobals cometdGlobals, Logger logger) {
		this.bayeuxServer = bayeuxServer;
		this.bayeuxServer.addListener(new DisconnectListener());
		this.componentStringRenderer = componentStringRenderer;
		this.typeCoercer = typeCoercer;
		this.cometdGlobals = cometdGlobals;
		this.logger = logger;
	}

	// TODO: move this to separate class
	public class DisconnectListener implements ChannelListener {
		public void channelAdded(ServerChannel channel) {
		}

		/**
		 * Cleans up maps to avoid memory leaks TODO: There are race conditions
		 * which could cause this to result in an invalid state
		 */
		public void channelRemoved(String channelId) {
			cometdGlobals.removeChannel(channelId);
		}

		public void configureChannel(ConfigurableServerChannel channel) {
		}
	}

	public void broadcast(final String topic, Object... context) {
		Collection<String> channelIds = cometdGlobals.getChannelIds(topic);
		if (channelIds != null) {
			for (String channelId : channelIds) {
				ClientContext clientContext = cometdGlobals.getClientContext(channelId);
				if (clientContext == null) {
					logger.error("ClientContext not found for channelId {}", channelId);
				} else {
					deliver(clientContext, context);
				}
			}
		}
	}

	protected void deliver(ClientContext clientContext, Object[] pushContext) {
		EventContext eventContext = new ArrayEventContext(typeCoercer, pushContext);
		ComponentEventRequestParameters eventParams = new ComponentEventRequestParameters(
				clientContext.getActivePageName(),
				clientContext.getContainingPageName(),
				clientContext.getNestedComponentId(), clientContext.getEventType(),
				clientContext.getPageActivationContext(), 
				eventContext);


		ServerChannel channel = bayeuxServer.getChannel(clientContext.getChannelId());
		if (channel == null) {
			logger.error("Channel not found for channelId {}", clientContext.getChannelId());
		} else {
			JSONObject json = componentStringRenderer.render(eventParams, clientContext.getHttpSession());
			Map<String, Object> data = JSONUtils.unwrap(json);
			channel.publish(null, data, null);
		}
	}
}
