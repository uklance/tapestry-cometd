![Tapestry CometD](https://github.com/uklance/tapestry-cometd/raw/master/logo-m.jpg)

### A [push](http://en.wikipedia.org/wiki/Push_technology) library for [Tapestry5](http://tapestry.apache.org/) based on [CometD](http://cometd.org/)

## Features:
* Create push applications without writing a single line of Javascript
* Fully configurable
* Use [Tapestry templates](http://tapestry.apache.org/component-templates.html) to format incoming messages
* [POJO](http://en.wikipedia.org/wiki/Plain_Old_Java_Object) messages (use strings or custom objects)
* Built on top of the rock solid [CometD](http://cometd.org/) library
* Supports [Jetty Continuations](http://wiki.eclipse.org/Jetty/Feature/Continuations)
* [Topic](https://github.com/uklance/tapestry-cometd/wiki/Topics) abstraction (on top of [CometD channels](http://cometd.org/documentation/cometd-java/server/channel))
* Choice of client update strategies when a message arrives ([append](http://api.jquery.com/append/), [prepend](http://api.jquery.com/prepend/) or [replace](http://api.jquery.com/html/))
* Custom [Authorizers](https://github.com/uklance/tapestry-cometd/wiki/Custom-Authorizers)
* Custom [Subscription Listeners](https://github.com/uklance/tapestry-cometd/wiki/Custom-Subscription-Listeners)
* Push [dynamic javascript](https://github.com/uklance/tapestry-cometd/wiki/Pushing-javascript-to-the-client) to the client

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

	<!-- this PushTarget subscribes to the '/chatTopic' topic and appends received messages to itself -->
	<t:cometd.PushTarget topic="/chatTopic" event="chat" update="append" />

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
		pushManager.broadcast("/chatTopic", message);
		return formZone.getBody();
	}
}
```

## Demo

See a live demo running [here](http://t5cometd-lazan.rhcloud.com)

## Maven
```xml
<dependencies>
	<dependency>
		<groupId>org.lazan</groupId>
		<artifactId>tapestry-cometd</artifactId>
		<!-- 
			lookup latest version at 
			https://github.com/uklance/releases/tree/master/org/lazan/tapestry-cometd 
		-->
		<version>...</version> 
	</dependency>

	<dependency>
		<groupId>org.got5</groupId>
		<artifactId>tapestry5-jquery</artifactId>
		<version>3.0.0</version>
	</dependency>
</dependencies>

...

<repositories>
	<repository>
		<id>tapestry-cometd</id>
		<url>https://raw.github.com/uklance/releases/master</url>
	</repository>
	<repository>
		<id>devlab722-repo</id>
		<url>http://nexus.devlab722.net/nexus/content/repositories/releases</url>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
	</repository>
</repositories>
```

## Links
* [Custom Authorizers](https://github.com/uklance/tapestry-cometd/wiki/Custom-Authorizers)
* [Custom Subscription Listeners](https://github.com/uklance/tapestry-cometd/wiki/Custom-Subscription-Listeners)
* [Topics](https://github.com/uklance/tapestry-cometd/wiki/Topics)
* [Pushing javascript to the client](https://github.com/uklance/tapestry-cometd/wiki/Pushing-javascript-to-the-client)
* [Configuring the CometD Servlet](https://github.com/uklance/tapestry-cometd/wiki/Configuring-the-CometD-Servlet)
* [FAQ](https://github.com/uklance/tapestry-cometd/wiki/FAQ)
