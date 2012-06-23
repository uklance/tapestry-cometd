package org.lazan.t5.cometd.services.internal;

import org.cometd.bayeux.server.BayeuxServer.SubscriptionListener;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;
import org.lazan.t5.cometd.services.SubscriptionListeners;

public class BayeuxServerSubscriptionListener implements SubscriptionListener {
	private final SubscriptionListeners listeners;
	private final CometdGlobals cometdGlobals;

	public BayeuxServerSubscriptionListener(SubscriptionListeners listeners,
			CometdGlobals cometdGlobals) {
		super();
		this.listeners = listeners;
		this.cometdGlobals = cometdGlobals;
	}

	public void subscribed(ServerSession session, ServerChannel channel) {
		ClientContext clientContext = cometdGlobals.getClientContext(channel.getId());
		if (clientContext != null) {
			listeners.onSubscribe(clientContext.getTopic(), clientContext);
		}
	}
	
	public void unsubscribed(ServerSession session, ServerChannel channel) {
		ClientContext clientContext = cometdGlobals.getClientContext(channel.getId());
		if (clientContext != null) {
			listeners.onUnsubscribe(clientContext.getTopic(), clientContext);
		}
	}
}
