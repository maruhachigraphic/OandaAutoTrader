/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oandaautotrader;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.APPEND;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vittel
 */
public class TextWriter {

        TextWriter(List<String> linesList) {
        //LocalDateTime localDateTime = LocalDateTime.now();//ファイルネームに時間を入れるのでLocalDateTimeを取得。
        Object utc = System.currentTimeMillis();//unixtimeを取得

        System.out.println(utc);
        Path filename = Paths.get(utc + "transaction.csv");//Paths.get(ファイルパス)でPath型の変数を作っておく。
        try {
            if (!Files.exists(filename)) { //ファイルがあるか確認。
                Files.createFile(filename); //ファイルを新規作成したいときはこのcreateFileを追加。
            }
            Files.write(filename, linesList, UTF_8, APPEND);//ファイルにList型で書きこむ。
        } catch (IOException ex) {
            Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
