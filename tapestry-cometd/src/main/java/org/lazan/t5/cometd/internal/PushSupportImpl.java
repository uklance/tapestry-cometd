package org.lazan.t5.cometd.internal;

import org.apache.tapestry5.json.JSONObject;
import org.lazan.t5.cometd.PushSupport;

public class PushSupportImpl implements PushSupport {
	private JSONObject spec = new JSONObject();
	public JSONObject getSpec() {
		return spec;
	}
}
