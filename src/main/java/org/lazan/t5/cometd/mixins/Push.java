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
		"classpath:/org/lazan/t5/cometd/jquery.cometd.js",
		"classpath:/org/lazan/t5/cometd/jquery.cometd-ack.js",
		"classpath:/org/lazan/t5/cometd/jquery.cometd-reload.js",
		"classpath:/org/lazan/t5/cometd/jquery.cometd-timestamp.js",
		"classpath:/org/lazan/t5/cometd/jquery.cometd-timesync.js",
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
		String completeId = zoneResources.getCompleteId();
		String zoneClientId = zone.getClientId();
		String channelId = String.format("/%s/%s", zoneResources.getCompleteId(), zoneClientId);
		JSONObject spec = new JSONObject(
				"activePageName", zoneResources.getPageName(), 
				"containingPageName", zoneResources.getPageName(),
				"nestedComponentId", zoneResources.getCompleteId(),
				"eventType", event,
				"session", String.valueOf(session),
				"channelId", channelId);
		jss.addInitializerCall("push", spec);
		
		System.out.println(String.format("completeId: %s, clientId: %s, topic: %s, append: %s", completeId, zoneClientId, topic, append));
	}
}
