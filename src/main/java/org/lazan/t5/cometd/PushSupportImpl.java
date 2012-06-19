package org.lazan.t5.cometd;

import org.apache.tapestry5.json.JSONObject;

public class PushSupportImpl implements PushSupport {
	private JSONObject spec = new JSONObject();
	public JSONObject getSpec() {
		return spec;
	}
}
