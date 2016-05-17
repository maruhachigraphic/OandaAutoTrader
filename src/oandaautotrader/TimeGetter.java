/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oandaautotrader;

import java.lang.reflect.Field;

/**
 *時間を格納したフィールド群
 * @author kimuratadashi
 * 
 * 
 */
public final class TimeGetter {
    
    // 5 = 5 sec
    // 10 = 10 sec
    // 30 = 30 sec
    // 60 = 1 min
    // 300 = 5 min
    // 1800 = 30 min
    // 10800 = 3 hour
    // 86400  = 1 day

    public static final long TIME5SEC = 5,
            TIME10SEC = 10,
            TIME15SEC = 15,
            TIME30SEC = 30,
            TIME1MIN = 60,
            TIME2MIN30SEC = 150,
            TIME3MIN = 180,
            TIME5MIN = 300,
            TIME10MIN = 600,
            TIME15MIN = 900,
            TIME30MIN = 1800,
            TIME1HOUR = 3600,
            TIME4HOUR = 14400,
            TIME8HOUR = 28800,
            TIME1DAY = 86400;

    void getField() {
        for (Field field : TimeGetter.class.getDeclaredFields()) {
            System.out.println(field.getName());
        }
    }
}
