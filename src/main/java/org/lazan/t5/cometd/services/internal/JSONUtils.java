package org.lazan.t5.cometd.services.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

public class JSONUtils {
	public static Map<String, Object> unwrap(JSONObject json) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (String key : json.keys()) {
			Object value = json.get(key);
			map.put(key, unwrap(value));
		}
		return map;
	}
	
	public static Object unwrap(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof JSONObject) {
			return unwrap((JSONObject) value);
		}
		if (value instanceof JSONArray) {
			return unwrap((JSONArray) value);
		}
		return value;
	}

	public static Object[] unwrap(JSONArray jsonArr) {
		Object[] arr = new Object[jsonArr.length()];
		for (int i = 0; i < arr.length; ++ i) {
			arr[i] = unwrap(jsonArr.get(i));
		}
		return arr;
	}
}
