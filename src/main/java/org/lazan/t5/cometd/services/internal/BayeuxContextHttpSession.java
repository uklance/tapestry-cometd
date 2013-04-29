package org.lazan.t5.cometd.services.internal;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.cometd.bayeux.server.BayeuxContext;

public class BayeuxContextHttpSession implements HttpSession {
	private final BayeuxContext bayeuxContext;

	public BayeuxContextHttpSession(BayeuxContext bayeuxContext) {
		super();
		this.bayeuxContext = bayeuxContext;
	}

	@Override
	public String getId() {
		return bayeuxContext.getHttpSessionId();
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
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}
	
	@Override
	public void invalidate() {
		bayeuxContext.invalidateHttpSession();
	}
	
	@Override
	public long getCreationTime() {
		throw new UnsupportedOperationException("getCreationTime");
	}

	@Override
	public long getLastAccessedTime() {
		throw new UnsupportedOperationException("getLastAccessedTime");
	}

	@Override
	public ServletContext getServletContext() {
		throw new UnsupportedOperationException("getServletContext");
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		throw new UnsupportedOperationException("setMaxInactiveInterval");
	}

	@Override
	public int getMaxInactiveInterval() {
		throw new UnsupportedOperationException("getMaxInactiveInterval");
	}

	@Override
	public HttpSessionContext getSessionContext() {
		throw new UnsupportedOperationException("getSessionContext");
	}

	@Override
	public Enumeration getAttributeNames() {
		throw new UnsupportedOperationException("getAttributeNames");
	}

	@Override
	public String[] getValueNames() {
		throw new UnsupportedOperationException("getValueNames");
	}

	@Override
	public void removeAttribute(String name) {
		throw new UnsupportedOperationException("removeAttribute");
	}

	@Override
	public void removeValue(String name) {
		throw new UnsupportedOperationException("removeValue");
	}

	@Override
	public boolean isNew() {
		throw new UnsupportedOperationException("isNew");
	}
}
