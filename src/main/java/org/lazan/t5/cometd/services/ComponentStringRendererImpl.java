package org.lazan.t5.cometd.services;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.RequestImpl;
import org.apache.tapestry5.internal.services.ResponseImpl;
import org.apache.tapestry5.internal.services.SessionImpl;
import org.apache.tapestry5.internal.services.TapestrySessionFactory;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;
import org.lazan.t5.cometd.web.FakeHttpServletRequest;
import org.lazan.t5.cometd.web.FakeHttpServletResponse;

public class ComponentStringRendererImpl implements ComponentStringRenderer {
	private final ExecutorService executors;
	private final ComponentRequestHandler componentRequestHandler;
	private final RequestGlobals requestGlobals;
	private final String applicationCharset;
	private final PerthreadManager perthreadManager;

	public ComponentStringRendererImpl(ExecutorService executors, ComponentRequestHandler componentRequestHandler,
			RequestGlobals requestGlobals, @Symbol(SymbolConstants.CHARSET) String applicationCharset, PerthreadManager perthreadManager) {
		super();
		this.executors = executors;
		this.componentRequestHandler = componentRequestHandler;
		this.requestGlobals = requestGlobals;
		this.applicationCharset = applicationCharset;
		this.perthreadManager = perthreadManager;
	}

	public String render(ComponentEventRequestParameters parameters) {
		return render(parameters, null);
	}

	public String render(final ComponentEventRequestParameters parameters, final HttpSession httpSession) {
		Future<String> future = executors.submit(new Callable<String>() {
			public String call() throws Exception {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				final HttpServletRequest fakeHttpRequest = new FakeHttpServletRequest(httpSession);
				HttpServletResponse fakeHttpResponse = new FakeHttpServletResponse(out, applicationCharset);
				TapestrySessionFactory sessionFactory = new TapestrySessionFactory() {
					public Session getSession(boolean create) {
						if (httpSession == null) {
							throw new IllegalStateException("Session not registered, this is most likely because you have not set session = 'true' in the push mixin");
						}
						return new SessionImpl(fakeHttpRequest, httpSession);
					}
				};
				Request fakeRequest = new RequestImpl(fakeHttpRequest, applicationCharset, sessionFactory);
				Response fakeResponse = new ResponseImpl(fakeHttpRequest, fakeHttpResponse);				

				try {
					requestGlobals.storeServletRequestResponse(fakeHttpRequest, fakeHttpResponse);
					requestGlobals.storeRequestResponse(fakeRequest, fakeResponse);
					componentRequestHandler.handleComponentEvent(parameters);
				} finally {
					perthreadManager.cleanup();
				}

				return new String(out.toByteArray(), applicationCharset);
			}
		});
		try {
			return future.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
