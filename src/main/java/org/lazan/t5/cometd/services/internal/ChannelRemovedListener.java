package org.lazan.t5.cometd.services.internal;

import org.cometd.bayeux.server.BayeuxServer.ChannelListener;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.lazan.t5.cometd.services.CometdGlobals;

public class ChannelRemovedListener implements ChannelListener {
	private final CometdGlobals cometdGlobals;

	public ChannelRemovedListener(CometdGlobals cometdGlobals) {
		super();
		this.cometdGlobals = cometdGlobals;
	}

	public void channelAdded(ServerChannel channel) {
	}

	/**
	 * Cleans up maps to avoid memory leaks TODO: There are race conditions
	 * which could cause this to result in an invalid state
	 */
	public void channelRemoved(String channelId) {
		cometdGlobals.removeChannel(channelId);
	}

	public void configureChannel(ConfigurableServerChannel channel) {
	}
}
