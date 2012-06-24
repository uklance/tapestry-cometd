package org.lazan.t5.cometd;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class TopicMatchersTest {

	@Test
	public void test() {
		TopicMatchers<String> matchers = new TopicMatchers<String>();
		
		matchers.addMatcher("**", "a");
		matchers.addMatcher("*", "b");
		matchers.addMatcher("1", "c");
		matchers.addMatcher("1/1.1", "d");
		matchers.addMatcher("1/1.1/1.1.1", "e");
		matchers.addMatcher("1/1.2", "e");
		matchers.addMatcher("1/*", "g");
		matchers.addMatcher("1/**", "h");
		matchers.addMatcher("1/1.1/*", "i");
		matchers.addMatcher("1/1.1/**", "j");
		matchers.addMatcher("1/**", "k");
		matchers.addMatcher("2", "l");
		matchers.addMatcher("2/2.1", "m");
		
		assertMatches(new String[] { "a", "b", "c" }, matchers.getMatches("1"));
		assertMatches(new String[] { "a", "d", "g", "h", "k" }, matchers.getMatches("1/1.1"));
		assertMatches(new String[] { "a", "e", "h", "j", "k", "i" }, matchers.getMatches("1/1.1/1.1.1"));
		assertMatches(new String[] { "a", "b", "l" }, matchers.getMatches("2"));
		assertMatches(new String[] { "a", "b" }, matchers.getMatches("3"));
		assertMatches(new String[] { "a" }, matchers.getMatches("3/3.1"));
	}

	@Test
	public void test2() {
		TopicMatchers<String> matchers = new TopicMatchers<String>();
		
		matchers.addMatcher("/**", "a");
		matchers.addMatcher("/*", "b");
		matchers.addMatcher("/1/1.1", "c");
		
		assertMatches(new String[] { "a", "c" }, matchers.getMatches("/1/1.1"));
	}
	
	private void assertMatches(String[] expecteds, Set<String> actuals) {
		Assert.assertEquals(new HashSet<String>(Arrays.asList(expecteds)), actuals);
	}

}
