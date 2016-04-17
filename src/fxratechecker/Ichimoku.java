/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxratechecker;

//import static arraytest.Arraytest.arrayData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author kimuratadashi
 */
public final class Ichimoku {

    //private final int tenkan_day = 9, kijun_day = 26, senkou1_day = 26, senkou2_day = 52, chikouSpan_day = 26;//基本パラメータ
    //private final int tenkan_day = 7, kijun_day = 22, senkou1_day = 22, senkou2_day = 44, chikouSpan_day = 22;//海外パラメータ
    //private final int tenkan_day = 3, kijun_day = 9, senkou1_day = 9, senkou2_day = 18, chikouSpan_day = 9;//短期パラメータ
    private final int tenkan_day, kijun_day, senkou1_day, senkou2_day, chikouSpan_day;//期間パラメータ

    private final ArrayList<Object[]> fxarraydata;//fxArray内、Object[]の中身->日足 [0]=タイムスタンプ,[1]=open,[2]=max,[3]=min,[4]=close
    private final int checkSize;//日足リストのサイズ取得
    public final int totalListSize;//一目全体のサイズ

    public List<Object> dayList = new ArrayList<>();//unixtimeのみのList
    public List<Object> tenkanList = new ArrayList<>();//転換日のみのList
    public List<Object> kijunList = new ArrayList<>();//基準日のみのList
    public List<Object> senkou1List = new ArrayList<>();//先行1のみのList
    public List<Object> senkou2List = new ArrayList<>();//先行2のみのList
    public List<Object> chikouSpanList = new ArrayList<>();//遅行スパンのみのList

    @SuppressWarnings("empty-statement")
    Ichimoku(ArrayList<Object[]> FXarrayData, int[] parameter_day) { //日足データ:FXarrayData　期間パラメータ配列:parameter_day
        System.out.println("Ichimokuのコンストラクタ起動");
        this.tenkan_day = parameter_day[0];//転換
        this.kijun_day = parameter_day[1];//基準
        this.senkou1_day = parameter_day[2];//先行1
        this.senkou2_day = parameter_day[3];//先行2
        this.chikouSpan_day = parameter_day[4];//遅行
        this.fxarraydata = FXarrayData; //日足データを他のメソッドで使えるようにする
        checkSize = fxarraydata.size();//日足リストのサイズを取得
        totalListSize = checkSize + kijun_day;//一目全体のサイズ
        
        //各々のListを同じ長さに整える
        for (int i = 0; i < tenkan_day; i++) {
            dayList.add(0);
            tenkanList.add(0);
            kijunList.add(0);
        }
        for (int i = 0; i < senkou1_day; i++) {
            senkou1List.add(0);
            senkou2List.add(0);
        }

        for (int i = 0; i < this.fxarraydata.size(); i++) {
            Object[] getFxArray = this.fxarraydata.get(i);
            dayList.add(getFxArray[0]);//[0]=unixtimeを取得
            tenkanList.add(tenkan(i));//メソッドtenkan()をtenkanListに代入
            kijunList.add(kijun(i));
            senkou1List.add(senkou1(i));
            senkou2List.add(senkou2(i));
        }
        for (int i = 0; i < this.fxarraydata.size(); i++) {
            chikouSpanList.add(chikou(i));
            //System.out.println(chikouSpanList.get(i));
        }

        for (int i = 0; i < chikouSpan_day; i++) {
            chikouSpanList.add(0);
        }

        int count = kijun_day - tenkan_day;

        for (int i = 0; i < count; i++) {
            dayList.add(0);
            tenkanList.add(0);
            kijunList.add(0);
        }

    }

    public Object zeroCheck(Object obj) {
        Double doubleValue = Double.parseDouble(obj.toString());
        Object kotae = obj;
        if (doubleValue <= 0) {
            kotae = 0;
        }
        //System.out.println(doubleValue);
        return kotae;
    }

    public Object tenkan(int currentPoint) {
        Object tenkanKotae = this.keisan(currentPoint, tenkan_day);//定数フィールド(tenkan_day)を引数に入れる
        tenkanKotae = zeroCheck(tenkanKotae);

        return tenkanKotae;
    }

    public Object kijun(int currentPoint) {
        Object kijunKotae = this.keisan(currentPoint, kijun_day);//定数フィールドを引数に入れる
        kijunKotae = zeroCheck(kijunKotae);
        return kijunKotae;
    }

    public Object senkou1(int currentPoint) {
        Object tenkanPoint = this.tenkan(currentPoint);
        Object kijunPoint = this.kijun(currentPoint);
        Double doubleTenkanPoint = Double.parseDouble(tenkanPoint.toString());//Object型を一旦String型にして、Double型に変換
        Double doubleKijunPoint = Double.parseDouble(kijunPoint.toString());
        Object senkou1Kotae;
        if ((doubleTenkanPoint == 0) || (doubleKijunPoint == 0)) {
            senkou1Kotae = 0;
        } else {
            senkou1Kotae = (doubleTenkanPoint + doubleKijunPoint) / 2; //先行1 =（転換線 + 基準線）/2
        }
        return senkou1Kotae;
    }

    public Object senkou2(int currentPoint) {
        Object senkou2Kotae = this.keisan(currentPoint, senkou2_day);
        return senkou2Kotae;
    }

    public Object chikou(int currentPoint) {
        Object[] objArray = fxarraydata.get(currentPoint);
        Object chikouKotae = objArray[4];//[4]=close(終値)
        return chikouKotae;
    }

    public Object keisan(int currentPoint, int totalDay) {//引数：定数フィールド(例:kijun_day)
        int point = currentPoint - totalDay;//currentPoint(調べたい日)から定数フィールド（例:kijun_day）を引いた値
        List<Double> Max_list = new ArrayList<>();//定数フィールド分の数値を入れるための
        List<Double> Min_list = new ArrayList<>();//Double型Listを準備
        Object keisanKotae;
        if ((point >= 0)) { //pointが-1以下の場合は計算ができないので、keisanKotae=0
            for (int i = 0; i < totalDay; i++) {
                int sum = i + point;
                Object[] objArray = fxarraydata.get(sum);//fxArray（日足のArrayList<Object[]>）から、sum行目のObject[]を取得
                String objStrMax = objArray[2].toString();//objArray[2]=高値を取得してobjStrMaxに代入
                String objStrMin = objArray[3].toString();//objArray[3]=安値を取得してobjStrMinに代入
                Max_list.add(Double.parseDouble(objStrMax));//高値(objStrMax)をDouble型に変換してMax_list(Double型)に代入
                Min_list.add(Double.parseDouble(objStrMin));//安値(objStrMin)をDouble型に変換してMin_list(Double型)に代入
            }
            keisanKotae = (Collections.max(Max_list) + Collections.min(Min_list)) / 2;//取得日(totalDay)分の最高値＋取得日(totalDay)分の最安値／2
        } else {
            keisanKotae = 0;
        }
        return keisanKotae;
    }

}
