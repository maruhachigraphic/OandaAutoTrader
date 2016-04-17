/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EMA 指数平滑移動平均の計算
 *
 * @author kimuratadashi
 */
public class EMA extends IndicatorTemplate {

    public double alpha;
    public ArrayList<Long> dayList;//unixtimeのリスト
    public ArrayList<Double> closeList;//終値のリスト
    public ArrayList<Double> emaList;//EMAのリスト
    public ArrayList<Double> macdSignal;//シグナル用MACDのリスト

    /**
     *通常のEMAコンストラクタ
     * @param fxArrayData
     * @param span
     */
    public EMA(ArrayList<String[]> fxArrayData, int span) {
        this.span = span;//IndicatorTemplateのcheckSizeに代入
        this.fxArrayData = fxArrayData;//IndicatorTemplateのfxArrayDataに代入
        this.closeList = new ArrayList<>();
        this.dayList = new ArrayList<>();
        this.emaList = new ArrayList<>();
        alpha = 2.0 / (span + 1.0);//α：平滑定数

        //unixtimeとcloseのListを取得
        for (String[] getFxArray : this.fxArrayData) {
            dayList.add(Long.parseLong(getFxArray[0]));//[0]=unixtimeを取得
            closeList.add(Double.parseDouble(getFxArray[4]));//[4]=close（終値）を取得
        }
        //計算スタート
        keisan(closeList);
        //emaList.stream().forEach((a) -> {System.out.println(a);});
    }

    /**
     *MACDのシグナル用EMAコンストラクタ
     * @param macd
     * @param span
     */
    public EMA(MACD macd, int span) {
        this.span = span;
        this.fxArrayData = macd.fxArrayData;
        this.closeList = new ArrayList<>();
        this.dayList = new ArrayList<>();
        this.emaList = new ArrayList<>();
        this.macdSignal = macd.macdSignal;
        alpha = 2.0 / (span + 1.0);//α：平滑定数

        //計算スタート
        keisan(macd.macdList);
    }

    private ArrayList<Double> keisan(ArrayList<Double> closeListLocal) {
        //最初のema計算
        double total = 0;
        for (int i = 0; i < span; i++) {
            total += closeListLocal.get(i);
        }
        double firstema = total / span;
        emaList.add(firstema);
        int spansecond = span + 1;
        double secondema;
        //2日目以降の計算
        double zenjitu;
        double touzituowarine;
        int roopsize = closeListLocal.size() -1;

        for (int i = 0; i < roopsize; i++) {
            zenjitu = emaList.get(i);//emaListには初日が入っているので0から始める
            touzituowarine = closeListLocal.get(i + 1);
            //System.out.println("当日終値：" + touzituowarine + "前日終値:" + zenjitu + "alpha:" + this.alpha);
            double thrdema = zenjitu + (this.alpha * (touzituowarine - zenjitu));

            emaList.add(thrdema);
        }
        return emaList;
    }
}
