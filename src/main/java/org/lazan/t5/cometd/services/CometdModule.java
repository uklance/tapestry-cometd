package org.lazan.t5.cometd.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.LibraryMapping;
import org.cometd.bayeux.server.BayeuxServer;
import org.lazan.t5.cometd.web.BayeuxServletHttpServletRequestFilter;
import org.lazan.t5.cometd.web.CometdHttpServletRequestFilter;
import org.slf4j.Logger;

public class CometdModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(BayeuxServletHttpServletRequestFilter.class, CometdHttpServletRequestFilter.class);
		binder.bind(PushManager.class, CometdPushManager.class).eagerLoad();
		binder.bind(ComponentStringRenderer.class, ComponentStringRendererImpl.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> config) {
		config.add("cometd.uriPattern", "/cometd(/.*)?");
	}
	
	public static void contributeHttpServletRequestHandler(
			OrderedConfiguration<HttpServletRequestFilter> configuration,
			BayeuxServletHttpServletRequestFilter cometdHttpServletRequestFilter)
	{
		configuration.add("cometd", cometdHttpServletRequestFilter);
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> config) {
	}
	
	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration, Logger log) {
        log.info("Registering cometd component library");
        configuration.add(new LibraryMapping("cometd", "org.lazan.t5.cometd"));
    }	
	
	public static void contributeBayeuxServletHttpServletRequestFilter(MappedConfiguration<String, Object> config, SymbolSource symbolSource) {
		// add init-params for the Cometd servlet here
		config.add("logLevel", "3");
	}
	
	public static BayeuxServer buildBayeuxServer(BayeuxServletHttpServletRequestFilter cometdHttpServletRequestFilter) {
		return cometdHttpServletRequestFilter.getBayeuxServer();
	}
}
