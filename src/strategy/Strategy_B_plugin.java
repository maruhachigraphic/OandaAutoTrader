/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import fxratechecker.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import oandaautotrader.ObjectChenger;

/**
 * ストラテジー　デモプラグイン。newして使用する。 これはテストストラテジーBです。 日足１本分の中値を使ってストラテジーを組みます。
 *
 * @author maruhachi
 */
public class Strategy_B_plugin extends StrategyTemplate {

    ArrayList<Object[]> arraylistGlobal;
    
    

    Strategy_B_plugin(String[] arrayList, String[] arrayList2, String[] arrayList3) {
        arraylistGlobal = new ArrayList<>();
        double[] CP = new double[3];

        CP[0] = (Double.parseDouble(arrayList[1]) + Double.parseDouble(arrayList[4])) / 2;//長期の中値
        CP[1] = (Double.parseDouble(arrayList2[1]) + Double.parseDouble(arrayList2[4])) / 2;//中期の中値
        CP[2] = (Double.parseDouble(arrayList3[1]) + Double.parseDouble(arrayList3[4])) / 2;//短期の中値
        
        //長期が中期より下であればtrue
        boolean flagL = (CP[0]<CP[1] && CP[1]<CP[2]);
        System.out.println("CP[0]:" + CP[0] + " CP[1]:" + CP[1] +" CP[2]:" + CP[2]);
        System.out.println("flag:" + flagL);
    }

}
