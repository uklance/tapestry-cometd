package org.lazan.t5.cometd.services.internal;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.BayeuxServer.ChannelListener;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.internal.ClientContext;
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
	private static final EventContext EMPTY_EVENT_CONTEXT = new EmptyEventContext();

	public PushManagerImpl(BayeuxServer bayeuxServer, Logger logger,
			ComponentJsonRenderer componentStringRenderer, TypeCoercer typeCoercer,
			HttpServletRequest request, CometdGlobals cometdGlobals) {
		this.bayeuxServer = bayeuxServer;
		this.bayeuxServer.addListener(new DisconnectListener());
		this.logger = logger;
		this.componentStringRenderer = componentStringRenderer;
		this.typeCoercer = typeCoercer;
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
								clientContext.getActivePageName(),
								clientContext.getContainingPageName(),
								clientContext.getNestedComponentId(), clientContext.getEventType(),
								EMPTY_EVENT_CONTEXT, new ArrayEventContext(typeCoercer, context));
						if (clientContext.isSession()) {
							deliverSessionMessages(channel, eventParams);
						} else {
							// HttpSession not required, all subscribers share the same message
							deliver(channel, eventParams, null);
						}
					}
				}
			}
		}
	}

	private void deliver(ServerChannel channel, ComponentEventRequestParameters eventParams, HttpSession httpSession) {
		JSONObject json = componentStringRenderer.render(eventParams, httpSession);
		Map<String, Object> data = unwrapJsonObject(json);
		channel.publish(null, data, null);
	}

	@SuppressWarnings("unchecked")
	private void deliverSessionMessages(ServerChannel channel,
			ComponentEventRequestParameters eventParams) {
		// component rendering requires the HttpSession so each subscriber
		// requires it's own render of the component
		for (ServerSession subscriber : channel.getSubscribers()) {
			WeakReference<HttpSession> sessionRef = (WeakReference<HttpSession>) subscriber
					.getAttribute(PushConstants.ATTRIBUTE_SESSION_REFERENCE);
			HttpSession httpSession = sessionRef == null ? null : sessionRef.get();
			if (httpSession == null) {
				logger.error("No session reference for channelId {}, serverSession {}",
						channel.getId(), subscriber.getId());
			} else {
				deliver(channel, eventParams, httpSession);
			}
		}
	}
	
	protected Map<String, Object> unwrapJsonObject(JSONObject json) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (String key : json.keys()) {
			Object value = json.get(key);
			map.put(key, unwrapObject(value));
		}
		return map;
	}
	
	protected Object unwrapObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof JSONObject) {
			return unwrapJsonObject((JSONObject) value);
		}
		if (value instanceof JSONArray) {
			JSONArray jsonArr = (JSONArray) value;
			Object[] arr = new Object[jsonArr.length()];
			for (int i = 0; i < arr.length; ++ i) {
				arr[i] = unwrapObject(jsonArr.get(i));
			}
			return arr;
		}
		return value;
	}

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
}
