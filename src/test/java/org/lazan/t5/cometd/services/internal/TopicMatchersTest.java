package org.lazan.t5.cometd.services.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;

public class TopicMatchersTest {

	@Test
	public void testGetMatches() {
		TopicMatchers<String> matchers = new TopicMatchers<String>();

		matchers.addMatcher("/**", "a");
		matchers.addMatcher("/*", "b");
		matchers.addMatcher("/1", "c");
		matchers.addMatcher("/1/1.1", "d");
		matchers.addMatcher("/1/1.1/1.1.1", "e");
		matchers.addMatcher("/1/1.2", "e");
		matchers.addMatcher("/1/*", "g");
		matchers.addMatcher("/1/**", "h");
		matchers.addMatcher("/1/1.1/*", "i");
		matchers.addMatcher("/1/1.1/**", "j");
		matchers.addMatcher("/1/**", "k");
		matchers.addMatcher("/2", "l");
		matchers.addMatcher("/2/2.1", "m");

		assertMatches(new String[] { "a", "b", "c" }, matchers.getMatches("/1"));
		assertMatches(new String[] { "a", "d", "g", "h", "k" }, matchers.getMatches("/1/1.1"));
		assertMatches(new String[] { "a", "e", "h", "j", "k", "i" },
				matchers.getMatches("/1/1.1/1.1.1"));
		assertMatches(new String[] { "a", "b", "l" }, matchers.getMatches("/2"));
		assertMatches(new String[] { "a", "b" }, matchers.getMatches("/3"));
		assertMatches(new String[] { "a" }, matchers.getMatches("/3/3.1"));
	}

	@Test
	public void testThreadSafe() throws InterruptedException, ExecutionException {
		TopicMatchers<String> matchers = new TopicMatchers<String>();
		ExecutorService executors = Executors.newFixedThreadPool(4);
		List<Future<?>> futures = new ArrayList<Future<?>>();

		futures.add(executors.submit(new TestWorker(matchers, "/topic1")));
		futures.add(executors.submit(new TestWorker(matchers, "/topic1")));
		futures.add(executors.submit(new TestWorker(matchers, "/topic2")));
		futures.add(executors.submit(new TestWorker(matchers, "/topic2")));

		for (Future<?> future : futures) {
			future.get();
		}
	}

	private void assertMatches(String[] expecteds, Set<String> actuals) {
		Assert.assertEquals(new HashSet<String>(Arrays.asList(expecteds)), actuals);
	}

	private static class TestWorker implements Runnable {
		TopicMatchers<String> matchers;
		String topic;

		public TestWorker(TopicMatchers<String> matchers, String topic) {
			super();
			this.matchers = matchers;
			this.topic = topic;
		}

		public void run() {
			int sampleSize = 100000;
			for (int i = 0; i < sampleSize; ++i) {
				// use a thread specific value to avoid name clashes
				String value = String.format("%s-%s", Thread.currentThread().getName(), i);

				Assert.assertFalse("Not exists before add",
						matchers.getMatches(topic).contains(value));
				matchers.addMatcher(topic, value);
				Assert.assertTrue("Exists after add", matchers.getMatches(topic).contains(value));
				matchers.removeMatcher(topic, value);
				Assert.assertFalse("Not exists after remove",
						matchers.getMatches(topic).contains(value));
			}
		}
	}
}