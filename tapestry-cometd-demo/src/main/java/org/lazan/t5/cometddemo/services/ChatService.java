package org.lazan.t5.cometddemo.services;

import java.util.Collection;

public interface ChatService {
	void joinChat(String user);

	void leaveChat(String user);

	void sendChat(String fromUser, String message);

	String getChatTopic();

	String getChatUsersTopic();
	
	Collection<String> getChatUsers();
}