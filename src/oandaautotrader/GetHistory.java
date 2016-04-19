/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oandaautotrader;

import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.FXHistoryPoint;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.OAException;
import com.oanda.fxtrade.api.RateTable;
import java.util.ArrayList;
import java.util.Vector;

/**
 * 日足のセットを加工し、ArrayList＜String[]＞型で返すクラス
 * String[]の中身は[unixtime:open:max:min:close]となる
 *
 * @author maruhachi
 */
public class GetHistory {

    /**
     *
     * @param OAT OandaAutoTrader内のフィールドを取得する
     * @param rateTable 呼び出し元から逐一rateTableを取得する
     * @param interval_history 呼び出し元からインターバル時間を取得する
     * @param tick_history 呼び出し元からtick数を取得する
     * @return ArrayList＜String[]＞型で返す String[]の中身は[unixtime:open:max:min:close]
     */
    //public ArrayList<String[]> getHistory(FXClient fxclient, RateTable rateTable, FXPair fxpair, long interval_history, int tick_history) {
    public ArrayList<String[]> getHistory(OandaAutoTrader OAT, RateTable rateTable, long interval_history, int tick_history) {
        FXClient fxclient = OAT.fxclient;
        FXPair fxpair = OAT.fxpair;
        //このクラスのフィールドhiashiArrayListに代入するため、ローカルなインスタンスを準備
        ArrayList<String[]> hiashiArrayListLocal = new ArrayList<>();

        if (!fxclient.isLoggedIn()) {
            return null;
        }

        // make the history request
        // the FXTrade object returns as many FXHistoryPoints as possible up to the number requested,
        //System.out.println("OandaAutoTrader: rate history request...");
        Vector<? extends FXHistoryPoint> history = null;
        try {
            //history = fxclient.getHistory(pair, interval, ticks); // this call has been deprecated
            //！！！！！注）rateTableはinitメソッドから取得している　！！！！！
            history = rateTable.getHistory(fxpair, interval_history, tick_history);
        } catch (OAException se) {
            System.err.println("OandaAutoTrader: rate history request failed: " + se);
        }

        if (history != null && history.size() > 0) {
            /*
             System.out.println("Time|MaxBid|MaxAsk|OpenBid|OpenAsk|CloseBid|CloseAsk|MinBid|MinAsk|Volume");
             System.out.println("------------------------------------------------------------------");
             int i = 0;
             for (; i < history.size() - 1; i++) {
             System.out.println(history.elementAt(i));
             }
             System.out.println("------------------------------------------------------------------");
             */
            for (FXHistoryPoint myFXH : history) {
                String[] datain = new String[5];
                //Date past = new Date(myFXH.getTimestamp() * 1000);//getTimestampで取得した値をx1000でDate型に代入すると、普通の日付になる。
                //time,open,high,low,close
                //System.out.println(myFXH.getTimestamp() + "," + myFXH.getCandlePoint().getOpen() + "," + myFXH.getCandlePoint().getMax() + "," + myFXH.getCandlePoint().getMin() + "," + myFXH.getCandlePoint().getClose());
                datain[0] = String.valueOf(myFXH.getTimestamp());
                datain[1] = String.valueOf(myFXH.getCandlePoint().getOpen());
                datain[2] = String.valueOf(myFXH.getCandlePoint().getMax());
                datain[3] = String.valueOf(myFXH.getCandlePoint().getMin());
                datain[4] = String.valueOf(myFXH.getCandlePoint().getClose());
                //hiashiArrayList.add(myFXH.getTimestamp() + "," + myFXH.getCandlePoint().getOpen() + "," + myFXH.getCandlePoint().getMax() + "," + myFXH.getCandlePoint().getMin() + "," + myFXH.getCandlePoint().getClose());
                hiashiArrayListLocal.add(datain);
                //System.out.print("hiashiArrayListLocalにデータをセット");
            }

            // The last tick is a state 'tick' that represents the current, incomplete tick
            //System.out.println(history.elementAt(i) + " (Current State)");
            //System.out.println(history.size() + " pointsを得られました。");
        }
        
        OAT.HiashiList = hiashiArrayListLocal;//OandaAutoTraderのHiashiListに代入
        
        return hiashiArrayListLocal;
    }

}
