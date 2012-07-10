package org.lazan.t5.cometd.services.internal;

import java.util.Set;

import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerChannel;
import org.lazan.t5.cometd.internal.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;

public class CometdGlobalsImpl implements CometdGlobals {
	private static final String ATTRIBUTE_CLIENT_CONTEXT = "clientContext";
	private final BayeuxServer bayeuxServer;
	private final TopicMatchers<String> channelIdsByTopic = new TopicMatchers<String>();
	
	public CometdGlobalsImpl(BayeuxServer bayeuxServer) {
		super();
		this.bayeuxServer = bayeuxServer;
	}

	public void removeChannel(String channelId) {
		ClientContext clientContext = getClientContext(channelId);
		if (clientContext != null) {
			channelIdsByTopic.removeMatcher(clientContext.getTopic(), channelId);
		}
	}
	
	public void setClientContext(String channelId, ClientContext clientContext) {
		channelIdsByTopic.addMatcher(clientContext.getTopic(), channelId);
		ServerChannel channel = bayeuxServer.getChannel(channelId);
		if (channel == null) {
			throw new IllegalStateException("Channel not found " + channelId);
		}
		channel.setAttribute(ATTRIBUTE_CLIENT_CONTEXT, clientContext);
	}
	
	public Set<String> getChannelIds(String topic) {
		return channelIdsByTopic.getMatches(topic);
	}
	
	public ClientContext getClientContext(String channelId) {
		ServerChannel channel = bayeuxServer.getChannel(channelId);
		if (channel != null) {
			return (ClientContext) channel.getAttribute(ATTRIBUTE_CLIENT_CONTEXT);
		}
		return null;
	}
}
