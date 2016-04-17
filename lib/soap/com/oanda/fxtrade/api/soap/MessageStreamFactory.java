/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public class MessageStreamFactory {

	public MessageStream create(Connection connection) {
		return new DefaultMessageStream(connection);
	}

}
