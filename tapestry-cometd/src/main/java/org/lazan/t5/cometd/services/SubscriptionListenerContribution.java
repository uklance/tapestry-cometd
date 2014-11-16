package org.lazan.t5.cometd.services;

public class SubscriptionListenerContribution {
	private String topic;
	private SubscriptionListener subscriptionListener;
	
	/**
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 */
	public SubscriptionListenerContribution(String topic, SubscriptionListener subscriptionListener) {
		super();
		this.topic = topic;
		this.subscriptionListener = subscriptionListener;
	}
	
	public String getTopic() {
		return topic;
	}

	public SubscriptionListener getSubscriptionListener() {
		return subscriptionListener;
	}
}
