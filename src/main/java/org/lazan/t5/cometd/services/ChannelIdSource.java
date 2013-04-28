package org.lazan.t5.cometd.services;


public interface ChannelIdSource {
	String nextChannelId(String topic);
}
