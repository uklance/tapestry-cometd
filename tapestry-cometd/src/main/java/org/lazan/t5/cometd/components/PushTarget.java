package org.lazan.t5.cometd.components;

import static org.lazan.t5.cometd.services.CometdConstants.DATA_ACTIVE_PAGE_NAME;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_CHANNEL_ID;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_CONTAINING_PAGE_NAME;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_EVENT_TYPE;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_NESTED_COMPONENT_ID;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_PAGE_ACTIVATION_CONTEXT;
import static org.lazan.t5.cometd.services.CometdConstants.DATA_TOPIC;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
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
import org.lazan.t5.cometd.services.PageGlobals;

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
	
	@Inject
	private Request request;
	
	@Inject
	private BaseURLSource baseUrlSource;
	
	@Inject
	private ChannelIdSource channelIdSource;
	
	@Inject
	private PageGlobals pageGlobals;
	
	@Inject
	private Environment environment;

	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="literal:replace")
	private UpdateStrategy update;
	
	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private String topic;

	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private String event;
	
	@Parameter
	private boolean secure;
	
	@BeginRender
	void beginRender() {
		// use a single config object for all PushTargets on the page
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
    		// only one javascript initialization for all PushTargets on the page
    		jss.addInitializerCall("push", pushSupport.getSpec());
    		environment.pop(PushSupport.class);
    	}
    }
    
    protected JSONObject getInitData() {
    	String channelId = channelIdSource.nextChannelId(topic);
    	EventContext pageActivationContext = pageGlobals.getPageActivationContext();
    	JSONObject initData =  new JSONObject(
    		DATA_CHANNEL_ID, channelId,
			DATA_ACTIVE_PAGE_NAME, resources.getPageName(),
			DATA_CONTAINING_PAGE_NAME, resources.getPageName(),
			DATA_NESTED_COMPONENT_ID, resources.getNestedId(),
			DATA_EVENT_TYPE, event,
			DATA_TOPIC, topic
    	);
    	
    	// add the page activation context to the data
    	for (int i = 0; i < pageActivationContext.getCount(); ++i) {
    		String contextString = pageActivationContext.get(String.class, i);
    		initData.append(DATA_PAGE_ACTIVATION_CONTEXT, contextString);
    	}
    	return initData;
    }
    
    // TODO: https://github.com/uklance/tapestry-cometd/issues/12
    protected JSONObject getConfigureOptions() {
    	JSONObject json = new JSONObject();
    	String url = String.format("%s%s/cometd", baseUrlSource.getBaseURL(secure), request.getContextPath());
		json.put("url", url);
		//json.put("logLevel", "debug");
    	return json;
    }
}
