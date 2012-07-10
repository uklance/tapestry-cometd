package org.lazan.t5.cometd.services;

public interface SubscriptionListeners extends org.cometd.bayeux.server.BayeuxServer.SubscriptionListener {
	public void addListener(SubscriptionListener listener);
	public boolean removeListener(SubscriptionListener listener);
}
