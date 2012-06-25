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
		Set<T> matches = new HashSet<T>();
		addMatches(matches, topic);
		
		boolean isFirst = true;
		String subtopic = topic;
		while (subtopic != null) {
			int index = subtopic.lastIndexOf('/');
			if (index < 0) {
				if (isFirst) {
					addMatches(matches, "*");
					isFirst = false;
				}
				addMatches(matches, "**");
				subtopic = null;
			} else if (index == 0) {
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
	 * @param pattern A topic pattern (eg "/chat/cars", "/chat/*", "/**")
	 * @param matcher
	 */
	public void addMatcher(String pattern, T matcher) {
		Set<T> set = matchersByPattern.get(pattern);
		if (set == null) {
			Set<T> candidate = Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
			set = matchersByPattern.putIfAbsent(pattern, candidate);
			if (set == null) {
				set = candidate;
			}
		}
		set.add(matcher);
	}
	
	public boolean removeMatcher(String pattern, T matcher) {
		Set<T> set = matchersByPattern.get(pattern);
		boolean removed = false;
		if (set != null) {
			removed = set.remove(matcher);
			if (set.isEmpty()) {
				matchersByPattern.remove(pattern, set);
			}
		}
		return removed;
	}
}
