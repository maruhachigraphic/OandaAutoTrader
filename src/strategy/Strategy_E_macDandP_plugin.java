/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import com.oanda.fxtrade.api.API;
import com.oanda.fxtrade.api.Account;
import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.RateTable;
import com.oanda.fxtrade.api.SessionDisconnectedException;
import indicators.MACD;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import oandaautotrader.GetHistory;
import oandaautotrader.OandaAutoTrader;
import oandaautotrader.TimeGetter;

/**
 * ストラテジー　デモプラグイン。newして使用する。 <br>これはテストストラテジーEです。 MACDとMACP(移動平均乖離率)を使用します。
 * @author maruhachi
 */
public class Strategy_E_macDandP_plugin extends StrategyTemplate implements Runnable {

    private final CompletableFuture<double[]> future;
    
    private final FXClient fxclient;
    private final FXPair fxpair;
    private final Account account;
    private RateTable rateTable;
    double[] strategyData;

    /**
     * ストラテジーの期間
     */
    private final int signal;
    private final int intM;
    private final int intS;

    /**
     *
     * @param OAT OandaAutoTrader内のフィールドを利用するための引数
     * @param future CompletableFuture＜double[]＞の引数
     */
    //public Strategy_B2_plugin(OandaAutoTrader oandaAutoTraderLocal,FXClient fxclient, FXPair fxpair, Account account, CompletableFuture<double[]> future) {
    public Strategy_E_macDandP_plugin(OandaAutoTrader OAT, CompletableFuture<double[]> future) {
        //super(fxpair_ticker, account_ticker);
        this.future = future;
        this.fxclient = OAT.fxclient;
        this.fxpair = OAT.fxpair;
        this.account = OAT.account;
        oandaAutoTrader = OAT;//extendsしたStrategyTemplateの変数oandaAutoTraderに代入
        this.signal = OAT.signal;
        this.intM = OAT.intM;
        this.intS = OAT.intS;
    }


    double[] strategy() {
        MACD macd = new MACD(hiashiArrayList,intS,intM,signal);
        double[] CP = new double[3];
        //MACDの最新一つだけを取得して代入する
        CP[0] = macd.macdHistgram.get(macd.macdHistgram.size()-1);//MACDヒストグラム
        CP[1] = macd.macdSignal.get(macd.macdSignal.size()-1);//シグナル
        CP[2] = macd.macdList.get(macd.macdList.size()-1);//MACD
        //System.out.println("cp0"+macd.macdHistgram.get(macd.macdHistgram.size()-1));
        return CP;//double[]型、0ヒストグラム・1シグナル・2MACDの値を返す
    }

    /**
     * 日足(秒・分・時)を取得するメソッド
     *
     * @param pair_history Stringでペアを引数に入れる　例＞USD/JPY
     * @param interval_history longでインターバルを入れる 例＞3000 もしくは TimeGetter.TIME1MIN
     * @param tick_history intで日足の取得数を入れる 例＞100
     * @return ArrayList＜Object[]＞の日足セットを返す
     */
    public ArrayList<String[]> getHiashiList(FXPair pair_history, long interval_history, int tick_history) {
        //このクラスのフィールドhiashiArrayListに代入するため、ローカルなインスタンスを準備
        ArrayList<String[]> hiashiArrayListLocal = new ArrayList<>();

        if (!fxclient.isLoggedIn()) {
            return null;
        }

        // ペアの識別
        FXPair pair = null;
        if (pair_history.toString().equals("")) {
            pair = API.createFXPair("USD/JPY"); // default to USD/JPY
        } else {
            pair = pair_history;
        }
        //System.out.println("FXPair: " + pair);

        // インターバルの取得
        // 5 = 5 sec
        // 10 = 10 sec
        // 30 = 30 sec
        // 60 = 1 min
        // 300 = 5 min
        // 1800 = 30 min
        // 10800 = 3 hour
        // 86400  = 1 day
        long interval_local = interval_history * 1000;  // convert to milliseconds
        if (interval_local == 0) {//上記の計算結果が0であればデフォルトの数値を使う
            System.out.println("intervalHistoryの値が0なので、" + (TimeGetter.TIME1DAY * 1000) + "に変更しました。");
            interval_local = TimeGetter.TIME1DAY * 1000;
        }

        // get the number of ticks　ティック数をセットする
        int ticks = tick_history;
        if (ticks == 0) {
            ticks = 100; // default to 100 ticks
        }

        GetHistory gethistory = new GetHistory();
        hiashiArrayListLocal = gethistory.getHistory(oandaAutoTrader, rateTable, interval_local, ticks);

        return hiashiArrayListLocal;
    }

    @Override
    public void run() {
        try {
            rateTable = fxclient.getRateTable();//fxclientからRateTableを取得する
        } catch (SessionDisconnectedException ex) {
            Logger.getLogger(Strategy_E_macDandP_plugin.class.getName()).log(Level.SEVERE, null, ex);
        }

        //("",0,0)はデフォルト値を入れている。日足を取得、継承元のhiashiArrayListにデータを格納
        //
        hiashiArrayList = getHiashiList(this.oandaAutoTrader.fxpair, this.oandaAutoTrader.interval, this.oandaAutoTrader.totalTicks);
        System.out.println("日足取得サイズ:" + hiashiArrayList.size());
        strategyData = strategy();
        //System.out.println("strategyData:" + Arrays.toString(strategyData));
        future.complete(strategyData);//ここに最終的な値を入れる
    }
}
