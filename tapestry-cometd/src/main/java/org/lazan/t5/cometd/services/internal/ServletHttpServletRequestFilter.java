package org.lazan.t5.cometd.services.internal;

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

import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.lazan.t5.cometd.services.ServletSource;

/**
 * Allows a servlet to be managed by tapestry instead of being instantiated and managed by the servlet container
 */
public class ServletHttpServletRequestFilter implements HttpServletRequestFilter {
	private final HttpServlet servlet;
	private final Pattern uriPattern;
	
	public ServletHttpServletRequestFilter(
			ServletSource servletSource,
			ApplicationGlobals applicationGlobals,
			RegistryShutdownHub registryShutdownHub) {
		try {
			this.servlet = servletSource.createServlet();
			this.servlet.init(createServletConfig(applicationGlobals, servletSource));
			this.uriPattern = Pattern.compile(servletSource.getURI() + "(/.*)?", Pattern.CASE_INSENSITIVE);
			registryShutdownHub.addRegistryShutdownListener(new Runnable() {
				public void run() {
					servlet.destroy();
				}
			});
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected ServletConfig createServletConfig(final ApplicationGlobals applicationGlobals, final ServletSource servletFactory) {
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
