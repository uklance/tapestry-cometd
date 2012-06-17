tapestry-cometd
===============

A push implementation for [tapestry][http://tapestry.apache.org/] based on [cometd][http://cometd.org/]

Usage:

Page.tml
    <t:zone 
        t:mixins="cometd/Push" 
        t:topic="publicChat"
        t:privateTopic="privateChat${user.userId}"
        t:event="processChat" 
        t:append="true" 
        t:session="false"
    >
        ${chatMessage}
    </t:zone>

Page.java
    public class Page {
        @Property
        private String chatMessage;
        
        @OnEvent("processChat")
        onProcessChat(String chatMessage) {
            this.chatMessage = chatMessage;
        }
    }
    
ChatManager.java
    public class ChatManager {
        @Inject
        private PushManager pushManager;
       
        public void publicChat(String message) {
            pushManager.broadcast("chat", message);
        }
       
        public void privateChat(String userId, String message) {
            pushManager.service("privateChat" + userId, message);
        }
    }

