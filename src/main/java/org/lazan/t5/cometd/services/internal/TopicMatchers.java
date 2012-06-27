package org.lazan.t5.cometd.services.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TopicMatchers<T> {
	private ConcurrentMap<String, Set<T>> matchersByPattern = new ConcurrentHashMap<String, Set<T>>();
	
	/**
	 * @param topic A subscription topic (eg "/chat/cars")
	 * @return
	 */
	public Set<T> getMatches(String topic) {
		validateTopic(topic);
		Set<T> matches = new HashSet<T>();
		addMatches(matches, topic);
		
		boolean isFirst = true;
		String subtopic = topic;
		while (subtopic != null) {
			int index = subtopic.lastIndexOf('/');
			if (index == 0) {
				if (isFirst) {
					addMatches(matches, "/*");
					isFirst = false;
				}
				addMatches(matches, "/**");
				subtopic = null;
			} else {
				subtopic = subtopic.substring(0, index);
				if (isFirst) {
					addMatches(matches, subtopic + "/*");
					isFirst = false;
				}
				addMatches(matches, subtopic + "/**");
			}	
		}
		return matches;
	}
	
	private void addMatches(Set<T> matches, String topic) {
		Set<T> set = matchersByPattern.get(topic);
		if (set != null) {
			matches.addAll(set);
		}
	}
	
	/**
	 * Add a matcher using a topic pattern which may contain wildcards.
	 * "*" matches a single level
	 * "**" matches all sublevels 
	 * @param topic A topic pattern (eg "/chat/cars", "/chat/*", "/**")
	 * @param matcher
	 */
	public void addMatcher(String topic, T matcher) {
		validateTopic(topic);
		Set<T> set = matchersByPattern.get(topic);
		if (set == null) {
			Set<T> candidate = Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
			set = matchersByPattern.putIfAbsent(topic, candidate);
			if (set == null) {
				set = candidate;
			}
		}
		set.add(matcher);
	}
	
	public boolean removeMatcher(String topic, T matcher) {
		validateTopic(topic);
		Set<T> set = matchersByPattern.get(topic);
		boolean removed = false;
		if (set != null) {
			removed = set.remove(matcher);
			if (removed && set.isEmpty()) {
				matchersByPattern.remove(topic, set);
			}
		}
		return removed;
	}

	protected void validateTopic(String topic) {
		if (topic.indexOf('/') != 0) {
			throw new IllegalArgumentException("Topic must start with '/'");
		}
		if (topic.endsWith("/")) {
			throw new IllegalArgumentException("Topic must not end with '/'");
		}
	}
}
