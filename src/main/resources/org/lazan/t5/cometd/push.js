Tapestry.Initializer.push = function(spec)
{
	cometd.configure('http://localhost:8080/cometd');
	cometd.cometd.addListener('/meta/connect', function(message) {
		alert('Connect');
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
			alert('Message Received ' + data);
		}); 
		cometd.endBatch();
	});
	alert('Handshake()');
	cometd.handshake();
};