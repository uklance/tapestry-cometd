package org.lazan.t5.cometd.internal;


public class ClientContext {
    private final String activePageName, containingPageName, nestedComponentId, eventType, topic;
    private final boolean session;

	public ClientContext(boolean session, String activePageName, String containingPageName, String nestedComponentId, String eventType, String topic) {
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
}
