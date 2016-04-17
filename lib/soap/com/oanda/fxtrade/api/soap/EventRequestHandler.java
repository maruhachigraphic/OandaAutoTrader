/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.util.Vector;


public class EventRequestHandler implements ProtocolRequestHandler {

	private Session session = null;
	private boolean isShuttingDown;

	public EventRequestHandler(Session session) {
		this.session = session;
	}

	public String[] handle(String command, String[] arguments) {
		Vector<String> events = new Vector<String>();

		if(command.equals("readyForEvent")) {

            if(!isShuttingDown) {
            	Events.Record eventRecord = session.getEvents().getNext();
            	events.addAll(eventRecord.toStringArray());
            }
		}
		String[] result = new String[events.size()];
		return events.toArray(result);
	}

	public void shutDown() {
		isShuttingDown = true;
	}
}
