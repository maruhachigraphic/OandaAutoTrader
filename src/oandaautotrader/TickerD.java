/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oandaautotrader;

import com.oanda.fxtrade.api.Account;
import com.oanda.fxtrade.api.FXEventInfo;
import com.oanda.fxtrade.api.FXEventManager;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.FXRateEvent;
import com.oanda.fxtrade.api.FXRateEventInfo;
import com.oanda.fxtrade.api.MarketOrder;
import java.util.ArrayList;

/**
 * スレッドでティックを常時監視して、売買パターンを通してオーダーする
 *
 * @author maruhachi
 */
public class TickerD extends FXRateEvent {

    private final OandaAutoTrader oat;
    private final Account account;
    private MarketOrder marketOrder;
    private double delta;
    //private boolean watchingBuyOrder;
    private volatile double currentAsk;
    private volatile double currentBid;
    private volatile double[] SR;//OandaAutoTraderから引き抜いたstrategyResultを入れる
    private static boolean shortOrder, longOrder;//ショート注文フラグ・ロング注文フラグ
    //private volatile long transactionNum;//現在建てている建玉のtransactionナンバーを取得
    private volatile ArrayList<String> transactionArray;//transactionのArrayList
    private SetOrder setOrder;

    //建玉数
    private final long units;

    private TransactionCheck transactoncheck;

    /**
     * コンストラクタ
     *
     * @param OAT OandaAutoTrader内のフィールドを利用するための引数
     * @param fxpair_ticker fxpairを入れる引数
     */
    //public Ticker(FXPair fxpair_ticker, Account account_ticker) {
    public TickerD(OandaAutoTrader OAT, FXPair fxpair_ticker) {
        super(fxpair_ticker.toString());//super(親)はFXRateEvent
        this.oat = OAT;
        this.account = OAT.account;
        this.shortOrder = false;
        this.longOrder = false;
        this.setOrder = new SetOrder(OAT);
        this.units = 100;//ユニット数（建玉の数）
        this.transactionArray = new ArrayList<>();

        this.transactoncheck = new TransactionCheck(OAT);
        //marketOrder = marketorder;
        //delta = ;
        //watchingBuyOrder = (marketOrder.getUnits() > 0);//発注量が0より大きかったらtrue
        //currentSL = marketOrder.getStopLoss().getPrice();//getPrice=注文した値段
        setTransient(false);//FXRateEventのメソッド。このイベントは一時的であるかどうかを設定します（一度だけ呼び出し、そのハンドルメソッドを持つことになります）。
    }

    /**
     * FXRateEventで必ず呼び出されるメソッド handleメソッドは、それぞれのイベントで1回呼び出されます。
     * これはtrueを返すmatchメソッドになります。
     *
     * @param EI FXEventInfo型
     * @param EM FXEventManager型
     */
    @Override
    public void handle(FXEventInfo EI, FXEventManager EM) {
        FXRateEventInfo REI = (FXRateEventInfo) EI;
        //System.out.println("現在ASK値：" + REI.getTick().getAsk());
        this.currentAsk = REI.getTick().getAsk();
        this.currentBid = REI.getTick().getBid();
        //System.out.println("TickerからstrategyResult" + Arrays.toString(OandaAutoTrader.strategyResult));
        this.SR = this.oat.strategyResult;//ストラテジーからのデータを取得
        localStrategy();//ローカルなストラテジーを作動させる
    }

    /**
     * 売買専用のストラテジー 実際の売買司令(setOrder.setDealing(units))を出す
     *
     * SR[0]ヒストグラム 　SR[1]シグナル　SR[2]MACD
     */
    public void localStrategy() {
        //System.out.println("現在BID値：" + currentBid + " ASK値：" + currentAsk);
        //System.out.println("SR[2]MACD:" + SR[2] + " SR[1]シグナル:" + SR[1]+ " SR[0]ヒストグラム:" + SR[0]);
        
        if ((currentAsk - currentBid) < 1) {//スプレッドが1以内であればtrue

            boolean flagLongBuy = ( (SR[2] > SR[1]) && (SR[0] > 0) );//MACD(SR[2])がシグナル(SR[1])より上ならロングフラグTRUE
            boolean flagShortBuy = ( (SR[2] < SR[1]) && (SR[0] < 0) );//MACD(SR[2])がシグナル(SR[1])より下ならショートフラグTRUE

            if (flagLongBuy && !longOrder) {//もしflagLongBuyがtrue＆現在値が中期より上＆買い注文フラグがfalseなら
                System.out.println((flagLongBuy && (SR[1] < currentAsk)) + ":" + SR[1] + "買うぞ！");
                longOrder = true;//ロング注文フラグ発生
                shortOrder = false;//ショート注文フラグを取り消し

                if (oat.transactionNum > 1) {//もしtransactionNumに値が入っていれば一旦建玉をリリース
                    setOrder.setRelease(oat.transactionNum);
                    oat.transactionNum = 0;
                }
                //！！！！！！！！！！！！！！発注！！！！！！！！！！！！！！！！！
                oat.transactionNum = setOrder.setDealing(units);
            //this.transactionArray.add( this.transactoncheck.getTransaction() );//

            } else if (flagShortBuy && !shortOrder) {//もしflagShortBuyがtrue＆短期が現在値より上＆売り注文フラグがfalseなら
                System.out.println((flagShortBuy && (SR[1] > currentBid)) + ":" + SR[1] + "売るぞ！");
                longOrder = false;//ロング注文フラグを取り消し
                shortOrder = true;//ショート注文フラグ発生
                if (oat.transactionNum > 1) {//もしtransactionNumに値が入っていれば一旦建玉をリリース
                    setOrder.setRelease(oat.transactionNum);
                    oat.transactionNum = 0;
                }
                //！！！！！！！！！！！！！！発注！！！！！！！！！！！！！！！！！
                oat.transactionNum = setOrder.setDealing(-units);//-unitsで-100となりショート取引となる

            }
        }//スプレッドが1を越えたら一旦戻すの終了
    }

}
