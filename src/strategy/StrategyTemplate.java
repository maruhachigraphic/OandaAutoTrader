/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import java.util.ArrayList;
import oandaautotrader.OandaAutoTrader;

/**
 * ストラテジープラグインを作成するための抽象クラス 例：Strategy_A_plugin extends StrategyTemplate{・・・} <br>
 *売買ポイントを知らせるための変数群を格納。
 * @author maruhachi
 */
public abstract class StrategyTemplate {
    ArrayList<String[]> hiashiArrayList;
    ArrayList<String[]> hiashiArrayListB;
    /**
     * 単一フラグ
     */
    //public static boolean flag;

    /**
     *ロング用フラグ
     */
    //public static boolean flagLong;

    /**
     *ショート用フラグ
     */
    //public static boolean flagShort;

    /**
     *ロングを手放すか否かのフラグ
     */
    //public static boolean flagLongRelease;

    /**
     *ショートを手放すか否かのフラグ
     */
    //public static boolean flagShortRelease;
    
    /**
     *OandaAutoTrader型を代入する変数
     */
    public static OandaAutoTrader oandaAutoTrader;
    
}
