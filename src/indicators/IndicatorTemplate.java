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

    public Double objdoubleExchenge(Object obj) {
        Double doubleValue = Double.parseDouble(obj.toString());
        return doubleValue;
    }

    public Double stringDoubleExchenge(String str) {
        Double doubleValue = Double.parseDouble(str);
        return doubleValue;
    }

    public Object zeroCheck(Object obj) {
        Double doubleValue = Double.parseDouble(obj.toString());
        Object kotae = obj;
        if (doubleValue <= 0) {
            kotae = 0;
        }
        return kotae;
    }
}
