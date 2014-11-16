package org.lazan.t5.cometd.services.internal;

import java.util.List;

import org.apache.tapestry5.services.Session;
import org.cometd.bayeux.server.BayeuxContext;

public class BayeuxContextSession implements Session {
	private final BayeuxContext bayeuxContext;

	public BayeuxContextSession(BayeuxContext bayeuxContext) {
		super();
		this.bayeuxContext = bayeuxContext;
	}
	
	@Override
	public Object getAttribute(String name) {
		return bayeuxContext.getHttpSessionAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		bayeuxContext.setHttpSessionAttribute(name, value);
	}

	@Override
	public void invalidate() {
		bayeuxContext.invalidateHttpSession();
	}

	@Override
	public List<String> getAttributeNames() {
		throw new UnsupportedOperationException("getAttributeNames");
	}

	@Override
	public List<String> getAttributeNames(String prefix) {
		throw new UnsupportedOperationException("getAttributeNames");
	}

	@Override
	public int getMaxInactiveInterval() {
		throw new UnsupportedOperationException("getMaxInactiveInterval");
	}

	@Override
	public void setMaxInactiveInterval(int seconds) {
		throw new UnsupportedOperationException("setMaxInactiveInterval");
	}

	@Override
	public boolean isInvalidated() {
		throw new UnsupportedOperationException("isInvalidated");
	}

	@Override
	public void restoreDirtyObjects() {
		throw new UnsupportedOperationException("restoreDirtyObjects");
	}
}
