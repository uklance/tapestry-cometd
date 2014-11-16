package org.lazan.t5.cometd.services;

public interface SubscriptionListeners extends org.cometd.bayeux.server.BayeuxServer.SubscriptionListener {
	/**
	 * Map a subscription listener to a topic pattern. The listener will be fired for all messages 
	 * sent to a topic that matches the provided topic pattern
	 * 
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels)
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 */
	public void addListener(String topic, SubscriptionListener listener);

	/**
	 * Remove the subscription listener mapped to a topic
	 * 
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels)
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 *
	 * @return true if the authorizer was removed, false otherwise
	 */
	public boolean removeListener(String topic, SubscriptionListener listener);
}
