package org.lazan.t5.cometd.services;

import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.cometd.bayeux.server.BayeuxServer;

public interface CometdHttpServletRequestFilter extends HttpServletRequestFilter {
	public BayeuxServer getBayeuxServer();
}
