/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MACP 移動平均乖離率の計算
 *
 * @author kimuratadashi
 */
public class MACP extends IndicatorTemplate {

    public double alpha;

    /**
     * unixtimeのリスト
     */
    public ArrayList<Long> dayList;//unixtimeのリスト

    /**
     * 終値のリスト
     */
    public ArrayList<Double> closeList;//終値のリスト

    /**
     * MACPのリスト
     */
    public Double macp;//MACPの値

    private ArrayList<Double> closeListReverse;//計算用に順番を逆転させる.

    /**
     * 通常のEMAコンストラクタ
     *
     * @param span MACPの期間
     */
    public MACP(int span) {
        this.span = span;//IndicatorTemplateのspanに代入
        
        this.closeList = new ArrayList<>();
        this.dayList = new ArrayList<>();

    }

    /**
     * 最新のMACP値を計算
     * @param fxArrayData 日足データ
     * @param currentTick Tickerクラスで取得した現在値
     * @return
     */
    public Double keisan(ArrayList<String[]> fxArrayData, double currentTick) {
        
        this.fxArrayData = fxArrayData;//IndicatorTemplateのfxArrayDataに代入
        //unixtimeとcloseのListを取得
        for (String[] getFxArray : this.fxArrayData) {
            dayList.add(Long.parseLong(getFxArray[0]));//[0]=unixtimeを取得
            closeList.add(Double.parseDouble(getFxArray[4]));//[4]=close（終値）を取得
        }
        //計算スタート
        closeListReverse = new ArrayList<>(closeList);//リストをコピー
        Collections.reverse(closeListReverse);//リスト反転

        //n日足して割る
        double total = 0;
        for (int n = 0; n < span; n++) {
            total += closeListReverse.get(n);
        }
        double heikin = total / span;//n日平均値の計算
        this.macp = (currentTick - heikin) * 100;//MACP = (当日株価 - n日移動平均)*100
        System.out.println("現在値:" + currentTick + " 平均値:" + heikin);

        return macp;
    }
}
