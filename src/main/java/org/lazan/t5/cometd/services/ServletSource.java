package org.lazan.t5.cometd.services;

import java.util.Map;

import javax.servlet.http.HttpServlet;

public interface ServletSource {
	public HttpServlet createServlet();
	public Map<String, String> getInitParams();
	public String getServletName();
	public String getURI();
}
