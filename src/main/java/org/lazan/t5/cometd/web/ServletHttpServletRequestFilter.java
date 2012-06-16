package org.lazan.t5.cometd.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;

public class ServletHttpServletRequestFilter implements HttpServletRequestFilter {
	private final HttpServlet servlet;
	private final Pattern uriPattern;
	
	public ServletHttpServletRequestFilter(ApplicationGlobals applicationGlobals, ServletFactory servletFactory, String uriPattern) {
		try {
			this.servlet = servletFactory.createServlet();
			this.servlet.init(createServletConfig(applicationGlobals, servletFactory));
			this.uriPattern = Pattern.compile(uriPattern, Pattern.CASE_INSENSITIVE);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected ServletConfig createServletConfig(final ApplicationGlobals applicationGlobals, final ServletFactory servletFactory) {
		final Map<String, String> initParams = servletFactory.getInitParams();
		return new ServletConfig() {
			public String getInitParameter(String name) {
				return initParams.get(name);
			}
			public Enumeration<String> getInitParameterNames() {
				final Iterator<String> it = initParams.keySet().iterator();
				return new Enumeration<String>() {
					public boolean hasMoreElements() {
						return it.hasNext();
					}
					public String nextElement() {
						return it.next();
					}
				};
				
			}
			public ServletContext getServletContext() {
				return applicationGlobals.getServletContext();
			}
			public String getServletName() {
				return servletFactory.getServletName();
			}
		};
	}

	public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler) throws IOException {
		if (uriPattern.matcher(request.getServletPath()).matches()) {
			try {
				servlet.service(request, response);
			} catch (ServletException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		return handler.service(request, response);
	}
}
