package org.lazan.t5.cometd.services.internal;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.TopicMatchers;
import org.lazan.t5.cometd.services.SubscriptionListener;
import org.lazan.t5.cometd.services.SubscriptionListeners;

@UsesOrderedConfiguration(SubscriptionListener.class)
public class SubscriptionListenersImpl implements SubscriptionListeners {
	private final TopicMatchers<SubscriptionListener> listeners;
	
	public SubscriptionListenersImpl(List<SubscriptionListener> list) {
		listeners = new TopicMatchers<SubscriptionListener>();
		for (SubscriptionListener listener : list) {
			listeners.add(listener.getTopicPattern(), listener);
		}
		
	}
	public void onSubscribe(String topic, ClientContext context) {
		for (SubscriptionListener listener : listeners.getMatches(topic)) {
			listener.onSubscribe(topic, context);
		}
	}

	public void onUnsubscribe(String topic, ClientContext context) {
		for (SubscriptionListener listener : listeners.getMatches(topic)) {
			listener.onUnsubscribe(topic, context);
		}
	}

}
