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
import org.lazan.t5.cometd.services.internal.CometdHttpServletRequestFilterImpl;
import org.lazan.t5.cometd.services.internal.ComponentJSONRendererImpl;
import org.lazan.t5.cometd.services.internal.PushManagerImpl;
import org.lazan.t5.cometd.services.internal.SubscriptionListenersImpl;
import org.slf4j.Logger;

public class CometdModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(CometdHttpServletRequestFilter.class, CometdHttpServletRequestFilterImpl.class);
		binder.bind(PushManager.class, PushManagerImpl.class).eagerLoad();
		binder.bind(ComponentJSONRenderer.class, ComponentJSONRendererImpl.class);
		binder.bind(ChannelIdSource.class, ChannelIdSourceImpl.class);
		binder.bind(CometdGlobals.class, CometdGlobalsImpl.class);
		binder.bind(Authorizers.class, AuthorizersImpl.class);
		binder.bind(SubscriptionListeners.class, SubscriptionListenersImpl.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> config) {
		config.add("cometd.uri", "/cometd");
	}

	public static void contributeHttpServletRequestHandler(
			OrderedConfiguration<HttpServletRequestFilter> configuration,
			CometdHttpServletRequestFilter cometdFilter)
	{
		configuration.add("cometd", cometdFilter);
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration, Logger log)
	{
		log.info("Registering cometd component library");
		configuration.add(new LibraryMapping("cometd", "org.lazan.t5.cometd"));
	}

	public static void contributeCometdHttpServletRequestFilter(MappedConfiguration<String, Object> config, SymbolSource symbolSource) {
		// add init-params for the Cometd servlet here
		config.add("logLevel", "2");
		config.add("transports", "org.cometd.websocket.server.WebSocketTransport");
		config.add("org.atmosphere.useStream", "false");
	}

	public static BayeuxServer buildBayeuxServer(
			CometdHttpServletRequestFilter cometdFilter,
			Authorizers authorizers, SubscriptionListeners subscriptionListeners, CometdGlobals cometdGlobals)
	{
		BayeuxServer bayeuxServer = cometdFilter.getBayeuxServer();
		bayeuxServer.createIfAbsent("/push-target/**");
		ServerChannel channel = bayeuxServer.getChannel("/push-target/**");
		channel.addAuthorizer(authorizers);
		bayeuxServer.addListener(subscriptionListeners);
		return bayeuxServer;
	}
}
