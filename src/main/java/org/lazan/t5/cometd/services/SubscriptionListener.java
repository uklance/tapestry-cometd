package org.lazan.t5.cometd.services;

import org.lazan.t5.cometd.ClientContext;

public interface SubscriptionListener {
	/**
	 * @return A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels
	 *         /chat/cars/porsche will match /chat/cars/porsche
	 *         /chat/* will match /chat/cars and /chat/java
	 *         /chat/** will match /chat/cars and /chat/cars/porsche
	 */
	public String getTopic();

	public void onSubscribe(ClientContext context);

	public void onUnsubscribe(ClientContext context);
}
