![Tapestry CometD](https://github.com/uklance/tapestry-cometd/raw/master/logo-m.jpg)

### A [push](http://en.wikipedia.org/wiki/Push_technology) library for [Tapestry](http://tapestry.apache.org/) based on [CometD](http://cometd.org/)

## Features:
* Zero Javascript required
* Fully configurable
* Use [Tapestry templates](http://tapestry.apache.org/component-templates.html) to format incoming messages
* [POJO](http://en.wikipedia.org/wiki/Plain_Old_Java_Object) messages (use strings or custom objects)
* Built on top of the rock solid [CometD](http://cometd.org/) library
* Supports [Jetty Continuations](http://wiki.eclipse.org/Jetty/Feature/Continuations)
* [Topic](https://github.com/uklance/tapestry-cometd/wiki/Topics) abstraction (on top of [CometD channels](http://cometd.org/documentation/cometd-java/server/channel))
* Choice of client update strategies when a message arrives ([append](http://api.jquery.com/append/), [prepend](http://api.jquery.com/prepend/) or [replace](http://api.jquery.com/html/))
* Custom [Authorizers](https://github.com/uklance/tapestry-cometd/wiki/Authorizers)
* Custom [Subscription Listeners](https://github.com/uklance/tapestry-cometd/wiki/Subscription-Listeners)

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

## Maven
```xml
<dependencies>
	<dependency>
		<groupId>org.lazan</groupId>
		<artifactId>tapestry-cometd</artifactId>
		<version>0.9.9</version>
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
		<url>https://github.com/uklance/releases/raw/master</url>
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

## Demo

Download the tapestry-cometd-demo from [here](https://github.com/uklance/tapestry-cometd-demo) and run the 
following from the command line.
```
mvn jetty:run
```
After jetty starts, point your browser to 
* [http://localhost:8080/tapestry-cometd-demo/Stocks](http://localhost:8080/tapestry-cometd-demo/Stocks)
* [http://localhost:8080/tapestry-cometd-demo/Chat](http://localhost:8080/tapestry-cometd-demo/Chat)

Note: You will see exceptions being logged if you run through maven due to an old version of jetty.
You can use the [run jetty run](http://code.google.com/p/run-jetty-run/) eclipse plugin to quickly run a
newer version of jetty. There is an [issue](https://github.com/uklance/tapestry-cometd/issues/28) to 
support older servlet containers.

## Links
* [Configuring the CometD Servlet](https://github.com/uklance/tapestry-cometd/wiki/Configuring-the-CometD-Servlet)
* [Authorizers](https://github.com/uklance/tapestry-cometd/wiki/Authorizers)
* [Subscription Listeners](https://github.com/uklance/tapestry-cometd/wiki/Subscription-Listeners)
* [Topics](https://github.com/uklance/tapestry-cometd/wiki/Topics)
* [FAQ](https://github.com/uklance/tapestry-cometd/wiki/FAQ)
* [Pushing javascript to the client](https://github.com/uklance/tapestry-cometd/wiki/Pushing-javascript-to-the-client)