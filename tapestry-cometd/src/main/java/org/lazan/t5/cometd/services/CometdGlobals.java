package org.lazan.t5.cometd.services;

import java.util.Set;


public interface CometdGlobals {

	Set<String> getChannelIds(String topic);

	ClientContext getClientContext(String channelId);

	void setClientContext(String channelId, ClientContext clientContext);

	void removeChannel(String channelId);

}
