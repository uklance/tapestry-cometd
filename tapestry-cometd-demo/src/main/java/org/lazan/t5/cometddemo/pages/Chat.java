package org.lazan.t5.cometddemo.pages;

import java.util.Collection;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionAttribute;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.lazan.t5.cometddemo.model.ChatMessage;
import org.lazan.t5.cometddemo.model.ChatUser;
import org.lazan.t5.cometddemo.services.ChatConstants;
import org.lazan.t5.cometddemo.services.ChatService;

public class Chat {
    @Inject
    private ChatService chatService;
    
    @Inject
    private Block chatBlock;
    
    @Inject
    private Block chatUsersBlock;
    
    @InjectComponent
    private Zone chatZone;

    /**
     * use @SessionAttribute instead of @SessionState since the ApplicationStateManager is tied to
     * the Request. When using WebSockets we cannot @Inject the request
     */
    @SessionAttribute(ChatConstants.CHAT_USER_SESSION_ATTRIBUTE)
    @Property
    private ChatUser chatUser;
    
    @Property
    private ChatMessage inMessage;
    
    @Property
    private String outMessage;

    @Property
    private String username;
    
    @Property
    private Collection<String> chatUsers;
    
    @Property
    private String currentUser;
    
    
    void onSuccessFromLoginForm() {
    	chatUser = new ChatUser(username);
    }
    
    void onLogout() {
    	chatUser = null;
    }
    
    public String getChatTopic() {
    	return chatService.getChatTopic();
    }
    
    public String getChatUsersTopic() {
    	return chatService.getChatUsersTopic();
    }
    
    Block onChat(ChatMessage inMessage) {
    	this.inMessage = inMessage;
    	return chatBlock;
    }
    
    Block onSuccessFromChatForm() {
    	chatService.sendChat(chatUser.getUsername(), outMessage);
    	return chatZone.getBody();
    }
    
    Block onChatUsersUpdated(Collection<String> chatUsers) {
    	this.chatUsers = chatUsers;
    	return chatUsersBlock;
    }
}
