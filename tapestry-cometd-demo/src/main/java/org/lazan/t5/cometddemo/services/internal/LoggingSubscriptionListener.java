package org.lazan.t5.cometddemo.services.internal;

import org.lazan.t5.cometd.services.PushSession;
import org.lazan.t5.cometd.services.SubscriptionListener;

public class LoggingSubscriptionListener implements SubscriptionListener {
	public void onSubscribe(PushSession pushSession) {
		System.err.println(String.format("onSubscribe(%s)", pushSession.getTopic()));
	}

	public void onUnsubscribe(PushSession pushSession) {
		System.err.println(String.format("onUnsubscribe(%s)", pushSession.getTopic()));
	}
}
