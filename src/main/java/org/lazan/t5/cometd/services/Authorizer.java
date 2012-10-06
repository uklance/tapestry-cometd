package org.lazan.t5.cometd.services;


public interface Authorizer {
	boolean isAuthorized(PushSession pushSession);
}
