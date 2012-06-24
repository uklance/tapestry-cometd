package org.lazan.t5.cometd.services.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.lazan.t5.cometd.ClientContext;
import org.lazan.t5.cometd.services.CometdGlobals;

public class CometdGlobalsImplTest {

	@Test
	public void test() {
		CometdGlobals globals = new CometdGlobalsImpl();
		
		globals.setClientContext("/stocks/GOOG", "goog", mockClientContext());
		globals.setClientContext("/**", "wild", mockClientContext());
		
		assertChannelIds(new String[] { "goog", "wild" }, globals.getChannelIds("/stocks/GOOG"));
		assertChannelIds(new String[] { "wild" }, globals.getChannelIds("/stocks/YAHOO"));
	}

	private void assertChannelIds(String[] expecteds, Set<String> channelIds) {
		Assert.assertEquals(new HashSet<String>(Arrays.asList(expecteds)), channelIds);
	}

	private ClientContext mockClientContext() {
		String string = "test";
		return new ClientContext(false, string, string, string, string, string);
	}
	
	
	

}
