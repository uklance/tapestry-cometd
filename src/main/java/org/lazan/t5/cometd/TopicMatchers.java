package org.lazan.t5.cometd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TopicMatchers<T> {
	private MatchNode<T> rootNode;
	
	
	/**
	 * @param topic A subscription topic (eg "/chat/cars")
	 * @return
	 */
	public List<T> getMatches(String topic) {
		if (topic.startsWith("/")) {
			topic = topic.substring(1);
		}
		String[] levels = topic.split("/");
		List<T> matches = new ArrayList<T>();
		MatchNode<T> node = rootNode;
		for (int i = 0; i < levels.length; ++ i) {
			if (node == null) {
				break;
			}
			String level = levels[i];
			if (node.multiWildcards != null) {
				matches.addAll(node.multiWildcards);
			}
			if (i == levels.length - 1) {
				if (node.singleWildcards != null) {
					matches.addAll(node.singleWildcards);
				}
				List<T> list = node.matchersByLevel == null ? null : node.matchersByLevel.get(level);
				if (list != null) {
					matches.addAll(list);
				}
			} else {
				node = node.nodesByLevel == null ? null : node.nodesByLevel.get(level);
			}
		}
		return matches;
	}
	
	/**
	 * Add a matcher using a topic pattern which may contain wildcards.
	 * "*" matches a single level
	 * "**" matches all sublevels 
	 * @param pattern A topic pattern (eg "/chat/cars", "/chat/*", "/**")
	 * @param matcher
	 */
	public synchronized void add(String pattern, T matcher) {
		String[] levels = pattern.split("/");
		MatchNode<T> node = rootNode;
		if (node == null) {
			node = new MatchNode<T>();
			rootNode = node;
		}
		for (int i = 0; i < levels.length; ++i) {
			String level = levels[i];
			if (i == levels.length - 1) {
				if ("**".equals(level)) {
					if (node.multiWildcards == null) {
						node.multiWildcards = new CopyOnWriteArrayList<T>();
					}
					node.multiWildcards.add(matcher);
				} else if ("*".equals(level)) {
					if (node.singleWildcards == null) {
						node.singleWildcards = new CopyOnWriteArrayList<T>();
					}
					node.singleWildcards.add(matcher);
				} else {
					if (node.matchersByLevel == null) {
						node.matchersByLevel = new ConcurrentHashMap<String, List<T>>();
					}
					List<T> matchers = node.matchersByLevel.get(level);
					if (matchers == null) {
						matchers = new CopyOnWriteArrayList<T>();
						node.matchersByLevel.put(level, matchers);
					}
					matchers.add(matcher);
				}
			} else {
				if (node.nodesByLevel == null) {
					node.nodesByLevel = new ConcurrentHashMap<String, MatchNode<T>>();
				}
				MatchNode<T> next = node.nodesByLevel.get(level);
				if (next == null) {
					next = new MatchNode<T>();
					node.nodesByLevel.put(level, next);
				}
				node = next;
			}
		}
	}
	
	public static class MatchNode<T> {
		private Map<String, List<T>> matchersByLevel;
		private List<T> singleWildcards;
		private List<T> multiWildcards;
		private Map<String, MatchNode<T>> nodesByLevel;
	}
}
