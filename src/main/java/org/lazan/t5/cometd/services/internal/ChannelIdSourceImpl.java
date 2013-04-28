package org.lazan.t5.cometd.services.internal;

import java.util.concurrent.atomic.AtomicInteger;

import org.lazan.t5.cometd.services.ChannelIdSource;
import org.lazan.t5.cometd.services.CometdConstants;

public class ChannelIdSourceImpl implements ChannelIdSource {
	private static final AtomicInteger NEXT_SUFFIX = new AtomicInteger(1);
	
	@Override
	public String nextChannelId(String topic) {
		return String.format("%s%s/%s", CometdConstants.CHANNEL_PREFIX, topic.replace('*', 'x'), NEXT_SUFFIX.getAndIncrement());
	}
}
