/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public interface ProtocolCommandExecutor {
    String[] execute(String[] commandArguments);
}
