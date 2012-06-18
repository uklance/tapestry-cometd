Tapestry.Initializer.push = function(spec)
{
	var cometd = $.cometd;
    cometd.registerTransport('long-polling', new org.cometd.LongPollingTransport());
    cometd.registerTransport('callback-polling', new org.cometd.CallbackPollingTransport());
    cometd.configure({url: spec.cometdPath, logLevel: 'debug'});
	cometd.addListener('/meta/handshake', function(handshake) {
		if (handshake.successful === true) {
			var data = {
				activePageName: spec.activePageName,
				containingPageName: spec.containingPageName,
				nestedComponentId: spec.nestedComponentId,
				eventType: spec.eventType,
				session: spec.session,
				channelId: spec.channelId,
				topic: spec.topic
			};
			cometd.startBatch();
			cometd.publish('/service/pushInit', data);
			cometd.subscribe(spec.channelId, function(message) {
				//alert('message: ' + message.data.content);
				var zoneId = spec.zoneId;
				if (message.data.content) {
					$('#' + zoneId).tapestryZone("applyContentUpdate", message.data.content);
				} else if (message.data.zones) {
					// perform multi zone update
					/*
					$.each(message.data.zones, function(zoneId, content){
						if (zoneId === "" || ! $('#' + zoneId).length) {
							that.applyContentUpdate(content);
						} else {
							$('#' + zoneId).tapestryZone("applyContentUpdate", content);
						}
					});
					*/
					// TODO
				}
				//TODO
				//$.tapestry.utils.loadScriptsInReply(message.data, specs.callback);
			});
			cometd.endBatch();
		}
	});
	cometd.handshake();
};