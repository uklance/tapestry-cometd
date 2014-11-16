package org.lazan.t5.cometd.services.internal;

import java.util.Map;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JSONUtilsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testUnwrap() {
		JSONObject json = new JSONObject();
		json.put("string", "foo");
		json.put("integer", 1);
		json.put("array", new JSONArray(1, 2, 3));
		
		JSONObject nested = new JSONObject("key", "value");
		json.put("nested", nested);
		
		Map<String, Object> result = JSONUtils.unwrap(json);
		Assert.assertEquals("foo", result.get("string"));
		Assert.assertEquals(1, result.get("integer"));
		Assert.assertArrayEquals(new Object[] { 1, 2, 3 }, (Object[]) result.get("array"));
		
		Map<String, String> map = (Map) result.get("nested");
		Assert.assertEquals("value", map.get("key"));
	}

}
