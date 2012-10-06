package org.lazan.t5.cometd.services;


public interface SubscriptionListener {
	public void onSubscribe(PushSession pushSession);
	public void onUnsubscribe(PushSession pushSession);
}
