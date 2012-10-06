package org.lazan.t5.cometd.services.internal;

import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.internal.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;
import org.lazan.t5.cometd.services.PushSession;
import org.lazan.t5.cometd.services.SubscriptionListener;
import org.lazan.t5.cometd.services.SubscriptionListenerContribution;
import org.lazan.t5.cometd.services.SubscriptionListeners;

@UsesOrderedConfiguration(SubscriptionListenerContribution.class)
public class SubscriptionListenersImpl implements SubscriptionListeners {
	private final TopicMatchers<SubscriptionListener> listeners;
	private final CometdGlobals cometdGlobals;
	
	public SubscriptionListenersImpl(List<SubscriptionListenerContribution> contributions, CometdGlobals cometdGlobals) {
		listeners = new TopicMatchers<SubscriptionListener>();
		for (SubscriptionListenerContribution contribution : contributions) {
			addListener(contribution.getTopic(), contribution.getSubscriptionListener());
		}
		this.cometdGlobals = cometdGlobals;
	}
	
	public void addListener(String topic, SubscriptionListener listener) {
		listeners.addMatcher(topic, listener);
	}
	
	public boolean removeListener(String topic, SubscriptionListener listener) {
		return listeners.removeMatcher(topic, listener);
	}
	
	public void subscribed(ServerSession serverSession, ServerChannel channel) {
		ClientContext clientContext = cometdGlobals.getClientContext(channel.getId());
		if (clientContext != null) {
			String topic = clientContext.getTopic();
			Set<SubscriptionListener> matches = listeners.getMatches(topic);
			if (!matches.isEmpty()) {
				PushSession pushSession = new PushSessionImpl(serverSession, clientContext);
				for (SubscriptionListener listener : matches) {
					listener.onSubscribe(pushSession);
				}
			}
		}
	}
	
	public void unsubscribed(ServerSession serverSession, ServerChannel channel) {
		ClientContext clientContext = cometdGlobals.getClientContext(channel.getId());
		if (clientContext != null) {
			String topic = clientContext.getTopic();
			Set<SubscriptionListener> matches = listeners.getMatches(topic);
			if (!matches.isEmpty()) {
				PushSession pushSession = new PushSessionImpl(serverSession, clientContext);
				for (SubscriptionListener listener : matches) {
					listener.onUnsubscribe(pushSession);
				}
			}
		}
	}
}
