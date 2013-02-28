package org.lazan.t5.cometd.services.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SuppressWarnings("rawtypes")
public class FakeHttpServletRequest implements HttpServletRequest {
	private HttpSession session;
	private Map<String, String> headers = new HashMap<String, String>();
	private String characterEncoding;
	private Map<String, String> parameters = new HashMap<String, String>();
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private String contextPath;
	
	public FakeHttpServletRequest(HttpSession session, String contextPath) {
		super();
		this.session = session;
		this.contextPath = contextPath;
	}

	public Object getAttribute(String arg0) {
		return attributes.get(arg0);
	}

	public Enumeration getAttributeNames() {
		throw new UnsupportedOperationException("getAttributeNames");
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public int getContentLength() {
		throw new UnsupportedOperationException("getContentLength");
	}

	public String getContentType() {
		throw new UnsupportedOperationException("getContentType");
	}

	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("getInputStream");
	}

	public String getLocalAddr() {
		throw new UnsupportedOperationException("getLocalAddr");
	}

	public String getLocalName() {
		throw new UnsupportedOperationException("getLocalName");
	}

	public int getLocalPort() {
		throw new UnsupportedOperationException("getLocalPort");
	}

	public Locale getLocale() {
		throw new UnsupportedOperationException("getLocale");
	}

	public Enumeration getLocales() {
		throw new UnsupportedOperationException("getLocales");
	}

	public String getParameter(String arg0) {
		return parameters.get(arg0);
	}
	
	public void setParameter(String key, String value) {
		parameters.put(key,  value);
	}

	public Map getParameterMap() {
		throw new UnsupportedOperationException("getParameterMap");
	}

	public Enumeration getParameterNames() {
		throw new UnsupportedOperationException("getParameterNames");
	}

	public String[] getParameterValues(String arg0) {
		throw new UnsupportedOperationException("getParameterValues");
	}

	public String getProtocol() {
		throw new UnsupportedOperationException("getProtocol");
	}

	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException("getReader");
	}

	public String getRealPath(String arg0) {
		throw new UnsupportedOperationException("getRealPath");
	}

	public String getRemoteAddr() {
		throw new UnsupportedOperationException("getRemoteAddr");
	}

	public String getRemoteHost() {
		throw new UnsupportedOperationException("getRemoteHost");
	}

	public int getRemotePort() {
		throw new UnsupportedOperationException("getRemotePort");
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw new UnsupportedOperationException("getRequestDispatcher");
	}

	public String getScheme() {
		throw new UnsupportedOperationException("getScheme");
	}

	public String getServerName() {
		throw new UnsupportedOperationException("getServerName");
	}

	public int getServerPort() {
		throw new UnsupportedOperationException("getServerPort");
	}

	public boolean isSecure() {
		return false; // TODO
	}

	public void removeAttribute(String arg0) {
		throw new UnsupportedOperationException("removeAttribute");
	}

	public void setAttribute(String arg0, Object arg1) {
		attributes.put(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		this.characterEncoding = arg0;
	}

	public String getAuthType() {
		throw new UnsupportedOperationException("getAuthType");
	}

	public String getContextPath() {
		return contextPath;
	}

	public Cookie[] getCookies() {
		throw new UnsupportedOperationException("getCookies");
	}

	public long getDateHeader(String arg0) {
		throw new UnsupportedOperationException("getDateHeader");
	}

	public String getHeader(String arg0) {
		return headers.get(arg0);
	}
	
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}

	public Enumeration getHeaderNames() {
		throw new UnsupportedOperationException("getHeaderNames");
	}

	public Enumeration getHeaders(String arg0) {
		throw new UnsupportedOperationException("getHeaders");
	}

	public int getIntHeader(String arg0) {
		throw new UnsupportedOperationException("getIntHeader");
	}

	public String getMethod() {
		throw new UnsupportedOperationException("getMethod");
	}

	public String getPathInfo() {
		throw new UnsupportedOperationException("getPathInfo");
	}

	public String getPathTranslated() {
		throw new UnsupportedOperationException("getPathTranslated");
	}

	public String getQueryString() {
		throw new UnsupportedOperationException("getQueryString");
	}

	public String getRemoteUser() {
		throw new UnsupportedOperationException("getRemoteUser");
	}

	public String getRequestURI() {
		throw new UnsupportedOperationException("getRequestURI");
	}

	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException("getRequestURL");
	}

	public String getRequestedSessionId() {
		throw new UnsupportedOperationException("getRequestedSessionId");
	}

	public String getServletPath() {
		throw new UnsupportedOperationException("getServletPath");
	}

	public HttpSession getSession() {
		if (session == null) {
			throw new UnsupportedOperationException("getSession");
		}
		return session;
	}

	public HttpSession getSession(boolean arg0) {
		if (session == null) {
			throw new UnsupportedOperationException("getSession");
		}
		return session;
	}

	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException("getUserPrincipal");
	}

	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException("isRequestedSessionIdFromCookie");
	}

	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException("isRequestedSessionIdFromURL");
	}

	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException("isRequestedSessionIdFromUrl");
	}

	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException("isRequestedSessionIdValid");
	}

	public boolean isUserInRole(String arg0) {
		throw new UnsupportedOperationException("isUserInRole");
	}
}
