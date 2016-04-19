/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oandaautotrader;

import strategy.Strategy_D_macd_plugin;
import com.oanda.fxtrade.api.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import strategy.Strategy_E_macDandP_plugin;

/**
 * OANDA用の自動売買システム このクラスから始まります
 *
 * @author maruhachi OANDA用の自動売買システム
 */
public class OandaAutoTrader implements Observer {

    /**
     * OandaAutoTraderのバージョンをStringで格納
     */
    public static final String VERSION = "OandaAutoTrader ver0.44macp";

    // For keyboard input
    /**
     * キー入力用のオブジェクト
     */
    public static final EasyIn easyIn = new EasyIn();

    // the FXClient object will perform all interactions with the OANDA FXGame/FXTrade server
    public static FXClient fxclient;

    // The current FXTrade user
    private User user;
    private static String username;
    private static String password;

    // The current accountLocal
    public static Account account;
    private static int accountID;

    //FXPair
    public static FXPair fxpair;
    // The current rates table
    private RateTable rateTable;

    /**
     * ストラテジーで取得した結果を代入する変数
     */
    public static volatile double[] strategyResult;
    
    /**
     *GetHistoryで取得する日足のリストを代入する変数
     */
    public static volatile ArrayList<String[]> HiashiList;

    /**
     * 現在建てているトランザクションナンバー
     */
    public volatile long transactionNum;//現在建てている建玉のtransactionナンバーを取得

    /**
     * 購入後リリースするフラグ
     */
    public volatile boolean FlagSell;

    /**
     * トレーリングストップロスの値を代入する変数
     */
    public static double TSL;

    /**
     * 全体の実行時間 roopメソッドで使用 1 * 60 * 1000ms = 1分
     */
    public static int time;//分を入れる 
    public final int sleepCount;

    /**
     * 日足のインターバル、MACDシグナル、中長期間、短期期間
     */
    public static long interval;
    public static int signal;
    public static int intM;
    public static int intS;
    
    /**
     *MACPのスパン
     */
    public static int macpSpan;

    /**
     * コンストラクタ
     */
    public OandaAutoTrader() {
        //プロパティーの呼び出し
        fileReader();

        //FXPairの設定
        this.fxpair = com.oanda.fxtrade.api.API.createFXPair("USD/JPY");

        //トレーリングストップの設定
        TSL = 0.0;

        //全体の実行時間 roopメソッドで使用 1 * 60 * 1000ms = 1分
        time = 5;//分を入れる（10時間） 
        sleepCount = (time * 60 * 1000);

        //ストラテジーの期間を設定
        interval = TimeGetter.TIME1MIN;
        signal = 9;
        intM = 24;
        intS = 12;
        
        macpSpan = 24;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        OandaAutoTrader thisClass = new OandaAutoTrader();
        thisClass.init(username, password);

//定期実行でストラテジーの呼び出し
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);//遅延実行や定期実行をサポートするスレッドプール
        ScheduledFuture<?> future;

        ExecutorService executorFuture = Executors.newFixedThreadPool(2);//future用のスレッド 

        //ラムダ式 executor.scheduleAtFixedRateで定期的に命令を実行する。
        future = executor.scheduleAtFixedRate(
                () -> {//Runnerble()クラスのインスタンスをラムダ式で挿入している
                    CompletableFuture<double[]> futureCall = new CompletableFuture<>();//スレッドの実行結果で呼ばれるコールバック処理。エラー処理は省略
                    //ストラテジーの結果を呼び出し
                    futureCall.whenCompleteAsync((double[] result, Throwable error) -> {
                        strategyResult = result;
                        //System.out.println("Result =" + Arrays.toString(result));
                    });
                    executorFuture.execute(new Strategy_E_macDandP_plugin(thisClass, futureCall));//！！！！ここでストラテジーをnewする！！！！！！
                    //System.out.println("strategyResult =" + Arrays.toString(strategyResult));

                }, 1, 15, TimeUnit.SECONDS);//scheduleAtFixedRate,時間指定　15秒

        //ティッカークラスの呼び出し
        FXRateEvent ticker = new TickerE(thisClass, fxpair);//TickerクラスはFXRateEventを継承している。
        //getEventManager.add(FXEvent型)でFXEventを動作させる。何かイベントがあるごとに作動する。
        thisClass.rateTable.getEventManager().add(ticker);

        //executorのスケジュール　ラムダ式
        executor.schedule(() -> {
            executor.shutdown();
            executorFuture.shutdown();
        }, time, TimeUnit.MINUTES);

