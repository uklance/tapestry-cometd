package org.lazan.t5.cometddemo.services.internal;

import org.lazan.t5.cometd.services.Authorizer;
import org.lazan.t5.cometd.services.PushSession;

public class LoggingAuthorizer implements Authorizer {
	public boolean isAuthorized(PushSession pushSession) {
		System.err.println(String.format("isAuthorized(%s)", pushSession.getTopic()));
		return true;
	}
}
