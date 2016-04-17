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
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import oandaautotrader.GetHistory;
import oandaautotrader.OandaAutoTrader;
import oandaautotrader.TimeGetter;

/**
 * ストラテジー　デモプラグイン。newして使用する。 <br>これはテストストラテジーB2です。 日足１本分の中値を使ってストラテジーを組みます。
 *
 * @author maruhachi
 */
public class Strategy_B2_plugin extends StrategyTemplate implements Runnable {

    private final CompletableFuture<double[]> future;
    ArrayList<Object[]> arraylistGlobal;
    private final FXClient fxclient;
    private final FXPair fxpair;
    private final Account account;
    private RateTable rateTable;
    double[] strategyData;

    /**
     *
     * @param OAT OandaAutoTrader内のフィールドを利用するための引数
     * @param future CompletableFuture＜double[]＞の引数
     */
    //public Strategy_B2_plugin(OandaAutoTrader oandaAutoTraderLocal,FXClient fxclient, FXPair fxpair, Account account, CompletableFuture<double[]> future) {
    public Strategy_B2_plugin(OandaAutoTrader OAT,CompletableFuture<double[]> future){
    //super(fxpair_ticker, account_ticker);
        this.future = future;
        this.fxclient = OAT.fxclient;
        this.fxpair = OAT.fxpair;
        this.account = OAT.account;
        oandaAutoTrader = OAT;//extendsしたStrategyTemplateの変数oandaAutoTraderに代入


    }

    double[] strategy(String[] arrayList, String[] arrayList2, String[] arrayList3) {
        arraylistGlobal = new ArrayList<>();

        double[] CP = new double[3];
        CP[0] = (Double.parseDouble(arrayList[2]) + Double.parseDouble(arrayList[3])) / 2;//長期の中値
        CP[1] = (Double.parseDouble(arrayList2[2]) + Double.parseDouble(arrayList2[3])) / 2;//中期の中値
        CP[2] = (Double.parseDouble(arrayList3[2]) + Double.parseDouble(arrayList3[3])) / 2;//短期の中値
        
        boolean[] innerBooleanSet = new boolean[2];
        boolean flagLongSell = (CP[2] < CP[1] && CP[1] < CP[2]);//短期が中期より下&中期が長期より下ならTRUE
        boolean flagLongBuy = (CP[2] > CP[1]);//短期が中期より上ならTRUE
        boolean flagShortSell = (CP[2] > CP[1] && CP[1] > CP[2]);//短期が中期より上＆中期が長期より上ならTRUE
        boolean flagShortBuy = (CP[2] < CP[1]);//短期が中期より下ならTRUE
        
        //System.out.println("ストラテジー動いてるよ！");
        return CP;//長期・中期・短期の値を返す
    }

    /**
     * 日足(秒・分・時)を取得するメソッド
     *
     * @param pair_history Stringでペアを引数に入れる　例＞USD/JPY
     * @param interval_history longでインターバルを入れる 例＞3000 もしくは TimeGetter.TIME1MIN
     * @param tick_history intで日足の取得数を入れる 例＞100
     * @return ArrayList＜Object[]＞の日足セットを返す
     */
    public ArrayList<String[]> getHiashiList(String pair_history, long interval_history, int tick_history) {
        //このクラスのフィールドhiashiArrayListに代入するため、ローカルなインスタンスを準備
        ArrayList<String[]> hiashiArrayListLocal = new ArrayList<>();

        if (!fxclient.isLoggedIn()) {
            return null;
        }

        // ペアの識別
        FXPair pair = null;
        if (pair_history.equals("")) {
            pair = API.createFXPair("USD/JPY"); // default to USD/JPY
        } else {
            pair = API.createFXPair(pair_history);
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
        long interval = interval_history * 1000;  // convert to milliseconds
        if (interval == 0) {//上記の計算結果が0であればデフォルトの数値を使う
            System.out.println("intervalHistoryの値が0なので、" + (TimeGetter.TIME1DAY * 1000) + "に変更しました。");
            interval = TimeGetter.TIME1DAY * 1000;
        }

        // get the number of ticks　ティック数をセットする
        int ticks = tick_history;
        if (ticks == 0) {
            ticks = 100; // default to 100 ticks
        }

        GetHistory gethistory = new GetHistory();
        hiashiArrayListLocal = gethistory.getHistory(oandaAutoTrader, rateTable, interval, ticks);
        /*
        for(String[] a:hiashiArrayListLocal){
            System.out.println("hiashiArrayListLocal:" + Arrays.toString(a));
        }
        */
        return hiashiArrayListLocal;
    }

    @Override
    public void run() {
                try {
            rateTable = fxclient.getRateTable();//fxclientからRateTableを取得する
        } catch (SessionDisconnectedException ex) {
            Logger.getLogger(Strategy_B2_plugin.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<String[]> hiashiArrayList = getHiashiList("", TimeGetter.TIME30MIN, 3);//("",0,0)はデフォルト値を入れている。日足を取得、hiashiArrayListにデータを格納
        ArrayList<String[]> hiashiArrayList2 = getHiashiList("", (TimeGetter.TIME10MIN), 3);
        ArrayList<String[]> hiashiArrayList3 = getHiashiList("", TimeGetter.TIME1MIN*2, 3);
        strategyData = strategy(hiashiArrayList.get(0), hiashiArrayList2.get(0), hiashiArrayList3.get(0));
        //System.out.println("strategyData:" + Arrays.toString(strategyData));
        future.complete(strategyData);
    }

}