        //スレッドでループ、もしくはスリープさせるメソッド
        thisClass.roop();

        //終了
        thisClass.orderClose();//取引中のポジションを手仕舞う
        System.out.println("終了前にtransactionリストを取得、保存");
        TransactionCheck transactoncheck = new TransactionCheck(thisClass);//新規にtransactioncheckのインスタンスを作成
        ArrayList<String> transactionArray = new ArrayList<>();
        transactionArray = transactoncheck.getAll();
        //transactionArray.stream().forEach((a) -> {System.out.println(a);});//トランザクションリストのプリント

        thisClass.fxclient.logout();
        fxclient.logout();
        System.out.println("ログアウト");

    }

    /**
     * mainからinitを呼び出し、oandaサーバに接続
     *
     * @param username_init usernameを入れる
     * @param password_init passwordを入れる
     */
    private void init(String username_init, String password_init) {
        try {
            System.out.println("creating FXClient object...");

            fxclient = API.createFXGame();
            /*
             * Set this object to be an Observer of the FXClient
             * to be notified of connections/disconnections/updates from the server
             */
            fxclient.addObserver(this);
            /*
             * connect and login to the server
             *
             */

            System.out.println("logging in...");
            fxclient.setProxy(false);
            fxclient.setWithRateThread(true);
            fxclient.setWithKeepAliveThread(true);
            fxclient.login(username_init, password_init, "OandaAutoTrader");

            // save the password_init in case a reconnection is needed later
            this.username = username_init;
            this.password = password_init;

            //スタートポイント
            startPoint();
            //
        } catch (InvalidUserException | InvalidPasswordException | SessionException | MultiFactorAuthenticationException ex) {
            Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * startMenuThread initのtry節中で使う。
     */
    private void startPoint() {
        //アカウントの変更
        try {
            account = user.getAccountWithId(accountID);
        } catch (AccountException ex) {
            Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("なんか押すと始まる！");
        String name = new java.util.Scanner(System.in).nextLine();
        System.out.println("おけ！");
        /*
         Thread menuThread = new Thread("Example.menuThread") {
         @Override
         public void run() {
         System.out.println("＊＊＊＊＊MenuThread＊＊＊＊＊");
         System.out.println("ストラテジー呼び出し");
         int i = 0;
         do {
         i++;
         System.out.println("ここに処理を書く");
         } while (i < 3);

         //OandaAutoTrader.this.getHiashiList();
         } // END RUN()
         }; // END MENUTHREAD()

         menuThread.start();
         */

    }

    @Override
    public void update(Observable source, Object status) {
        try {
            if (source == fxclient) {
                // connected to server, update User, Account and RateTable
                if (status.equals(FXClient.CONNECTED)) {
                    System.out.println("OAT: setting user...");
                    user = fxclient.getUser();
                    //
                    System.out.println("OAT: setting account...");
                    account = (Account) user.getAccounts().elementAt(0);
                    //
                    System.out.println("OAT: fetching rate table...");
                    rateTable = fxclient.getRateTable();//fxclientからRateTableを取得する
                } // disconnected from server, attempt reconnection
                else if (status.equals(FXClient.DISCONNECTED)) {
                    System.out.println("OAT: 接続が切れました。 再接続を試みます...");
                    fxclient.login(username, password, "OandaAutoTraderReconnect");
                    //アカウントの設定
                    try {
                        account = user.getAccountWithId(accountID);
                    } catch (AccountException ex) {
                        Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        } catch (OAException oa) {
            System.out.println("Example: caught: " + oa);
        }
    }

    /**
     * ループ処理用メソッド
     */
    private void roop() {
        /*
         while (true) {
         // 処理
         try {
         Thread.sleep(1 * 60 * 1000); // 5分おき
         } catch (InterruptedException e) {
         }
         }
         */

        try {
            Thread.sleep(sleepCount);//全体の実行時間をスリープで決めている。
        } catch (InterruptedException ex) {
            Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fileReader() {
        Reader fr = null;
        try {
            fr = new FileReader("OandaAutoTrader.properties");
            Properties p = new Properties();
            p.load(fr);
            this.username = p.getProperty("UserName");
            this.password = p.getProperty("PassWord");
            this.accountID = Integer.parseInt(p.getProperty("AccountID"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(OandaAutoTrader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void orderClose() {
        SetOrder setorder = new SetOrder(this);
        setorder.setRelease(transactionNum);
    }

}
