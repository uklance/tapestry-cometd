package org.lazan.t5.cometddemo.services.internal;

import org.cometd.bayeux.server.BayeuxContext;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.transport.HttpTransport;
import org.lazan.t5.cometd.services.PushSession;
import org.lazan.t5.cometd.services.SubscriptionListener;
import org.lazan.t5.cometddemo.model.ChatUser;
import org.lazan.t5.cometddemo.services.ChatConstants;
import org.lazan.t5.cometddemo.services.ChatService;

public class ChatSubscriptionListener implements SubscriptionListener {
	private static final String ATT_USERNAME = "username";
	private final ChatService chatService;
	private final BayeuxServer bayeuxServer;

	public ChatSubscriptionListener(ChatService chatService, BayeuxServer bayeuxServer) {
		super();
		this.chatService = chatService;
		this.bayeuxServer = bayeuxServer;
	}

	@Override
	public void onSubscribe(PushSession pushSession) {
		// lookup the session attribute from the current bayeux transport context.
		// We can't @Inject the request here because the request might have originated from a WebSocket
		HttpTransport transport = (HttpTransport) bayeuxServer.getCurrentTransport();
		BayeuxContext bayeuxContext = transport.getContext();
		ChatUser chatUser = (ChatUser) bayeuxContext.getHttpSessionAttribute(ChatConstants.CHAT_USER_SESSION_ATTRIBUTE);
		if (chatUser == null) {
			throw new IllegalStateException("Chat user not logged in");
		}

		// copy the HTTP Session attribute to a PushSession session attribute to avoid lifecycle differences
		pushSession.setAttribute(ATT_USERNAME, chatUser.getUsername());
		chatService.joinChat(chatUser.getUsername());
	}
	
	@Override
	public void onUnsubscribe(PushSession pushSession) {
		String username = pushSession.getAttribute(ATT_USERNAME, String.class);
		chatService.leaveChat(username);
	}
}
