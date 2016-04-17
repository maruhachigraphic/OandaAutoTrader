/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import com.oanda.fxtrade.api.FXAccountEvent;
import com.oanda.fxtrade.api.FXEventInfo;
import com.oanda.fxtrade.api.FXEventManager;

public class AccountEventListener extends FXAccountEvent {
	private Events events = null;
	private String uid    = null;

	public AccountEventListener(Events events, String uid) {
		this.events = events;
		this.uid    = uid;
	}

	public void handle(FXEventInfo eventInfo, FXEventManager EM) {
        events.add(uid, eventInfo, "ACCOUNT_EVENT");
	}

	public boolean match(FXEventInfo EI) {
		return true;
	}
}
