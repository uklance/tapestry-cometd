package org.lazan.t5.cometd.services;

public interface Authorizers extends org.cometd.bayeux.server.Authorizer {
	public void addAuthorizer(Authorizer auth);
	public boolean removeAuthorizer(Authorizer auth);
}
