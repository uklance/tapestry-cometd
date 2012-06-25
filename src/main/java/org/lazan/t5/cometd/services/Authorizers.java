package org.lazan.t5.cometd.services;

import org.cometd.bayeux.server.Authorizer;


public interface Authorizers extends Authorizer {
	public void addAuthorizer(org.lazan.t5.cometd.services.Authorizer auth);
	public boolean removeAuthorizer(org.lazan.t5.cometd.services.Authorizer auth);
}
