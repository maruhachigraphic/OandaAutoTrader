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
public class TickerE2 extends FXRateEvent {

    private final OandaAutoTrader oat;
    private final Account account;
    private MarketOrder marketOrder;

    private boolean rule;
    private double delta;
    //private boolean watchingBuyOrder;
    private volatile double currentAsk;
    private volatile double currentBid;
    private volatile double currentBidAsk;
    private volatile double[] SR;//OandaAutoTraderから引き抜いたstrategyResultを入れる
    private static boolean shortOrder, longOrder;//ショート注文フラグ・ロング注文フラグ
    //private volatile long transactionNum;//現在建てている建玉のtransactionナンバーを取得
    private volatile ArrayList<String> transactionArray;//transactionのArrayList
    private SetOrder setOrder;

    //建玉数
    private final long units;

    private GetTransaction transactoncheck;

    private MACP macp;

    //MACDシグナルを一時記録するための変数
    private double macdHistogramMem;
    //MACDシグナルが前回より＋であればtrue、−であればfalse
    private boolean macdHistogramFlag;

    private boolean stoplossFlag;

    private long currentUnits;//現在保有している建玉

    private ArrayList<Double> memoryMacdSignal;//MACDシグナルを保管

    private boolean longBuyFlag;
    private boolean shortBuyFlag;

    //初段ストップロスの値
    double firstStopLossValue;
    //二段目ストップロスの値
    double secondStopLossValue;
    //二段目ストップロスを発動させるためのリミット値
    double stopLossLimit;

    /**
     * コンストラクタ
     *
     * @param OAT OandaAutoTrader内のフィールドを利用するための引数
     * @param fxpair_ticker fxpairを入れる引数
     */
    //public Ticker(FXPair fxpair_ticker, Account account_ticker) {
    public TickerE2(OandaAutoTrader OAT, FXPair fxpair_ticker) {
        super(fxpair_ticker.toString());//super(親)はFXRateEvent
        this.oat = OAT;
        this.account = OAT.account;

        this.rule = OAT.rule;
        this.firstStopLossValue = OAT.firstStopLossValue;
        this.secondStopLossValue = OAT.secondStopLossValue;
        this.stopLossLimit = OAT.stopLossLimit;
        this.shortOrder = false;
        this.longOrder = false;
        this.setOrder = new SetOrder(OAT);
        this.units = 100;//ユニット数（建玉の数）
        this.transactionArray = new ArrayList<>();

        this.transactoncheck = new GetTransaction(OAT);

        this.macp = new MACP(oat.macpSpan);//MACPのインスタンス生成
        this.memoryMacdSignal = new ArrayList<>();//MACDシグナルの保存用ArrayListを生成
        this.stoplossFlag = false;
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
        //MACDシグナルを記録*******************保留＊＊＊＊＊＊＊＊＊＊＊＊
        //memoryMacdSignal.add(SR[0]);
        //System.out.println(memoryMacdSignal.get(memoryMacdSignal.size() - 1));
        localStrategy();//ローカルなストラテジーを作動させる
    }

    /**
     * 売買専用のストラテジー 実際の売買司令(setOrder.setDealing(units))を出す
     *
     * SR[0]ヒストグラム SR[1]シグナル　SR[2]MACD
     */
    public void localStrategy() {
        this.currentBidAsk = (currentBid + currentAsk) / 2.0;//BidとAskの中値
        //rule(ロング優先orショート優先)がtrueであればロングで取引、falseであればショートで取引
        if (this.rule) {
            if (this.currentUnits == 0) {
                setLongbuy();
                macdHistogramMem = SR[0];//macdヒストグラムを一時保存
            } else if (this.currentUnits > 0) {
                //System.out.println("currentUnits:" + this.currentUnits); 
                setLongRelease();
                modifyStopLoss(secondStopLossValue, stopLossLimit);//ストップロスの修正
            }
        } else {
            if (this.currentUnits == 0) {
                setShortbuy();
                macdHistogramMem = SR[0];//macdヒストグラムを一時保存
            } else if (this.currentUnits < 0) {
                //System.out.println("currentUnits:" + this.currentUnits); 
                setShortRelease();
                modifyStopLoss(-secondStopLossValue, -stopLossLimit);//ストップロスの修正
            }
        }
    }

    private void setLongbuy() {
        //SR[0]ヒストグラム SR[1]シグナル　SR[2]MACD
        //SR[3]=ヒストグラム長期 SR[4]=シグナル長期 SR[5]=MACD長期
        if ((currentAsk - currentBid) < 1) {
            //長期MACDが長期シグナルの下、短期MACDが短期シグナルの上、短期ヒストグラムが0より上
            longBuyFlag = ((SR[5] < SR[4]) && (SR[2] > SR[1]) && (SR[0] > 0));
            boolean longBuyFlagSecond = (SR[5] > SR[4]);

            if (longBuyFlag && !longOrder) {//longOrderがfalseなら
                System.out.println("！！！！！！！！！！！！買うぞ！！！！！！！！！！！！！");
                setLongbuySub();
            }
            if (longBuyFlagSecond && !longOrder) {//
                System.out.println("！！！！！！！！！！！！ロング２段目発動！！！！！！！！！！！！！");
                setLongbuySub();
            }
        }//スプレッドが1を越えたら一旦戻すの終了
    }

