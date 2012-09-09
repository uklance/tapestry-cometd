package org.lazan.t5.cometd.services.internal;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TopicMatchers<T> {
	private ConcurrentMap<String, Set<T>> matchersByPattern = new ConcurrentHashMap<String, Set<T>>();

	/**
	 * Find the objects that match the topic. This includes exact matches and
	 * wildcard matches.
	 * 
	 * @param topic
	 *            A subscription topic (eg "/chat/cars")
	 * @return
	 */
	public Set<T> getMatches(String topic) {
		Set<T> matches = new LinkedHashSet<T>();
		validateTopic(topic);
		addExactMatches(topic, matches);
		addWildcardMatches(topic, matches);
		return matches.isEmpty() ? Collections.<T> emptySet() : matches;
	}

	/**
	 * Add a matcher using a topic pattern which may contain wildcards. "*"
	 * matches a single level "**" matches all sublevels
	 * 
	 * @param topic
	 *            A topic pattern (eg "/chat/cars", "/chat/*", "/**")
	 * @param matcher
	 */
	public synchronized void addMatcher(String topic, T matcher) {
		validateTopic(topic);
		Set<T> set = matchersByPattern.get(topic);
		if (set != null) {
			set.add(matcher);
		} else {
			set = Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
			set.add(matcher);
			matchersByPattern.put(topic, set);
		}
	}

	public synchronized boolean removeMatcher(String topic, T matcher) {
		validateTopic(topic);
		Set<T> set = matchersByPattern.get(topic);
		boolean removed = false;
		if (set != null) {
			removed = set.remove(matcher);
			if (removed && set.isEmpty()) {
				matchersByPattern.remove(topic);
			}
		}
		return removed;
	}

	/**
	 * Topics must start with '/' and can not end with '/'
	 * @param topic
	 */
	protected void validateTopic(String topic) {
		if (topic.indexOf('/') != 0) {
			throw new IllegalArgumentException("Topic must start with '/'");
		}
		if (topic.charAt(topic.length() - 1) == '/') {
			throw new IllegalArgumentException("Topic must not end with '/'");
		}
	}

	protected void addExactMatches(String topic, Set<T> matches) {
		Set<T> set = matchersByPattern.get(topic);
		if (set != null) {
			matches.addAll(set);
		}
	}

	protected void addWildcardMatches(String topic, Set<T> matches) {
		boolean isFirstParent = true;
		for (int i = topic.length() - 1; i >= 0; --i) {
			if (topic.charAt(i) == '/') {
				String parent = topic.substring(0, i);
				if (isFirstParent) {
					// only attempt * for the first parent
					addExactMatches(parent + "/*", matches);
					isFirstParent = false;
				}

				// attempt ** for all parents
				addExactMatches(parent + "/**", matches);
			}
		}
	}
}