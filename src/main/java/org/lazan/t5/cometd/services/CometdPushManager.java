package org.lazan.t5.cometd.services;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerChannel.MessageListener;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.slf4j.Logger;

public class CometdPushManager implements PushManager {
	private final BayeuxServer bayeuxServer;
	private final Logger logger;
	private final ComponentStringRenderer componentStringRenderer;
	private final TypeCoercer typeCoercer;
	private final HttpServletRequest request;
	private final ConcurrentMap<String, Set<String>> channelIdsByTopic = new ConcurrentHashMap<String, Set<String>>();
	private final ConcurrentMap<String, ClientContext> zoneContextByChannelId = new ConcurrentHashMap<String, ClientContext>();
	private static final EventContext EMPTY_EVENT_CONTEXT = new EmptyEventContext(); 
	
	public CometdPushManager(BayeuxServer bayeuxServer, Logger logger, ComponentStringRenderer componentStringRenderer, TypeCoercer typeCoercer, HttpServletRequest request) {
		this.bayeuxServer = bayeuxServer;
		this.bayeuxServer.getChannel("/service/pushInit").addListener(new PushInitListener());
		this.logger = logger;
		this.componentStringRenderer = componentStringRenderer;
		this.typeCoercer = typeCoercer;
		this.request = request;
	}

	public void broadcast(final String topic, Object[] context) {
		Collection<String> channelIds = channelIdsByTopic.get(topic);
		if (channelIds != null) {
			for (String channelId : channelIds) {
				ServerChannel channel = bayeuxServer.getChannel(channelId);
				if (channel != null) {
					ClientContext clientContext = zoneContextByChannelId.get(channelId);
					if (clientContext == null) {
						logger.error("ClientContext unknown for channelId {}", channelId);
					} else {
						ComponentEventRequestParameters eventParams = new ComponentEventRequestParameters(
								clientContext.getActivePageName(),
								clientContext.getContainingPageName(),
								clientContext.getNestedComponentId(),
								clientContext.getEventType(),
								EMPTY_EVENT_CONTEXT,
								new ArrayEventContext(typeCoercer, context)
						);
						if (clientContext.isSession()) {
							// component rendering requires the HttpSession so each subscriber requires it's own render of the component
							for (ServerSession subscriber : channel.getSubscribers()) {
								WeakReference<HttpSession> sessionRef = (WeakReference<HttpSession>) subscriber.getAttribute("sessionRef");
								HttpSession session = sessionRef == null ? null : sessionRef.get();
								if (session == null) {
									logger.error("No session reference for channelId {}, serverSession {}", channelId, subscriber.getId());
								} else {
									String html = componentStringRenderer.render(eventParams);
									Map<String, String> message = new HashMap<String, String>();
									message.put("content", html);
									subscriber.deliver(null, channelId, message, null);
								}
							}
						} else {
							// HttpSession not required, all subscribers share the same message
							Map<String, String> message = new HashMap<String, String>();
							String html = componentStringRenderer.render(eventParams);
							message.put("content", html);
							channel.publish(null, message, null);
						}
					}
				}
			}
		}
	}
	
	public void service(final String clientId, Object[] context) {
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
			Set<String> channelIds = channelIdsByTopic.get(topic);
			if (channelIds == null) {
				Set<String> tempChannelIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
				channelIds = channelIdsByTopic.putIfAbsent(topic, tempChannelIds);
			}
			channelIds.add(channel.getId());
			
			if (!zoneContextByChannelId.containsKey(channelId)) {
				String activePageName = getRequiredString(data, "activePageName");
				String containingPageName = getRequiredString(data, "containingPageName");
				String nestedComponentId = getRequiredString(data, "nestedComponentId");
				String eventType = getRequiredString(data, "eventType");
				
				ClientContext zoneContext = new ClientContext(session, activePageName, containingPageName, nestedComponentId, eventType);
				zoneContextByChannelId.put(channelId, zoneContext);
			}
			if (session) {
				WeakReference<HttpSession> sessionRef = new WeakReference<HttpSession>(request.getSession());
				serverSession.setAttribute("sessionRef", sessionRef);
			}
			return true;
		}
	}
}
