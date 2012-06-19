Tapestry.Initializer.push = function(spec)
{
	var cometd = $.cometd;
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
				var clientId = spec.clientId;
				if (message.data.content) {
					var html = $('#' + clientId).append(message.data.content);
				} else if (message.data.zones) {
					// perform multi zone update
					/*
					$.each(message.data.zones, function(clientId, content){
						if (clientId === "" || ! $('#' + clientId).length) {
							that.applyContentUpdate(content);
						} else {
							$('#' + clientId).tapestryZone("applyContentUpdate", content);
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