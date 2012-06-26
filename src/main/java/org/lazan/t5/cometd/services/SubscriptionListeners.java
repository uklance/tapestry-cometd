package org.lazan.t5.cometd.services;

import org.cometd.bayeux.server.BayeuxServer.SubscriptionListener;

public interface SubscriptionListeners extends SubscriptionListener {
	public void addListener(org.lazan.t5.cometd.services.SubscriptionListener listener);
	public boolean removeListener(org.lazan.t5.cometd.services.SubscriptionListener listener);
}
