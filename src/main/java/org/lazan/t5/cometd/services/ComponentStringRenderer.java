package org.lazan.t5.cometd.services;

import javax.servlet.http.HttpSession;

import org.apache.tapestry5.services.ComponentEventRequestParameters;

public interface ComponentStringRenderer {
	public String render(ComponentEventRequestParameters parameters);
	public String render(ComponentEventRequestParameters parameters, HttpSession session);
}
