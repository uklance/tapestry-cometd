package org.lazan.t5.cometd.services;


public interface Authorizer {
	/**
	 * @return A topic which may end in a wildcard (either "*" to match a single level or "**" to match all levels
	 *         /chat/cars/porsche will match /chat/cars/porsche
	 *         /chat/* will match /chat/cars and /chat/java
	 *         /chat/** will match /chat/cars and /chat/cars/porsche
	 */
	String getTopic();
	
	boolean isAuthorized(PushSession pushSession);
}
