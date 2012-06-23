package org.lazan.t5.cometd.services;

import org.lazan.t5.cometd.ClientContext;

public interface SubscriptionListeners {
	public void onSubscribe(String topic, ClientContext context);
	public void onUnsubscribe(String topic, ClientContext context);
}