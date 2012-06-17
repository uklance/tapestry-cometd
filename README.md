tapestry-cometd
===============

A push implementation for [tapestry](http://tapestry.apache.org/) based on [cometd](http://cometd.org/)

<font color="red">This project is a work in progress and does not work in it's current state</font>

Usage:

Page.tml

    <t:zone 
        t:mixins="cometd/Push" 
        t:topic="publicChat"
        t:event="processChat" 
        t:append="true" 
        t:session="false"
    />
    <t:block t:id="messageBlock">
        ${chatMessage}
    </t:block>

Page.java

    public class Page {
        @Property
        private String chatMessage;
        
        @SessionState
        @Property
        private User user;
        
        @Inject
        private Block messageBlock;
        
        @OnEvent("processChat")
        Block onProcessChat(String chatMessage) {
            this.chatMessage = chatMessage;
            return messageBlock;
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

