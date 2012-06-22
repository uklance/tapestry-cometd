package org.lazan.t5.cometd.services.internal;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;

public class CometdGlobalsImpl implements CometdGlobals {
	private final ConcurrentMap<String, Set<String>> channelIdsByTopic = new ConcurrentHashMap<String, Set<String>>();
	private final ConcurrentMap<String, ClientContext> clientContextByChannelId = new ConcurrentHashMap<String, ClientContext>();
	
	// TODO: synchronize nicely
	public void removeChannel(String channelId) {
		clientContextByChannelId.remove(channelId);
		// TODO: map lookup
		for (Set<String> topicChannelIds : channelIdsByTopic.values()) {
			topicChannelIds.remove(channelId);
		}
	}
	
	// TODO: synchronize nicely
	public void setClientContext(String topic, String channelId, ClientContext clientContext) {
		Set<String> channelIds = channelIdsByTopic.get(topic);
		if (channelIds == null) {
			// TODO: Collections.newSetFromMap() is a java 1.6 feature. Do I care?
			Set<String> tempChannelIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
			channelIds = channelIdsByTopic.putIfAbsent(topic, tempChannelIds);
			if (channelIds == null) {
				channelIds = tempChannelIds;
			}
		}
		channelIds.add(channelId);
		clientContextByChannelId.put(channelId, clientContext);
	}
	
	public Set<String> getChannelIds(String topic) {
		return channelIdsByTopic.get(topic);
	}
	
	public ClientContext getClientContext(String channelId) {
		return clientContextByChannelId.get(channelId);
	}
}
