package org.lazan.t5.cometd.components;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Any;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library={
		"classpath:/org/lazan/t5/cometd/cometd-namespace.js",
		"classpath:/org/lazan/t5/cometd/cometd-header.js",
		"classpath:/org/lazan/t5/cometd/Cometd.js",
		"classpath:/org/lazan/t5/cometd/cometd-json.js",
		"classpath:/org/lazan/t5/cometd/CallbackPollingTransport.js",
		"classpath:/org/lazan/t5/cometd/LongPollingTransport.js",
		"classpath:/org/lazan/t5/cometd/RequestTransport.js",
		"classpath:/org/lazan/t5/cometd/Transport.js",
		"classpath:/org/lazan/t5/cometd/TransportRegistry.js",
		"classpath:/org/lazan/t5/cometd/Utils.js",
		"classpath:/org/lazan/t5/cometd/WebSocketTransport.js",
		"classpath:/org/lazan/t5/cometd/push.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-ack.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-reload.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timestamp.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timesync.js",
})
public class Push extends Any	{
    @Inject
    private ComponentResources resources;
	
    @Inject
	private JavaScriptSupport jss;
	
	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private String topic;
	
	@Parameter
	private boolean session;
	
	@Inject
	private Request request;
	
	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private String event;
	
	@Inject
	private BaseURLSource baseUrlSource;
	
    @BeginRender
	void beginRender() {
		String cometdPath = String.format("%s%s/cometd", baseUrlSource.getBaseURL(false), request.getContextPath()); // TODO
		String clientId = getClientId();
		String channelId = String.format("/%s/%s", resources.getCompleteId(), clientId);
		JSONObject spec = new JSONObject(
				"cometdPath", cometdPath,
				"activePageName", resources.getPageName(), // PushDemo
				"containingPageName", resources.getPageName(), // PushDemo
				"nestedComponentId", resources.getNestedId(), // ""
				"eventType", event, // "chat"
				"session", String.valueOf(session),
				"channelId", channelId,
				"topic", topic,
				"clientId", clientId);
		jss.addInitializerCall("push", spec);
		
		//System.out.println(String.format("completeId: %s, clientId: %s, topic: %s, append: %s", completeId, zoneClientId, topic, append));
	}
}
