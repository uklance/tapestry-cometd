package org.lazan.t5.cometd.services;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.RequestImpl;
import org.apache.tapestry5.internal.services.ResponseImpl;
import org.apache.tapestry5.internal.services.SessionImpl;
import org.apache.tapestry5.internal.services.TapestrySessionFactory;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;
import org.lazan.t5.cometd.web.FakeHttpServletRequest;
import org.lazan.t5.cometd.web.FakeHttpServletResponse;

public class ComponentJsonRendererImpl implements ComponentJsonRenderer {
	private final ParallelExecutor parallelExecutor;
	private final ComponentRequestHandler componentRequestHandler;
	private final RequestGlobals requestGlobals;
	private final String applicationCharset;
	private final PerthreadManager perthreadManager;

	public ComponentJsonRendererImpl(ParallelExecutor parallelExecutor, ComponentRequestHandler componentRequestHandler,
			RequestGlobals requestGlobals, @Symbol(SymbolConstants.CHARSET) String applicationCharset, PerthreadManager perthreadManager) {
		super();
		this.parallelExecutor = parallelExecutor;
		this.componentRequestHandler = componentRequestHandler;
		this.requestGlobals = requestGlobals;
		this.applicationCharset = applicationCharset;
		this.perthreadManager = perthreadManager;
	}

	public JSONObject render(ComponentEventRequestParameters parameters) {
		return render(parameters, null);
	}

	public JSONObject render(final ComponentEventRequestParameters parameters, final HttpSession httpSession) {
		Future<JSONObject> future = parallelExecutor.invoke(new Invokable<JSONObject>() {
			public JSONObject invoke() {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				final FakeHttpServletRequest fakeHttpRequest = new FakeHttpServletRequest(httpSession);
				fakeHttpRequest.setHeader("X-Requested-With", "XMLHttpRequest");
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
					String jsonString = new String(out.toByteArray(), applicationCharset);
					return new JSONObject(jsonString);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					perthreadManager.cleanup();
				}

			}
		});
		try {
			return future.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
