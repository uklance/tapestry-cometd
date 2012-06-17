package org.lazan.t5.cometd.web;

import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.annotations.UsesMappedConfiguration;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.CometdServlet;

@UsesMappedConfiguration(key=String.class, value=String.class)
public class CometdHttpServletRequestFilter extends ServletHttpServletRequestFilter implements BayeuxServletHttpServletRequestFilter {
	public static final String SERVLET_NAME = "cometd";
	
	private final BayeuxServer bayeuxServer;
	
	public CometdHttpServletRequestFilter(ApplicationGlobals applicationGlobals, Map<String, String> initParams, @Symbol("cometd.uriPattern") String uriPattern) {
		super(applicationGlobals, createServletFactory(initParams), uriPattern);
		this.bayeuxServer = (BayeuxServer) applicationGlobals.getServletContext().getAttribute(BayeuxServer.ATTRIBUTE);
	}
	
	public BayeuxServer getBayeuxServer() {
		return bayeuxServer;
	}
	
	protected static ServletFactory createServletFactory(final Map<String, String> initParams) {
		return new ServletFactory() {
			public HttpServlet createServlet() {
				return new CometdServlet();
			}
			public Map<String, String> getInitParams() {
				return initParams;
			}
			public String getServletName() {
				return SERVLET_NAME;
			}
		};
	}
}
