package org.lazan.t5.cometd.web;

import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.cometd.bayeux.server.BayeuxServer;

public interface BayeuxServletHttpServletRequestFilter extends HttpServletRequestFilter {
	public BayeuxServer getBayeuxServer();
}
