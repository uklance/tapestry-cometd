package org.lazan.t5.cometd.services;

public interface Authorizers extends org.cometd.bayeux.server.Authorizer {
	/**
	 * Map an authorizer to a topic pattern. The authorizer will be fired for any subscriptions 
	 * attempt to a topic that matches the provided topic pattern
	 * 
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels)
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 *
	 * @param auth An authorizer
	 */
	public void addAuthorizer(String topic, Authorizer auth);

	/**
	 * Remove the authorizer mapped to a topic
	 * 
	 * @param topic	A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels)
	 * 					/chat/cars/porsche will match /chat/cars/porsche
	 *					/chat/* will match /chat/cars and /chat/java
	 *					/chat/** will match /chat/cars and /chat/cars/porsche
	 *
	 * @param auth The authorizer to remove
	 * 
	 * @return true if the authorizer was removed, false otherwise
	 */
	public boolean removeAuthorizer(String topic, Authorizer auth);
}
