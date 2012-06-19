package org.lazan.t5.cometd.services;

import org.apache.tapestry5.ComponentResources;

public class ChannelIdSourceImpl implements ChannelIdSource {
	public String getChannelId(ComponentResources resources, String clientId) {
		return String.format("/%s/%s", resources.getCompleteId(), clientId);
	}
}
