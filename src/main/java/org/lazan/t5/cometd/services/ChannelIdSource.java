package org.lazan.t5.cometd.services;

import org.apache.tapestry5.ComponentResources;

public interface ChannelIdSource {
	String getChannelId(ComponentResources resources, String clientId);
}
