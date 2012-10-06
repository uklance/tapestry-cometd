package org.lazan.t5.cometd.services;

import javax.servlet.http.HttpSession;

import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentEventRequestParameters;

public interface ComponentJSONRenderer {
	public JSONObject render(ComponentEventRequestParameters parameters);
	public JSONObject render(ComponentEventRequestParameters parameters, HttpSession session);
}
