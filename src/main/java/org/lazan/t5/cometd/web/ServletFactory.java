package org.lazan.t5.cometd.web;

import java.util.Map;

import javax.servlet.http.HttpServlet;

public interface ServletFactory {
	public HttpServlet createServlet();
	public Map<String, String> getInitParams();
	public String getServletName();
}
