tapestry-cometd
===============

### A [push](http://en.wikipedia.org/wiki/Comet_%28programming%29) implementation for [Tapestry](http://tapestry.apache.org/) based on [CometD](http://cometd.org/)

## Features:
* Zero Javascript required
* Fully configurable
* Use [Tapestry templates](http://tapestry.apache.org/component-templates.html) to format incoming messages
* [POJO](http://en.wikipedia.org/wiki/Plain_Old_Java_Object) messages (use strings or custom objects)
* Build on top of the rock solid [CometD](http://cometd.org/) library
* Supports [Jetty Continuations](http://wiki.eclipse.org/Jetty/Feature/Continuations)
* Topic abstraction (on top of [CometD channels](http://cometd.org/documentation/cometd-java/server/channel))
* Choice of client update strategies when a message arrives ([append](http://api.jquery.com/append/), [prepend](http://api.jquery.com/prepend/) or [replace](http://api.jquery.com/html/))
* Custom authorization ([TODO](https://github.com/uklance/tapestry-cometd/issues/21))
* Custom message listeners ([TODO](https://github.com/uklance/tapestry-cometd/issues/21))

## Usage:

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

	<!-- this component subscribes to the 'chatTopic' topic and appends received messages to itself -->
	<t:cometd.push topic="chatTopic" event="chat" update="APPEND" />

	<!-- this template is applied to each chat message when it is received -->
	<t:block t:id="messageBlock">
		<h2>${message}</h2>
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
	
	// this event is fired when a message is received on the 'chatTopic' topic
	Block onChat(String message) {
		this.message = message;
		return messageBlock;
	}

	// this event is fired when the form is posted
	Block onSuccess() {
		// broadcast the message on the 'chatTopic' topic
		pushManager.broadcast("chatTopic", message);
		return formZone.getBody();
	}
}
```
