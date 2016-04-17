/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.FXTick;
import com.oanda.fxtrade.api.LimitOrder;
import com.oanda.fxtrade.api.MarketOrder;
import com.oanda.fxtrade.api.StopLossOrder;
import com.oanda.fxtrade.api.TakeProfitOrder;


public interface ApiFactory {
	/* factory methods */
	public FXClient        createFXGame ();
	public FXClient        createFXTrade();
	public FXPair          createFXPair ();
	public FXPair          createFXPair(String pair);
	public FXPair          createFXPair(String base, String quote);
	public LimitOrder      createLimitOrder();
	public MarketOrder     createMarketOrder();
	public StopLossOrder   createStopLossOrder(double stopLoss);
	public TakeProfitOrder createTakeProfitOrder(double takeProfit);
	public FXTick          createFXTick(long timestamp, double bid, double ask);
}
