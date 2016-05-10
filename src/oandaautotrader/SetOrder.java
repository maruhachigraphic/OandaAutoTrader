/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oandaautotrader;

import com.oanda.fxtrade.api.API;
import com.oanda.fxtrade.api.Account;
import com.oanda.fxtrade.api.AccountException;
import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.MarketOrder;
import com.oanda.fxtrade.api.OAException;
import com.oanda.fxtrade.api.SessionException;
import com.oanda.fxtrade.api.StopLossOrder;
import com.oanda.fxtrade.api.Transaction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 売買オーダーを発生させます。 new
 * SetOrder(OandaAutoTrader型);で準備をしておき、setDealing/setReleaseで実取引をします。
 *
 * @author kimuratadashi
 */
public class SetOrder {

    Account account;
    private final FXClient fxclient;
    private final FXPair fxpair;
    private double tsl;//トレーリングストップロスのpips値
    private final OandaAutoTrader oat;
    public long transactionNum;
    public long units;

    SetOrder(OandaAutoTrader oat) {//OandaAutoTrader型を受け取り、中身のフィールド値を取得する。
        this.account = oat.account;
        this.fxclient = oat.fxclient;
        this.fxpair = oat.fxpair;
        this.tsl = oat.TSL;
        this.oat = oat;
    }

    /**
     * 呼び出すとディーリング開始
     *
     * @param units 建玉数を入力、マイナス値でショートになる
     * @return トランザクションナンバーをlongで取得
     */
    public long setDealing(long units) {//units量を設定
        //APIクラスは新規インスタンスを作るためのクラス。
        MarketOrder order = API.createMarketOrder();//新たにMarketOrder（注文）のインスタンスを作成
        order.setPair(this.fxpair);//orderにペアを代入
        order.setUnits(units);//orderに取引量(units)を代入
        order.setTrailingStopLoss(tsl);//トレーリングストップロス OandaAutoTraderのフィールドTSLで設定する
        //System.out.println("tsl:"+tsl);
        //API.create〜で、発注金額のインスタンスを作り、それをMarketOrderに代入しなければならない。

        try {//！！！発注命令！！！！
            account.execute(order);//executeで指値発注
        } catch (OAException e) {
            fxclient.logout();
            System.exit(1);
        }
        this.transactionNum = order.getTransactionNumber();
        return this.transactionNum;
    }

    /**
     * オーダーした建玉を手仕舞う。トランザクションナンバーで建玉を選別
     *
     * @param transactionNum long値でトランザクションNo.を入力
     */
    public void setRelease(long transactionNum) {
        try {
            if (!fxclient.isLoggedIn()) {
                return;
            }

            // return if the user presses enter or 0
            if (transactionNum == 0) {
                return;
            }

            MarketOrder marketOrder = account.getTradeWithId(transactionNum);//closeさせるために、transactionNumをMarketOrder型に変換

            System.out.println("OAT: closing market order " + transactionNum + "...");

            // submit the market order close request//クローズ命令の送信
            account.close(marketOrder);
            System.out.println(transactionNum + "のクローズ");

        } catch (SessionException se) {
            System.err.println("OAT: market order close failed: " + se);
        } catch (Exception e) {
            System.err.println("OAT: market order close failed: " + e);
        }
    }

    public long getUnits(long transactionNum) {
        long units1 = 0;
        try {
            MarketOrder marketOrder = account.getTradeWithId(transactionNum);
            units1 = marketOrder.getUnits();
        } catch (AccountException ex) {
            Logger.getLogger(SetOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return units1;
    }
    
    public void editStopLoss(long transactionNum){
        StopLossOrder stopLO = null ;
        try {
            MarketOrder marketOrder = account.getTradeWithId(transactionNum);
            double currentprice = marketOrder.getPrice();
            System.out.println("getPrice:"+ currentprice);
            marketOrder.getStopLoss().setPrice(currentprice - 0.03);           
            this.account.modify(marketOrder);
            System.out.println("ストップロス:" + marketOrder.getStopLoss());
        } catch (AccountException ex) {
            Logger.getLogger(SetOrder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAException ex) {
            Logger.getLogger(SetOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
