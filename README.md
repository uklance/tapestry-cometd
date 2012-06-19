tapestry-cometd
===============

A push implementation for [tapestry](http://tapestry.apache.org/) based on [cometd](http://cometd.org/)

Usage:

Page.tml

    <html 
          xmlns:t="http://tapestry.apache.org/schema/tapestry_5_3.xsd"
          xmlns:p="tapestry:parameter">
    
    	<t:block t:id="messageBlock">
    		<h2>1: ${message}</h2>
    	</t:block>
    
    	<t:zone t:id="formZone" id="formZone">
    		<form t:id="ajaxForm" t:type="form" t:zone="formZone">
    			Message: <input t:type="TextField" t:id="message" /><input type="submit" value="Send"/>
    		</form>
    	</t:zone>
    
    	<t:cometd.push topic="chatTopic" event="chat" style="border: 1px solid" update="APPEND" />
    </html>

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

    public class PushDemo {
        @InjectComponent
    	private Zone formZone;
    	
    	@Inject
    	private Block messageBlock;
    
    	@Property
    	private String message;
    	
    	@Inject
    	private PushManager pushManager;
    	
    	Block onChat(String message) {
    		this.message = message;
    		return messageBlock;
    	}
    
    	Block onSuccess() {
    		pushManager.broadcast(topic, message);
    		return formZone.getBody();
    	}
    }
