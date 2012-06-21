package org.lazan.t5.cometd.services;

import org.lazan.t5.cometd.ClientContext;

public interface Authorizer {
	String getTopic();
	boolean isAuthorized(ClientContext clientContext);
}
