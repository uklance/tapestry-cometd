package org.lazan.t5.cometd;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
	public synchronized void addMatcher(String pattern, T matcher) {
		Set<T> set = matchersByPattern.get(pattern);
		if (set == null) {
			Set<T> temp = Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
			set = matchersByPattern.putIfAbsent(pattern, temp);
			if (set == null) {
				set = temp;
			}
		}
		set.add(matcher);
	}
	
	public synchronized boolean removeMatcher(T matcher) {
		boolean removed = false;
		for (Iterator<Set<T>> it = matchersByPattern.values().iterator(); it.hasNext(); ) {
			Set<T> set = it.next();
			if (set.remove(matcher)) {
				removed = true;
				if (set.isEmpty()) {
					// TODO: use ConcurrentMap.remove(Object key, Object value) and get rid of synchronized
					it.remove();
				}
			}
		}
		return removed;
	}
}
