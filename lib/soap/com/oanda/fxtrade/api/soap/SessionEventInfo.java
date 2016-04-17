/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import com.oanda.fxtrade.api.FXEventInfo;
import com.oanda.fxtrade.api.FXEventKey;

public class SessionEventInfo implements FXEventInfo {

	private String eventText;

	public SessionEventInfo(String eventText) {
		this.eventText = eventText;
	}

	public int compareTo(Object other) {
		return 0;
	}

	public FXEventKey getKey() {
		return null;
	}

	public long getTimestamp() {
		return 0;
	}

	public String getEventText() {
		return eventText;
	}
}
