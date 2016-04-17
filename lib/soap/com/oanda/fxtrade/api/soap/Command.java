/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public class Command {

    public static final String SET_WITH_RATE_THREAD      = "setWithRateThread";
    public static final String SET_TIMEOUT               = "setTimeout";
    public static final String SET_RATE_TIMEOUT          = "setRateTimeout";
    public static final String LOGIN                     = "login";
    public static final String LOGOUT                    = "logout";
    public static final String IS_LOGGED_IN              = "isLoggedIn";
    public static final String GET_PAIRS                 = "getPairs";
    public static final String GET_RATE                  = "getRate";
    public static final String GET_RATE_FOR_UNITS        = "getRateForUnits";
    public static final String GET_RATES_FOR_ALL_UNITS   = "getRatesForAllUnits";
    public static final String GET_ACCOUNT_LIST          = "getAccountsList";
    public static final String GET_ACCOUNT               = "getAccount";
    public static final String GET_SERVER_TIME           = "getServerTime";
    public static final String GET_HISTORY_POINTS        = "getHistoryPoints";
    public static final String GET_TRADES                = "getTrades";
    public static final String GET_ORDERS                = "getOrders";
    public static final String TRADE_ORDER               = "tradeOrder";
    public static final String CHANGE_TRADE              = "changeTrade";
    public static final String CLOSE_TRADE               = "closeTrade";
    public static final String LIMIT_ORDER               = "limitOrder";
    public static final String CHANGE_ORDER              = "changeOrder";
    public static final String CLOSE_ORDER               = "closeOrder";
    public static final String CREATE                    = "create";
    public static final String DESTROY                   = "destroy";
    public static final String GET_TRANSACTIONS          = "getTransactions";
    public static final String SUBSCRIBE_LISTENER        = "subscribeListener";
	public static final String GET_EVENTS                = "getEvents";
	public static final String GET_POSITIONS             = "getPositions";
	public static final String GET_INSTRUMENT            = "getInstrument";
	public static final String GET_CANDLES               = "getCandles";
	public static final String UNSUBSCRIBE_LISTENER      = "unsubscribeListener";
	public static final String SET_LOGFILE               = "setLogfile";
	public static final String SET_WITH_KEEPALIVE_THREAD = "setWithKeepAliveThread";
	public static final String GET_MINMAXS               = "getMinMaxs";
	public static final String GET_USER_INFO             = "getUserInfo";
	public static final String SET_PROFILE               = "setProfile";
	public static final String GET_SESSION_KEY           = "getSessionKey";
	public static final String GET_ALL_SYMBOLS           = "getAllSymbols";
	public static final String GET_TRAILING_STOP         = "getTrailingStop";
}
