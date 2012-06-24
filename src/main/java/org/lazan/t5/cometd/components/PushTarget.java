package org.lazan.t5.cometd.components;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Any;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.lazan.t5.cometd.PushSupport;
import org.lazan.t5.cometd.UpdateStrategy;
import org.lazan.t5.cometd.internal.PushSupportImpl;
import org.lazan.t5.cometd.services.ChannelIdSource;

// TODO: Investigate using a stack for this
// TODO: work out if all of these js files are actually required
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
		"classpath:/org/lazan/t5/cometd/PushTarget.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-ack.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-reload.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timestamp.js",
		"classpath:/org/lazan/t5/cometd/jquery/jquery.cometd-timesync.js",
})
public class PushTarget extends Any	{
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
	
	// TODO: use an enum for this
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="literal:replace")
	private UpdateStrategy update;
	
	@Inject
	private Environment environment;
	
	@BeginRender
	void beginRender() {
		// append config to a single PushSupport instance for all PushTargets
		PushSupport pushSupport = environment.peek(PushSupport.class);
		if (pushSupport == null) {
    		pushSupport = new PushSupportImpl();
    		environment.push(PushSupport.class, pushSupport);
    		
    		JSONObject spec = pushSupport.getSpec();
    		spec.put("configureOptions", getConfigureOptions());
    	}

    	JSONObject subSpec = new JSONObject();
		subSpec.put("clientId",  getClientId());
		subSpec.put("initData", getInitData());
		subSpec.put("update", update.name());
    	pushSupport.getSpec().append("subSpecs", subSpec);
	}
    
    @AfterRender
    void afterRender() {
    	PushSupport pushSupport = environment.peek(PushSupport.class);
    	if (pushSupport != null) {
    		// if there are multiple push targets on the page, only a single javascript
    		// initialization is done
    		jss.addInitializerCall("push", pushSupport.getSpec());
    		environment.pop(PushSupport.class);
    	}
    }
    
    protected JSONObject getInitData() {
    	String channelId = channelIdSource.getChannelId(resources, getClientId());
    	return new JSONObject(
			"activePageName", resources.getPageName(),
			"containingPageName", resources.getPageName(),
			"nestedComponentId", resources.getNestedId(),
			"eventType", event,
			"session", String.valueOf(session),
			"channelId", channelId,
			"topic", topic
    	);
    }
    
    // TODO: https://github.com/uklance/tapestry-cometd/issues/12
    protected JSONObject getConfigureOptions() {
    	JSONObject json = new JSONObject();
    	String url = String.format("%s%s/cometd", baseUrlSource.getBaseURL(false), request.getContextPath()); // TODO
		json.put("url", url);
		//json.put("logLevel", "debug");
    	return json;
    }
}
