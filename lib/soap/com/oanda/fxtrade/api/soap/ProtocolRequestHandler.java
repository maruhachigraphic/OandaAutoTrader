/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public interface ProtocolRequestHandler {
    public String[] handle(String command, String[] arguments);

	public void shutDown();
}
