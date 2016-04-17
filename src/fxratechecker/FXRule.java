/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxratechecker;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kimuratadashi
 */
public class FXRule {

    private final List<Object[]> IchimokuList;
    private final List<Object[]> RSIList;
    private final int currentPoint = 0;
    private final int rsi_day;//rsiのパラメータ用変数
    private int[] ichimoku_day = new int[5];//一目のパラメータ用配列変数

    public FXRule(ArrayList<Object[]> FXarrayData, int[] into_ichimoku, int into_rsi) {
        this.IchimokuList = new ArrayList<>();//ichimokuのArrayListを準備
        this.RSIList = new ArrayList<>();//RSIのArrayListを準備
        this.rsi_day = into_rsi;//引数からrsiのパラメータに代入
        this.ichimoku_day = into_ichimoku;//引数から一目のパラメータに代入

        RSIRule(FXarrayData);//メソッドRSIRuleに日足リストfxArrayを代入して起動
        IchimokuRule(FXarrayData);//メソッドIchimokuRuleに日足リストfxArrayを代入して起動

    }

    public void RSIRule(ArrayList<Object[]> FXarrayData) {//fxArray=日足リスト
        //RSI用
        RSI rsi = new RSI(FXarrayData, rsi_day);
        int sum = 0;
        for (int i = 0; i < rsi.totalListSize; i++) {
            Object dayObj = rsi.dayList.get(i);//RSIクラスrsiからunixtimeが入っているフィールドdayListを取得
            Object rsiObj = rsi.rsiList.get(i);//RSIクラスrsiからrsiが入っているフィールドrsiListを取得
            Object[] rsiSetObj = {dayObj, rsiObj};
            //System.out.println(rsiSetObj[0]+","+rsiSetObj[1]);
            RSIList.add(rsiSetObj);
        }
        System.out.println("RSIのList<Object[]>代入完了");
    }

    public List<Object[]> getRSIRule() {//RSIリストのgetter
        System.out.println("getRSIRuleだよ");
        return RSIList;
    }

    public void IchimokuRule(ArrayList<Object[]> FXarrayData) {//fxArray=日足リスト
        Ichimoku ichimoku = new Ichimoku(FXarrayData, ichimoku_day);//日足のデータ一式を入れた(ArrayList<Object[]> arrayData)を引数にする。

        //System.out.println("unix時間,転換,基準,先行1,先行2,遅行");
        for (int i = 0; i < ichimoku.totalListSize; i++) {

            Object dayObj = ichimoku.dayList.get(i);
            Object tenkanObj = ichimoku.tenkanList.get(i);
            Object kijunObj = ichimoku.kijunList.get(i);
            Object senkou1Obj = ichimoku.senkou1List.get(i);
            Object senkou2Obj = ichimoku.senkou2List.get(i);
            Object chikouObj = ichimoku.chikouSpanList.get(i);
            //System.out.println(dayObj + "," + tenkanObj + "," + kijunObj + "," + senkou1Obj + "," + senkou2Obj + "," + chikouObj);
            Object[] ichimokuObj = {dayObj, tenkanObj, kijunObj, senkou1Obj, senkou2Obj, chikouObj};
            IchimokuList.add(ichimokuObj);
        }
        System.out.println("一目均衡表のList<Object[]>代入完了");
    }

    public List<Object[]> getIchimokuRule() {//一目リストのgetter
        System.out.println("getIchimokuRuleだよ");
        return IchimokuList;
    }
}
