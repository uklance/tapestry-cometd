package org.lazan.t5.cometd.services;

import org.lazan.t5.cometd.ClientContext;

public interface SubscriptionListener {
	public void onSubscribe(ClientContext context);

	public void onUnsubscribe(ClientContext context);

	public String getTopicPattern();
}
