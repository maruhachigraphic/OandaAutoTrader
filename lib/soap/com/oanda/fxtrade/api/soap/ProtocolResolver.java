/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public interface ProtocolResolver {

	String resolve(String request);

	void shutDown();

	void setLogoutCommandHandler(DestroyHandler logoutHandler);

}
