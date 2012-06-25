package org.lazan.t5.cometd.services.internal;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;
import org.lazan.t5.cometd.services.SubscriptionListener;
import org.lazan.t5.cometd.services.SubscriptionListeners;

@UsesOrderedConfiguration(SubscriptionListener.class)
public class SubscriptionListenersImpl implements SubscriptionListeners {
	private final TopicMatchers<SubscriptionListener> listeners;
	private final CometdGlobals cometdGlobals;
	
	public SubscriptionListenersImpl(List<SubscriptionListener> list, CometdGlobals cometdGlobals) {
		listeners = new TopicMatchers<SubscriptionListener>();
		for (SubscriptionListener listener : list) {
			addListener(listener);
		}
		this.cometdGlobals = cometdGlobals;
	}
	
	public void addListener(SubscriptionListener listener) {
		listeners.addMatcher(listener.getTopicPattern(), listener);
	}
	
	public boolean removeListener(SubscriptionListener listener) {
		return listeners.removeMatcher(listener.getTopicPattern(), listener);
	}
	
	public void subscribed(ServerSession session, ServerChannel channel) {
		ClientContext clientContext = cometdGlobals.getClientContext(channel.getId());
		if (clientContext != null) {
			String topic = clientContext.getTopic();
			for (SubscriptionListener listener : listeners.getMatches(topic)) {
				listener.onSubscribe(clientContext);
			}
		}
	}
	
	public void unsubscribed(ServerSession session, ServerChannel channel) {
		ClientContext clientContext = cometdGlobals.getClientContext(channel.getId());
		if (clientContext != null) {
			String topic = clientContext.getTopic();
			for (SubscriptionListener listener : listeners.getMatches(topic)) {
				listener.onUnsubscribe(clientContext);
			}
		}
	}
}
