/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxratechecker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author kimuratadashi
 */
public class Strategy {
//フィールド

    private static List<Object[]> hiashiList, ichimokuList, rsiList;
//コンストラクタ    

    Strategy(ArrayList<Object[]> hiashi, List<Object[]> ichimoku, List<Object[]> rsi) {
        this.hiashiList = hiashi;
        this.ichimokuList = ichimoku;
        this.rsiList = rsi;

        System.out.println("ストラテジースタート");
        RSIStrategy();
        IchimokuStrategy();
    }
//メソッド

    public void RSIStrategy() {
        System.out.println("＞ RSIストラテジー");
        rsiList.stream().forEach((Object[] rsiObj) -> {
            double rsidata = new Double(rsiObj[1].toString());
            if (rsidata > 80) {//RSIが79%以上ならフラグ発生
                System.out.println("over80％ " + DayConvert(rsiObj[0]) + " " + rsiObj[1]);
            } else if (rsidata < 21 && rsidata > 0) {//RSIが21％以下0以上ならフラグ発生
                System.out.println("under20％ " + DayConvert(rsiObj[0]) + " " + rsiObj[1]);
            }
        });
    }

    public void IchimokuStrategy() {
        System.out.println("＞ 一目ストラテジー");
        for (Object[] ichimokuSet : ichimokuList) {
            double doubleS1 = Double.parseDouble(ichimokuSet[3].toString());//先行1
            double doubleS2 = Double.parseDouble(ichimokuSet[4].toString());//先行2
            Object ichimokuDay = DayConvert(ichimokuSet[0]);//ichimokuSet[0]＝unixtime
            System.out.println(ichimokuDay);//日付を表示
            Object[] hiashiSet = new Object[4];
            hiashiSet = DayCheck(ichimokuSet[0]);//DayCheckは日足リストのObject[]を抽出してObject[]を返す。結果をObject型の配列hiashiSetに代入。
            double doubleClose = Double.parseDouble(hiashiSet[4].toString());//hiashiSet[4]から終値(close)をdouble型で取得
            //終値が先行1(S1)か先行2(S2)より上か下かを表示
            if (doubleClose > doubleS1){System.out.print("S1:"+"△ ");}else{System.out.print("S1:"+"▼ ");}
            if (doubleClose > doubleS2){System.out.println("S2:"+"△");}else{System.out.println("S2:"+"▼");}
        }

    }

    public Date DayConvert(Object obj) {//日足リスト等から取ったObject型を日付に変換
        long dayLong = new Long(obj.toString());
        Instant dayInst = Instant.ofEpochSecond(dayLong);
        Date date = Date.from(dayInst);
        return date;
    }

    public Object[] DayCheck(Object obj) {//日足リストを日付でチェックし、日足リストList<Object[]>のObject[]を抽出
        Object[] answerObj = {0, 0, 0, 0, 0};//リセット
        //System.out.println("DayCheck");
        for (Object[] hiashiObj : hiashiList) {
            if (obj.equals(hiashiObj[0])) {//引数objとhiashiObj[0]（unixタイム）を比較
                //System.out.println("flag");
                answerObj = hiashiObj;//hiashiListの1行を代入
            }
        }
        //for(Object i :answerObj){System.out.println("i = " + i);}
        return answerObj;
    }
}
