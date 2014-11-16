package org.lazan.t5.cometd.services.internal;

import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.annotations.UsesMappedConfiguration;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.CometdServlet;
import org.lazan.t5.cometd.services.CometdHttpServletRequestFilter;
import org.lazan.t5.cometd.services.ServletSource;

@UsesMappedConfiguration(key = String.class, value = String.class)
public class CometdHttpServletRequestFilterImpl extends ServletHttpServletRequestFilter implements CometdHttpServletRequestFilter {
	public static final String SERVLET_NAME = "cometd";

	private final BayeuxServer bayeuxServer;

	public CometdHttpServletRequestFilterImpl(
			RegistryShutdownHub registryShutdownHub,

			ApplicationGlobals applicationGlobals,

			@Symbol("cometd.uri")
			String uri,
			
			Map<String, String> initParams)
	{
		super(createServletSource(uri, initParams), applicationGlobals, registryShutdownHub);
		this.bayeuxServer = (BayeuxServer) applicationGlobals.getServletContext().getAttribute(BayeuxServer.ATTRIBUTE);
	}

	public BayeuxServer getBayeuxServer() {
		return bayeuxServer;
	}

	protected static ServletSource createServletSource(final String uri, final Map<String, String> initParams) {
		return new ServletSource() {
			public HttpServlet createServlet() {
				return new CometdServlet();
			}

			public Map<String, String> getInitParams() {
				return initParams;
			}

			public String getServletName() {
				return SERVLET_NAME;
			}
			
			@Override
			public String getURI() {
				return uri;
			}
		};
	}
}
