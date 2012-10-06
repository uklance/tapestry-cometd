package org.lazan.t5.cometd.services;

public interface SubscriptionListeners extends org.cometd.bayeux.server.BayeuxServer.SubscriptionListener {
	/**
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 */
	public void addListener(String topic, SubscriptionListener listener);

	/**
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 */
	public boolean removeListener(String topic, SubscriptionListener listener);
}
