/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public class ProtocolResolverFactory {

	public ProtocolResolver create() {
		return new DefaultProtocolResolver();
	}
}
