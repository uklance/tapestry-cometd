package org.lazan.t5.cometd.mixins;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.MixinAfter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.json.JSONObject;
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
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-ack.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-reload.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timestamp.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timesync.js",
		"classpath:/org/lazan/t5/cometd/push.js",
})
@MixinAfter
public class Push {
	@Inject
	private ComponentResources mixinResources;
	
	@InjectContainer
	private Zone zone;
	
	@Inject
	private JavaScriptSupport jss;
	
	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private String topic;
	
	@Parameter(value="literal:true")
	private boolean append;
	
	@Parameter
	private boolean session;
	
	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private String event;
	
	@BeginRender
	void beginRender() {
		ComponentResources zoneResources = mixinResources.getContainerResources();
		String cometdPath = "http://localhost:8080/tapestry-sandbox/cometd"; // TODO
		String completeId = zoneResources.getCompleteId();
		String zoneClientId = zone.getClientId();
		String channelId = String.format("/%s/%s", zoneResources.getCompleteId(), zoneClientId);
		JSONObject spec = new JSONObject(
				"cometdPath", cometdPath,
				"activePageName", zoneResources.getPageName(), 
				"containingPageName", zoneResources.getPageName(),
				"nestedComponentId", zoneResources.getCompleteId(),
				"eventType", event,
				"session", String.valueOf(session),
				"channelId", channelId,
				"topic", topic);
		jss.addInitializerCall("push", spec);
		
		System.out.println(String.format("completeId: %s, clientId: %s, topic: %s, append: %s", completeId, zoneClientId, topic, append));
	}
}
