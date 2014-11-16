package org.lazan.t5.cometd.services;

public interface CometdConstants {
	public static final String CHANNEL_PREFIX = "/push-target";
	public static final String CHANNEL_WILDCARD = CHANNEL_PREFIX + "/**";
	public static final String ATTRIBUTE_PUSH_SESSION = "push-session";
	
	public static final String DATA_CHANNEL_ID = "channelId";
	public static final String DATA_ACTIVE_PAGE_NAME = "activePageName";
	public static final String DATA_CONTAINING_PAGE_NAME = "containingPageName";
	public static final String DATA_NESTED_COMPONENT_ID = "nestedComponentId";
	public static final String DATA_EVENT_TYPE = "eventType";
	public static final String DATA_TOPIC = "topic";
	public static final String DATA_PAGE_ACTIVATION_CONTEXT = "pageActivationContext";
}
