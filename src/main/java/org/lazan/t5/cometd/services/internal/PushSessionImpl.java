package org.lazan.t5.cometd.services.internal;

import java.util.Set;

import org.cometd.bayeux.server.ServerSession;
import org.lazan.t5.cometd.internal.ClientContext;
import org.lazan.t5.cometd.services.PushSession;

public class PushSessionImpl implements PushSession {
	private final ServerSession serverSession;
	private final ClientContext clientContext;
	
	public PushSessionImpl(ServerSession serverSession, ClientContext clientContext) {
		super();
		this.serverSession = serverSession;
		this.clientContext = clientContext;
	}

	public String getTopic() {
		return clientContext.getTopic();
	}
	
	public <T> T getAttribute(String name, Class<T> type) {
		Object att = serverSession.getAttribute(name);
		return type.cast(att);
	}
	
	public void setAttribute(String name, Object value) {
		serverSession.setAttribute(name, value);
	}
	
	public Set<String> getAttributeNames() {
		return serverSession.getAttributeNames();
	}
}
