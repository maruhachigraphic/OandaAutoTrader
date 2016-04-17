/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import com.oanda.fxtrade.api.FXEventInfo;
import com.oanda.fxtrade.api.FXEventManager;
import com.oanda.fxtrade.api.FXRateEvent;

public class RateEventListener extends FXRateEvent {
	private Events events = null;
	private String uid    = null;

	public RateEventListener(Events events, String uid) {
		this.events = events;
		this.uid    = uid;
	}

	public void handle(FXEventInfo eventInfo, FXEventManager EM) {
		events.add(uid, eventInfo, "RATE_EVENT");
	}

	public boolean match(FXEventInfo EI) {
		return true;
	}

}
