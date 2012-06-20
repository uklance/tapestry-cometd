tapestry-cometd
===============

A [push](http://en.wikipedia.org/wiki/Comet_%28programming%29) implementation for [tapestry](http://tapestry.apache.org/) based on [cometd](http://cometd.org/)

Usage:

Page.tml
```xml
<html 
      xmlns:t="http://tapestry.apache.org/schema/tapestry_5_3.xsd"
      xmlns:p="tapestry:parameter">

	<t:zone t:id="formZone" id="formZone">
		<!-- type a chat message in this form -->
		<form t:id="ajaxForm" t:type="form" t:zone="formZone">
			Message: <input t:type="TextField" t:id="message" /><input type="submit" value="Send"/>
		</form>
	</t:zone>

	<!-- all chats messages will be appended to this div -->
	<t:cometd.push topic="chatTopic" event="chat" update="APPEND" />

	<!-- this template is applied to each chat message -->
	<t:block t:id="messageBlock">
		<h2>1: ${message}</h2>
	</t:block>
</html>
```

Page.java

```java
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
```
