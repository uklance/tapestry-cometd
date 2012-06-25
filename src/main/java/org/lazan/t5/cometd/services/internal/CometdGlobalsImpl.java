package org.lazan.t5.cometd.services.internal;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;

public class CometdGlobalsImpl implements CometdGlobals {
	private final TopicMatchers<String> channelIdsByTopic = new TopicMatchers<String>();
	private final ConcurrentMap<String, ClientContext> clientContextByChannelId = new ConcurrentHashMap<String, ClientContext>();
	
	// TODO: synchronize nicely
	public void removeChannel(String channelId) {
		ClientContext clientContext = clientContextByChannelId.remove(channelId);
		if (clientContext != null) {
			channelIdsByTopic.removeMatcher(clientContext.getTopic(), channelId);
		}
	}
	
	// TODO: synchronize nicely
	public void setClientContext(String topic, String channelId, ClientContext clientContext) {
		channelIdsByTopic.addMatcher(topic, channelId);
		clientContextByChannelId.put(channelId, clientContext);
	}
	
	public Set<String> getChannelIds(String topic) {
		return channelIdsByTopic.getMatches(topic);
	}
	
	public ClientContext getClientContext(String channelId) {
		return clientContextByChannelId.get(channelId);
	}
}
