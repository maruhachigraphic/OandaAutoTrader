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
import indicators.MACP;
import java.util.ArrayList;

/**
 * スレッドでティックを常時監視し、売買パターンを通してオーダーする MACDとMACP（移動平均乖離率）を使用する。
 *
 * @author maruhachi
 */
public class TickerE extends FXRateEvent {

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

    private GetTransaction transactoncheck;

    private MACP macp;

    private double currentBidAsk;

    private long currentUnits;//現在保有している建玉

    /**
     * コンストラクタ
     *
     * @param OAT OandaAutoTrader内のフィールドを利用するための引数
     * @param fxpair_ticker fxpairを入れる引数
     */
    //public Ticker(FXPair fxpair_ticker, Account account_ticker) {
    public TickerE(OandaAutoTrader OAT, FXPair fxpair_ticker) {
        super(fxpair_ticker.toString());//super(親)はFXRateEvent
        this.oat = OAT;
        this.account = OAT.account;
        this.shortOrder = false;
        this.longOrder = false;
        this.setOrder = new SetOrder(OAT);
        this.units = 100;//ユニット数（建玉の数）
        this.transactionArray = new ArrayList<>();

        this.transactoncheck = new GetTransaction(OAT);

        this.macp = new MACP(oat.macpSpan);//MACPのインスタンス生成

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
     * SR[0]ヒストグラム SR[1]シグナル　SR[2]MACD
     */
    public void localStrategy() {
        this.currentBidAsk = (currentBid + currentAsk) / 2.0;//BidとAskの中値
        if (this.currentUnits == 0) {
            setbuy();
            System.out.println("currentUnits:"+this.currentUnits);
        }else if(this.currentUnits > 0){
            setRelease();
        }
    }

    private void setbuy() {
        //System.out.println("SR[2]MACD:" + SR[2] + " SR[1]シグナル:" + SR[1]+ " SR[0]ヒストグラム:" + SR[0]);
        //SR[5]=MACD長期 SR[4]=シグナル長期 SR[3]=ヒストグラム長期 
        if ((currentAsk - currentBid) < 1) {//スプレッドが1以内であればtrue

            //boolean flagLongBuy = ((SR[2] > SR[1]) && (SR[0] > 0));//MACD(SR[2])がシグナル(SR[1])より上ならロングフラグTRUE
            boolean flagLongBuy = (SR[5] > SR[4]) && (SR[3] > 0);
            boolean flagShortBuy = ((SR[2] < SR[1]) && (SR[0] < 0));//MACD(SR[2])がシグナル(SR[1])より下ならショートフラグTRUE

            if (flagLongBuy && !longOrder) {//もしflagLongBuyがtrue＆現在値が中期より上＆買い注文フラグがfalseなら
                System.out.println((flagLongBuy && (SR[1] < this.currentBidAsk)) + ":" + SR[1] + "買うぞ！");
                longOrder = true;//ロング注文フラグ発生
                shortOrder = false;//ショート注文フラグを取り消し
                releaseTransaction();//トランザクションがあればリリース

                //！！！！！！！！！！！！！！発注！！！！！！！！！！！！！！！！！
                oat.transactionNum = setOrder.setDealing(units);
                this.currentUnits = setOrder.getUnits(oat.transactionNum);
                //this.transactionArray.add( this.transactoncheck.getTransaction() );//
            }
        }//スプレッドが1を越えたら一旦戻すの終了
    }
    /**
     * リリースするためのストラテジー
     * 5分足のMACDで判断する
     */
    private void setRelease(){
        if((currentAsk - currentBid) < 1){
            //MACD(SR[5])がシグナル(SR[4])より下、ヒストグラムSR[3]が0より下ならflagRerase=true
            System.out.println("ヒストグラム:"+SR[3]+" シグナル:"+SR[4]+" MACD:"+SR[5]);
            boolean flagRelease = (SR[5] < SR[4]) && (SR[3] < 0);
            //
            //作業中*********************************************
            //
            if (flagRelease){
                System.out.println("手仕舞いします！:" + oat.transactionNum);
                releaseTransaction();
                longOrder = false;
            }
        }
    }
    //トランザクションの有無でリリース
    private void releaseTransaction() {
        if (oat.transactionNum > 1) {//もしtransactionNumに値が入っていれば一旦建玉をリリース
            setOrder.setRelease(oat.transactionNum);
            oat.transactionNum = 0;
            this.currentUnits = 0;
        }
    }

    private void tickCount() {
        double answer;
        //現在値>元高値であれば現在値をtickCounterHiに代入
        oat.tickCounterHi = ((oat.tickCounterHi > this.currentAsk) ? oat.tickCounterHi : this.currentAsk);
        oat.tickCounterLow = ((oat.tickCounterLow < this.currentBid) ? oat.tickCounterLow : this.currentBid);

    }
}
