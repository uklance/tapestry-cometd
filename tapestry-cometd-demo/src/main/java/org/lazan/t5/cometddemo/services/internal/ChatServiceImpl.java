package org.lazan.t5.cometddemo.services.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.lazan.t5.cometd.services.PushManager;
import org.lazan.t5.cometddemo.model.ChatMessage;
import org.lazan.t5.cometddemo.services.ChatService;

public class ChatServiceImpl implements ChatService {
	private final Set<String> users = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private final PushManager pushManager;
	
	public ChatServiceImpl(PushManager pushManager) {
		super();
		this.pushManager = pushManager;
	}

	@Override
	public void joinChat(String user) {
		users.add(user);
		sendChat(null, String.format("%s joined the chatroom", user));
		pushManager.broadcast(getChatUsersTopic(), getChatUsers());
	}
	
	@Override
	public void leaveChat(String user) {
		users.remove(user);
		sendChat(null, String.format("%s left the chatroom", user));
		pushManager.broadcast(getChatUsersTopic(), getChatUsers());
	}
	
	@Override
	public void sendChat(String fromUser, String message) {
		ChatMessage chat = new ChatMessage(fromUser, null, message, new Date());
		pushManager.broadcast(getChatTopic(), chat);
	}
	
	@Override
	public String getChatTopic() {
		return "/chatservice/chat";
	}
	
	@Override
	public String getChatUsersTopic() {
		return "/chatservice/users";
	}
	
	public Collection<String> getChatUsers() {
		return Collections.unmodifiableSet(users);
	}
}
