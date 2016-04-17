/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import com.oanda.fxtrade.api.API;
import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.FXTick;
import com.oanda.fxtrade.api.LimitOrder;
import com.oanda.fxtrade.api.MarketOrder;
import com.oanda.fxtrade.api.StopLossOrder;
import com.oanda.fxtrade.api.TakeProfitOrder;

public class DefaultApi implements ApiFactory {

	public FXClient createFXGame() {
		return API.createFXGame();
	}

	public FXPair createFXPair() {
		return API.createFXPair();
	}

	public FXPair createFXPair(String pair) {
		return API.createFXPair(pair);
	}

	public FXPair createFXPair(String base, String quote) {
		return API.createFXPair(base, quote);
	}

	public FXTick createFXTick(long timestamp, double bid, double ask) {
		return API.createFXTick(timestamp, bid, ask);
	}

	public FXClient createFXTrade() {
		return API.createFXTrade();
	}

	public LimitOrder createLimitOrder() {
		return API.createLimitOrder();
	}

	public MarketOrder createMarketOrder() {
		return API.createMarketOrder();
	}

	public StopLossOrder createStopLossOrder(double stopLoss) {
		return API.createStopLossOrder(stopLoss);
	}

	public TakeProfitOrder createTakeProfitOrder(double takeProfit) {
		return API.createTakeProfitOrder(takeProfit);
	}

}
