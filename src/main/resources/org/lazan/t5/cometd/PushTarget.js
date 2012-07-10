Tapestry.Initializer.push = function(spec)
{
	var cometd = $.cometd;
    cometd.configure(spec.configureOptions);
	cometd.addListener('/meta/handshake', function(handshake) {
		if (handshake.successful === true) {
			cometd.startBatch();
			var subSpecs = spec.subSpecs;
			for (var i = 0; i < subSpecs.length; ++i) {
				var subSpec = subSpecs[i];
				var callback = function(message) {
					//alert('message: ' + message.data.content);
					var clientId = subSpec.clientId;
					if (message.data.content) {
						if (subSpec.update == 'append') {
							$('#' + clientId).append(message.data.content);
						} else if (subSpec.update == 'prepend') {
							$('#' + clientId).prepend(message.data.content);
						} else {
							$('#' + clientId).html(message.data.content);
						}
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
					$.tapestry.utils.loadScriptsInReply(message.data);
				};
				cometd.subscribe(subSpec.initData.channelId, undefined, callback, { data: subSpec.initData });
			}
			cometd.endBatch();
		}
	});
	cometd.handshake();
};