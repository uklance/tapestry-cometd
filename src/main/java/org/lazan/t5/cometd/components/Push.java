package org.lazan.t5.cometd.components;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Any;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.lazan.t5.cometd.PushSupport;
import org.lazan.t5.cometd.internal.PushSupportImpl;
import org.lazan.t5.cometd.services.ChannelIdSource;

/*
 */
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
		"classpath:/org/lazan/t5/cometd/WebSocketTransport.js",
		"classpath:/org/lazan/t5/cometd/Utils.js",
		"classpath:/org/lazan/t5/cometd/AckExtension.js",
		"classpath:/org/lazan/t5/cometd/ReloadExtension.js",
		"classpath:/org/lazan/t5/cometd/TimeStampExtension.js",
		"classpath:/org/lazan/t5/cometd/TimeSyncExtension.js",
		"classpath:/org/lazan/t5/cometd/push.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-ack.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-reload.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timestamp.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timesync.js",
})
public class Push extends Any	{
	public static final String INIT_CHANNEL_ID = "/service/pushInit";
	
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
	
	@Inject
	private ChannelIdSource channelIdSource;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="literal:UPDATE")
	private String update;
	
	@Inject
	private Environment environment;
	
    @BeginRender
	void beginRender() {
		PushSupport pushSupport = environment.peek(PushSupport.class);
    	JSONArray subSpecs;
		if (pushSupport == null) {
    		pushSupport = new PushSupportImpl();
    		environment.push(PushSupport.class, pushSupport);
    		
    		JSONObject spec = pushSupport.getSpec();
    		
    		spec.put("initChannelId", INIT_CHANNEL_ID);
    		spec.put("configureOptions", getConfigureOptions());
    		
    		subSpecs = new JSONArray();
    		spec.put("subSpecs", subSpecs);
    	} else {
    		subSpecs = pushSupport.getSpec().getJSONArray("subSpecs");
    	}

    	JSONObject subSpec = new JSONObject();
		subSpec.put("clientId",  getClientId());
		subSpec.put("initData", getInitData());
		subSpec.put("update", update);
    	subSpecs.put(subSpec);
	}
    
    @AfterRender
    void afterRender() {
    	PushSupport pushSupport = environment.peek(PushSupport.class);
    	if (pushSupport != null) {
    		jss.addInitializerCall("push", pushSupport.getSpec());
    		environment.pop(PushSupport.class);
    	}
    }
    
    protected JSONObject getInitData() {
    	String channelId = channelIdSource.getChannelId(resources, getClientId());
    	return new JSONObject(
			"activePageName", resources.getPageName(), // PushDemo
			"containingPageName", resources.getPageName(), // PushDemo
			"nestedComponentId", resources.getNestedId(), // ""
			"eventType", event, // "chat"
			"session", String.valueOf(session),
			"channelId", channelId,
			"topic", topic
    	);
    }
    
    protected JSONObject getConfigureOptions() {
    	JSONObject json = new JSONObject();
    	String url = String.format("%s%s/cometd", baseUrlSource.getBaseURL(false), request.getContextPath()); // TODO
		json.put("url", url);
		//json.put("logLevel", "debug");
    	return json;
    }
}
