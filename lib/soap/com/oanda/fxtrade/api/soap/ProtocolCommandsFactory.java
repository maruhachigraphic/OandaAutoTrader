/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.util.HashMap;
import java.util.Map;

public class ProtocolCommandsFactory {
    private Map<String, ProtocolCommandExecutor> commands = new HashMap<String, ProtocolCommandExecutor>();

    private ProtocolCommands Commands = new ProtocolCommands();

	private ProtocolCommandExecutor unsupportedCommand;

    ProtocolCommandsFactory(Session session) {
    	unsupportedCommand = Commands.new UnsupportedCommand(session);

        commands.put(Command.SET_WITH_RATE_THREAD     , Commands.new SetWithRateThread     (session));
        commands.put(Command.SET_TIMEOUT              , Commands.new SetTimeout            (session));
        commands.put(Command.SET_RATE_TIMEOUT         , Commands.new SetRateTimeout        (session));
        commands.put(Command.LOGIN                    , Commands.new Login                 (session));
        commands.put(Command.LOGOUT                   , Commands.new Logout                (session));
        commands.put(Command.IS_LOGGED_IN             , Commands.new IsLoggedIn            (session));
        commands.put(Command.GET_PAIRS                , Commands.new GetPairs              (session));
        commands.put(Command.GET_RATE                 , Commands.new GetRate               (session));
        commands.put(Command.GET_RATE_FOR_UNITS       , Commands.new GetRateForUnits      (session));
        commands.put(Command.GET_RATES_FOR_ALL_UNITS  , Commands.new GetRatesForAllUnits  (session));
        commands.put(Command.GET_ACCOUNT_LIST         , Commands.new GetAccountsList       (session));
        commands.put(Command.GET_ACCOUNT              , Commands.new GetAccount            (session));
        commands.put(Command.GET_SERVER_TIME          , Commands.new GetServerTime         (session));
        commands.put(Command.GET_HISTORY_POINTS       , Commands.new GetHistoryPoints      (session));
        commands.put(Command.GET_TRADES               , Commands.new GetTrades             (session));
        commands.put(Command.GET_ORDERS               , Commands.new GetOrders             (session));
        commands.put(Command.TRADE_ORDER              , Commands.new TradeOrder            (session));
        commands.put(Command.CHANGE_TRADE             , Commands.new ChangeTrade           (session));
        commands.put(Command.CLOSE_TRADE              , Commands.new CloseTrade            (session));
        commands.put(Command.LIMIT_ORDER              , Commands.new LimitOrder            (session));
        commands.put(Command.CHANGE_ORDER             , Commands.new ChangeOrder           (session));
        commands.put(Command.CLOSE_ORDER              , Commands.new CloseOrder            (session));
        commands.put(Command.CREATE                   , Commands.new Create                (session));
        commands.put(Command.DESTROY                  , Commands.new Destroy               (session));
        commands.put(Command.GET_TRANSACTIONS         , Commands.new GetTransactions       (session));
        commands.put(Command.SUBSCRIBE_LISTENER       , Commands.new SubscribeListener     (session));
        commands.put(Command.UNSUBSCRIBE_LISTENER     , Commands.new UnsubscribeListener   (session));
        commands.put(Command.GET_EVENTS               , Commands.new GetEvents             (session));
        commands.put(Command.GET_POSITIONS            , Commands.new GetPositions          (session));
        commands.put(Command.GET_INSTRUMENT           , Commands.new GetInstrument         (session));
        commands.put(Command.GET_CANDLES              , Commands.new GetCandles            (session));
        commands.put(Command.SET_LOGFILE              , Commands.new SetLogfile            (session));
        commands.put(Command.SET_WITH_KEEPALIVE_THREAD, Commands.new SetWithKeepAliveThread(session));
        commands.put(Command.GET_MINMAXS              , Commands.new GetMinMaxs            (session));
        commands.put(Command.GET_USER_INFO            , Commands.new GetUserInfo           (session));
        commands.put(Command.SET_PROFILE              , Commands.new SetProfile            (session));
        commands.put(Command.GET_SESSION_KEY          , Commands.new GetSessionKey         (session));
        commands.put(Command.GET_ALL_SYMBOLS          , Commands.new GetAllSymbols         (session));
        commands.put(Command.GET_TRAILING_STOP        , Commands.new GetTrailingStop       (session));
    }

    ProtocolCommandExecutor create(String command) {
    	if(commands.containsKey(command)) {
    		return commands.get(command);
    	}
    	else {
    		Logger.getInstance().log(getClass().getName() + "::create(): '" + command + "' is not supported");
    		return unsupportedCommand;
    	}
    }
}
