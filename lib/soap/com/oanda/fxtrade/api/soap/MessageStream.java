/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.IOException;

public interface MessageStream {

	String readNext() throws IOException;
	void write(String response);
	void close();
}
