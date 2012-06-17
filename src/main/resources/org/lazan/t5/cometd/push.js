Tapestry.Initializer.push = function(spec)
{
	var cometd = jQuery.cometd;
	cometd.configure(spec.cometdPath);
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
				alert('message: ' + message.data.content);
			});
			cometd.endBatch();
		}
	});
	cometd.handshake();
};