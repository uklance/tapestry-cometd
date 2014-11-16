package org.lazan.t5.cometddemo.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.lazan.t5.cometd.services.AuthorizerContribution;
import org.lazan.t5.cometd.services.CometdModule;
import org.lazan.t5.cometd.services.SubscriptionListenerContribution;
import org.lazan.t5.cometddemo.services.internal.ChatServiceImpl;
import org.lazan.t5.cometddemo.services.internal.ChatSubscriptionListener;
import org.lazan.t5.cometddemo.services.internal.LoggingAuthorizer;
import org.lazan.t5.cometddemo.services.internal.LoggingSubscriptionListener;
import org.lazan.t5.cometddemo.services.internal.StockServiceImpl;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule(CometdModule.class)
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(StockService.class, StockServiceImpl.class);
		binder.bind(ChatService.class, ChatServiceImpl.class);
	}
	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.override(SymbolConstants.APPLICATION_VERSION, "1.0-SNAPSHOT");
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
	}
	
	/*
	// override the core javascript stack to include jquery
    public static void contributeJavaScriptStackSource(MappedConfiguration<String, JavaScriptStack> configuration) {
        configuration.overrideInstance(InternalConstants.CORE_STACK_NAME, ExtendedCoreJavascriptStack.class);
    }
	*/

	public static void contributeAuthorizers(
			OrderedConfiguration<AuthorizerContribution> config,
			@Autobuild LoggingAuthorizer loggingAuthorizer)
	{
		AuthorizerContribution contribution = new AuthorizerContribution("/**", loggingAuthorizer);
		config.add("log", contribution);
	}

	public static void contributeSubscriptionListeners(
			OrderedConfiguration<SubscriptionListenerContribution> config,
			@Autobuild LoggingSubscriptionListener loggingListener,
			@Autobuild ChatSubscriptionListener chatListener,
			ChatService chatService) {
		
		config.add("log", new SubscriptionListenerContribution("/**", loggingListener));
		config.add("chat", new SubscriptionListenerContribution(chatService.getChatTopic(), chatListener));
	}
}
