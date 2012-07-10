package org.lazan.t5.cometd.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.LibraryMapping;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerChannel;
import org.lazan.t5.cometd.services.internal.AuthorizersImpl;
import org.lazan.t5.cometd.services.internal.ChannelIdSourceImpl;
import org.lazan.t5.cometd.services.internal.CometdGlobalsImpl;
import org.lazan.t5.cometd.services.internal.ComponentJsonRendererImpl;
import org.lazan.t5.cometd.services.internal.PushManagerImpl;
import org.lazan.t5.cometd.services.internal.SubscriptionListenersImpl;
import org.lazan.t5.cometd.web.BayeuxServerHttpServletRequestFilter;
import org.lazan.t5.cometd.web.CometdHttpServletRequestFilter;
import org.slf4j.Logger;

public class CometdModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(BayeuxServerHttpServletRequestFilter.class,
				CometdHttpServletRequestFilter.class);
		binder.bind(PushManager.class, PushManagerImpl.class).eagerLoad();
		binder.bind(ComponentJsonRenderer.class, ComponentJsonRendererImpl.class);
		binder.bind(ChannelIdSource.class, ChannelIdSourceImpl.class);
		binder.bind(CometdGlobals.class, CometdGlobalsImpl.class);
		binder.bind(Authorizers.class, AuthorizersImpl.class);
		binder.bind(SubscriptionListeners.class, SubscriptionListenersImpl.class);

	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> config) {
		config.add("cometd.uriPattern", "/cometd(/.*)?");
	}

	public static void contributeHttpServletRequestHandler(
			OrderedConfiguration<HttpServletRequestFilter> configuration,
			BayeuxServerHttpServletRequestFilter cometdHttpServletRequestFilter) {
		configuration.add("cometd", cometdHttpServletRequestFilter);
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> config) {
	}

	public static void contributeComponentClassResolver(
			Configuration<LibraryMapping> configuration, Logger log) {
		log.info("Registering cometd component library");
		configuration.add(new LibraryMapping("cometd", "org.lazan.t5.cometd"));
	}

	public static void contributeBayeuxServerHttpServletRequestFilter(
			MappedConfiguration<String, Object> config, SymbolSource symbolSource) {
		// add init-params for the Cometd servlet here
		config.add("logLevel", "2");
		config.add("transports", "org.cometd.websocket.server.WebSocketTransport");
	}

	public static BayeuxServer buildBayeuxServer(
			BayeuxServerHttpServletRequestFilter cometdHttpServletRequestFilter,
			Authorizers authorizers, SubscriptionListeners subscriptionListeners, CometdGlobals cometdGlobals) {
		BayeuxServer bayeuxServer = cometdHttpServletRequestFilter.getBayeuxServer();
		bayeuxServer.createIfAbsent("/push-target/**");
		ServerChannel channel = bayeuxServer.getChannel("/push-target/**");
		channel.addAuthorizer(authorizers);
		bayeuxServer.addListener(subscriptionListeners);
		return bayeuxServer;
	}
}
