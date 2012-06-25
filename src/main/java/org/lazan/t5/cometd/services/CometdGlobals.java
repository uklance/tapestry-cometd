package org.lazan.t5.cometd.services;

import java.util.Set;

import org.lazan.t5.cometd.ClientContext;

public interface CometdGlobals {

	Set<String> getChannelIds(String topic);

	ClientContext getClientContext(String channelId);

	void setClientContext(String channelId, ClientContext clientContext);

	void removeChannel(String channelId);

}
