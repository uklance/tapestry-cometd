package org.lazan.t5.cometd.services;

public class AuthorizerContribution {
	private final String topic;
	private final Authorizer authorizer;
	
	/**
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 */
	public AuthorizerContribution(String topic, Authorizer authorizer) {
		super();
		this.topic = topic;
		this.authorizer = authorizer;
	}
	public String getTopic() {
		return topic;
	}
	public Authorizer getAuthorizer() {
		return authorizer;
	}
}