    private void setLongbuySub() {
        longOrder = true;//ロング注文フラグ発生
        releaseTransaction();//トランザクションがあればリリース
        setOrderLocal(true);//オーダーの命令、ロングなのでtrue
    }

    private void setShortbuy() {
        //SR[0]ヒストグラム SR[1]シグナル　SR[2]MACD
        //SR[3]=ヒストグラム長期 SR[4]=シグナル長期 SR[5]=MACD長期
        if ((currentAsk - currentBid) < 1) {
            //長期MACDが長期シグナルの上、短期MACDが短期シグナルの下、短期ヒストグラムが0より下
            shortBuyFlag = ((SR[5] > SR[4]) && (SR[2] < SR[1]) && (SR[0] < 0));
            boolean shortBuyFlagSecond = (SR[5] < SR[4]);

            if (shortBuyFlag && !shortOrder) {//shortOrderがfalseなら
                System.out.println("！！！！！！！！！！！！売るぞ！！！！！！！！！！！！！");
                setShortbuySub();
            }
            if(shortBuyFlagSecond && !longOrder){
                System.out.println("！！！！！！！！！！！！ショート２段目発動！！！！！！！！！！！！！");
                setShortbuySub();
            }
        }
    }
    
    private void setShortbuySub(){
                shortOrder = true;//ショート注文フラグ発生
                releaseTransaction();//トランザクションがあればリリース            
                setOrderLocal(false);//オーダーの命令　ショートなのでfalse
    }

    /**
     * setLongbuy、setShortbuyで使用するオーダー命令
     *
     * @param flag ロングならtrue、ショートならfalse
     */
    private void setOrderLocal(boolean flag) {
        int a = (flag ? (1) : (-1));
        //！！！！！！！！！！！！！！発注！！！！！！！！！！！！！！！！！
        oat.transactionNum = setOrder.setDealing(units * a);//unitsを−にするとショートになる
        this.currentUnits = setOrder.getUnits(oat.transactionNum);
        //ストップロスの設定、stopLossValueを現在値よりマイナスに設定する。
        //ロングの例＞0.05*(-1*(1))= -0.05
        //ショート例＞0.05*(-1*(-1))= 0.05
        setOrder.setStopLoss(oat.transactionNum, (firstStopLossValue * (-a)));
        this.stoplossFlag = true;
    }

    /**
     * ロングをリリースするためのストラテジー 1分足のMACDで判断する
     */
    private void setLongRelease() {
        if ((currentAsk - currentBid) < 1) {
            //boolean flagRelease = macdHistogramFlag();
            //SR[0]ヒストグラム SR[1]シグナル　SR[2]MACD
            //SR[3]=ヒストグラム長期 SR[4]=シグナル長期 SR[5]=MACD長期
            boolean longSellFlag = ((SR[5] < SR[4]) && (SR[2] < SR[1]));//長短MACDがシグナルより下

            if (longSellFlag) {
                System.out.println("ロングを手仕舞いします！:" + oat.transactionNum);
                releaseTransaction();
                longOrder = false;
            }
        }
    }

    /**
     * ショートをリリースするためのストラテジー 1分足のMACDで判断する
     */
    private void setShortRelease() {
        if ((currentAsk - currentBid) < 1) {//スプレッドが1より下であれば取引する
            //boolean flagRelease = macdHistogramFlag();
            //SR[0]ヒストグラム SR[1]シグナル　SR[2]MACD
            //SR[3]=ヒストグラム長期 SR[4]=シグナル長期 SR[5]=MACD長期
            boolean shortSellFlag = ((SR[5] > SR[4]) && (SR[2] > SR[1]));//長短MACDがシグナルより上

            if (shortSellFlag) {
                System.out.println("ショートを手仕舞いします！:" + oat.transactionNum);
                releaseTransaction();
                shortOrder = false;
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

    /**
     * ストップロスを修正
     */
    private void modifyStopLoss(double stoploss, double limit) {
        double orderPrice = this.setOrder.getPrice(oat.transactionNum);
        //System.out.println("stoploss:" + stoploss + " orderPrice:" + orderPrice + " flag:" + this.stoplossFlag);
        if (oat.rule) {//longの場合
            boolean longAnswer = ((this.currentBidAsk >= orderPrice + limit) && this.stoplossFlag);
            //ロングのストップロスの指示
            modSetStopLoss(stoploss, longAnswer);
        } else if (!oat.rule) {//shortの場合
            boolean shortAnswer = ((this.currentBidAsk <= orderPrice + limit) && this.stoplossFlag);
            //ショートのストップロスの指示
            modSetStopLoss(stoploss, shortAnswer);
        }
    }

    private void modSetStopLoss(double stoploss, boolean answer) {
        if (answer) {
            setOrder.setStopLoss(oat.transactionNum, stoploss);
            this.stoplossFlag = false;
        }
    }

    private boolean macdHistogramFlag() {
        if (SR[0] > macdHistogramMem) {
            macdHistogramMem = SR[0];
            macdHistogramFlag = true;
        } else if (SR[0] < macdHistogramMem) {
            macdHistogramMem = SR[0];
            macdHistogramFlag = false;
        }
        return macdHistogramFlag;
    }
}
