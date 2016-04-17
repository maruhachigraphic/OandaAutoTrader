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
public final class RSI {
    //フィールド
    //private final int rsi_day = 14; //基本パラメータ
    //private final int rsi_day = 5;//テストパラメータ
    //private final int rsi_day = 12;//テストパラメータ2

    private final ArrayList<Object[]> fxarraydata;//fxArray内、Object[]の中身->日足 [0]=タイムスタンプ,[1]=open,[2]=max,[3]=min,[4]=close
    private final int checkSize;//日足リストのサイズ取得
    public final int totalListSize;//RSI全体のサイズ

    public List<Object> dayList = new ArrayList<>();//unixtimeのみのList
    public List<Object> rsiList = new ArrayList<>();//rsiのみのList
    public List<Double> closeList = new ArrayList<>();//終値のみのList
    public List<Double> hilowList = new ArrayList<>();//上下値幅のList
    public List<Double> highList = new ArrayList<>();//上げ幅のみのList
    public List<Double> lowList = new ArrayList<>();//下げ幅のみのList

    RSI(ArrayList<Object[]> FXarrayData,int rsi_day) { //日足データ一式をHiashiArrayに代入 
        System.out.println("RSIのコンストラクタ起動");
        this.fxarraydata = FXarrayData; //日足データを他のメソッドで使えるようにする
        checkSize = fxarraydata.size();//日足リストのサイズを取得
        //totalListSize = checkSize + rsi_day;//rsi全体のサイズ
        totalListSize = checkSize;

        //unixtimeとcloseのListを取得
        for (int i = 0; i < this.fxarraydata.size(); i++) {
            Object[] getFxArray = this.fxarraydata.get(i);
            dayList.add(getFxArray[0]);//[0]=unixtimeを取得
            double closeDouble = objdoubleExchenge(getFxArray[4]);//[4]=close（終値）を取得
            closeList.add(closeDouble);//double型にしたclose（終値）をcloseList<Double>に代入
            //senkou1List.add(senkou1(i));
        }

        nehaba();//nehabaを起動してhilowListに値幅を代入
        int totalcal = hilowList.size() - rsi_day;//全体のサイズ
        System.out.println("hilowList:" + hilowList.size());
        System.out.println("rsi_day:" + rsi_day);
        System.out.println("totalcal:" + totalcal);
        for (int i = 0;i<rsi_day;i++){ //rsiListにrsi_day分を先行して挿入
            rsiList.add(0);
        };
        for (int i = 1; i < totalcal + 1; i++) {//RSIを計算できる数だけループ（例：95=hilowlist(100)-rsi_day(5)）
            double hiTotal = 0;
            double lowTotal = 0;
            for (int j = 0; j < rsi_day; j++) {//rsi_day分（例：5個分）の値幅を取る
                //System.out.println("j+i=" + (j + i));
                double hilow = hilowList.get(j + i);
                if (hilow > 0) {
                    hiTotal += hilow;//hilowが+であればhiTotalに加算
                } else if (hilow < 0) {
                    lowTotal -= hilow;//hilowが-であればlowTotalに加算
                    //**重要** 
                    //lowTotal += -hilow; 「-hilow」変数の前に-をつけると＋ーを反転できる
                    //もしくは、-=でも同じ　lowTotal = lowTotal - hilow;
                }
            }
            Double hiHeikin = hiTotal/rsi_day;//平均値に変換
            Double lowHeikin = lowTotal/rsi_day;//平均値に変換
            Double kotae = rsiKeisan(hiHeikin,lowHeikin);//RSI計算メソッド呼び出し
            rsiList.add(kotae);
            //System.out.println(kotae);
            //System.out.println((i+rsi_day)+":-RSI-:"+kotae+"％");

        }

    }

    public Object zeroCheck(Object obj) {
        Double doubleValue = Double.parseDouble(obj.toString());
        Object kotae = obj;
        if (doubleValue <= 0) {
            kotae = 0;
        }
        return kotae;
    }

    public Double objdoubleExchenge(Object obj) {
        Double doubleValue = Double.parseDouble(obj.toString());
        return doubleValue;
    }

    public void nehaba() {
        for (int i = 0; i < this.closeList.size(); i++) {//終値から値幅を計算してhilowListに代入
            double a = this.closeList.get(i);//当日
            double b;//前日
            double nehabaDouble = 0;//値幅
            if (i != 0) {       //一番初め(i=0)は前日の値が無いのでチェックして飛ばす
                b = this.closeList.get(i - 1);
                nehabaDouble = a - b;//値幅=当日a-前日b
            }
            hilowList.add(nehabaDouble);//hilowListに値幅を挿入
        }
        System.out.println("値幅のList化終了");
    }

    public Double rsiKeisan(Double highHeikin, Double lowHeikin) {//RSIの計算：RSI=(high/(high+low))*100            
        Double kotae = (highHeikin / (highHeikin + lowHeikin)) * 100;

        return kotae;//RSIをDouble型で返す
    }

}
