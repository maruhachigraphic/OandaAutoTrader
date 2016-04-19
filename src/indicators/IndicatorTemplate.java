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
public abstract class IndicatorTemplate {

    ArrayList<String[]> fxArrayData;
    int span;

    /**
     *Object型をDouble型に変換する
     * @param obj
     * @return
     */
    public Double objdoubleExchenge(Object obj) {
        Double doubleValue = Double.parseDouble(obj.toString());
        return doubleValue;
    }

    /**
     *String型をDouble型に変換する
     * @param str
     * @return
     */
    public Double stringDoubleExchenge(String str) {
        Double doubleValue = Double.parseDouble(str);
        return doubleValue;
    }

    /**
     *Object型を一旦Double型に変換し、0以下であれば0で返す
     * @param obj
     * @return
     */
    public Object objectZeroCheck(Object obj) {
        Double doubleValue = Double.parseDouble(obj.toString());
        Object kotae = obj;
        if (doubleValue <= 0) {
            kotae = 0;
        }
        return kotae;
    }
}
