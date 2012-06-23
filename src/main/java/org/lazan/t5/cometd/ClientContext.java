package org.lazan.t5.cometd;

import java.util.Map;

import org.cometd.bayeux.server.ServerMessage;


public class ClientContext {
    private final String activePageName, containingPageName, nestedComponentId, eventType, topic;
    private final boolean session;

	private ClientContext(boolean session, String activePageName, String containingPageName, String nestedComponentId, String eventType, String topic) {
		super();
		this.session = session;
		this.activePageName = activePageName;
		this.containingPageName = containingPageName;
		this.nestedComponentId = nestedComponentId;
		this.eventType = eventType;
		this.topic = topic;
	}

	public String getActivePageName() {
		return activePageName;
	}

	public String getContainingPageName() {
		return containingPageName;
	}

	public String getNestedComponentId() {
		return nestedComponentId;
	}

	public String getEventType() {
		return eventType;
	}
	
	public boolean isSession() {
		return session;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public static ClientContext fromMessage(ServerMessage message) {
		Map<String, Object> data = message.getDataAsMap();
		String activePageName = getRequiredString(data, "activePageName");
		String containingPageName = getRequiredString(data, "containingPageName");
		String nestedComponentId = (String) data.get("nestedComponentId");
		if (nestedComponentId == null) {
			nestedComponentId = "";
		}
		String eventType = getRequiredString(data, "eventType");
		boolean session = "true".equals(getRequiredString(data, "session"));
		String topic = getRequiredString(data, "topic");

		return new ClientContext(session, activePageName, containingPageName, nestedComponentId, eventType, topic);
	}

	private static String getRequiredString(Map<String, Object> data, String key) {
		String value = (String) data.get(key);
		if (value == null) {
			throw new IllegalStateException(String.format("Required attribute %s not present", key));
		}
		return value;
	}
}
