package org.lazan.t5.cometd;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class TopicMatchersTest {

	@Test
	public void test() {
		TopicMatchers<String> matchers = new TopicMatchers<String>();
		
		matchers.add("**", "a");
		matchers.add("*", "b");
		matchers.add("1", "c");
		matchers.add("1/1.1", "d");
		matchers.add("1/1.1/1.1.1", "e");
		matchers.add("1/1.2", "e");
		matchers.add("1/*", "g");
		matchers.add("1/**", "h");
		matchers.add("1/1.1/*", "i");
		matchers.add("1/1.1/**", "j");
		matchers.add("1/**", "k");
		matchers.add("2", "l");
		matchers.add("2/2.1", "m");
		
		assertMatches(new String[] { "a", "b", "c" }, matchers.getMatches("/1"));
		assertMatches(new String[] { "a", "d", "g", "h", "k" }, matchers.getMatches("/1/1.1"));
		assertMatches(new String[] { "a", "e", "h", "j", "k", "i" }, matchers.getMatches("/1/1.1/1.1.1"));
		assertMatches(new String[] { "a", "b", "l" }, matchers.getMatches("/2"));
		assertMatches(new String[] { "a", "b" }, matchers.getMatches("/3"));
		assertMatches(new String[] { "a" }, matchers.getMatches("/3/3.1"));
	}

	private void assertMatches(String[] expecteds, Iterator<String> matches) {
		Set<String> actuals = new HashSet<String>();
		while (matches.hasNext()) {
			Assert.assertTrue(actuals.add(matches.next()));
		}
		Assert.assertEquals(new HashSet<String>(Arrays.asList(expecteds)), actuals);
	}

}
