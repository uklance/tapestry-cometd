tapestry-cometd
===============

A push implementation for [tapestry](http://tapestry.apache.org/) based on [cometd](http://cometd.org/)
**This project is a work in progress and does not work in it's current state**

Usage:

Page.tml

    <t:zone 
        t:mixins="cometd/Push" 
        t:topic="publicChat"
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
        
        @SessionState
        @Property
        private User user;
        
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
            pushManager.broadcast("publicChat", message);
        }
    }

