/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indicators;

import java.util.ArrayList;

/**
 *
 * @author kimuratadashi
 */
public class MACD extends IndicatorTemplate {

    public double alpha;
    public ArrayList<Long> dayList;//unixtimeのリスト
    public ArrayList<Double> closeList;//終値のリスト
    public ArrayList<Double> emaList;//EMAのリスト
    public ArrayList<Double> macdList;//MACDのリスト
    public ArrayList<Double> macdSignal;//シグナル用macd
    public ArrayList<Double> macdHistgram;//MACDヒストグラム
    public final int spanS, spanM, signal;

    /**
     *
     * @param fxArrayData ArrayList＜String[]＞型の日足リスト
     * @param spanS 短期スパン
     * @param spanM　長期スパン
     * @param signal シグナル用のスパン
     */
    public MACD(ArrayList<String[]> fxArrayData, int spanS, int spanM, int signal) {
        //this.span = span;//IndicatorTemplateのcheckSizeに代入
        this.fxArrayData = fxArrayData;//IndicatorTemplateのfxArrayDataに代入
        this.closeList = new ArrayList<>();
        this.dayList = new ArrayList<>();
        this.emaList = new ArrayList<>();
        this.macdList = new ArrayList<>();
        this.macdSignal = new ArrayList<>();
        this.macdHistgram = new ArrayList<>();
        this.spanS = spanS;
        this.spanM = spanM;
        this.signal = signal;
        keisan();
    }

    public void keisan() {
        ArrayList<Double> emaSlst;
        ArrayList<Double> emaMlst;

        EMA emaS = new EMA(this.fxArrayData, this.spanS);
        EMA emaM = new EMA(this.fxArrayData, this.spanM);
        emaSlst = emaS.emaList;
        emaMlst = emaM.emaList;

        
        //MACD計算
        for (int i = 0; i < emaSlst.size(); i++) {//
            double macdsum = emaSlst.get(i) - emaMlst.get(i);
            this.macdList.add(macdsum);
        }

        //MACDのシグナル計算
        EMA macdSig = new EMA(this, signal);
        this.macdSignal = macdSig.emaList;

        
        //MACDヒストグラム計算のための準備
        //macdListとmacdSignalのリストサイズを揃える
        int arraySpan = this.macdList.size() - this.macdSignal.size();
        //ArrayList<Double> macdListCopy = new ArrayList<>(macdList);//長い方を一旦コピーする
        for (int i = 0; i < arraySpan; i++) {
            macdList.remove(0);//sumの分だけ最初のリストを削除
        }
        
        //MACDヒストグラムの計算
        for (int i = 0; i < macdList.size(); i++) {
            double a = macdList.get(i) - this.macdSignal.get(i);
            this.macdHistgram.add(a);
        }
    }
}
