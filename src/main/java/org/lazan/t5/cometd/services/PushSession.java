package org.lazan.t5.cometd.services;

import java.util.Set;

public interface PushSession {
	public String getTopic();
	public <T> T getAttribute(String name, Class<T> type);
	public void setAttribute(String name, Object value);
	public Set<String> getAttributeNames();
}
