package org.lazan.t5.cometd.services.internal;

import org.apache.tapestry5.ComponentResources;
import org.lazan.t5.cometd.services.ChannelIdSource;

public class ChannelIdSourceImpl implements ChannelIdSource {
	public String getChannelId(ComponentResources resources, String clientId) {
		return String.format("/push-target/%s/%s", resources.getCompleteId(), clientId);
	}
}
