/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oandaautotrader;

import com.oanda.fxtrade.api.Account;
import com.oanda.fxtrade.api.AccountException;
import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.Transaction;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * トランザクションを取得
 *
 * @author maruhachi
 */
public class GetTransaction {

    private final OandaAutoTrader OAT;
    private final FXPair pair;
    private final FXClient fxclient;
    private final Account account;
    private ArrayList<String> transactionArray;
            
            
    GetTransaction(OandaAutoTrader OAT) {
        this.OAT = OAT;
        this.pair = OAT.fxpair;
        this.fxclient = OAT.fxclient;
        this.account = OAT.account;
        transactionArray = new ArrayList<>();
    }

    public String getTransaction() {//トレードの最後のトランザクション（チケットナンバー）を取得
        String transactionNumber = "Transaction";
        

        // request the the current account's MarketOrders
        //Vector<? extends MarketOrder> trades = null;
        Vector<? extends Transaction> transactionVector = null;//vectorでtransactionが入れられている
        try {
            transactionVector = account.getTransactions();
            transactionNumber = transactionVector.lastElement().toString();
            System.out.println("トランザクション：" + transactionVector.lastElement().toString());//トランザクションVectorの最後の行を表示      
        } catch (AccountException ae) {
            System.out.println("caseGetOpenTrades(): caught: " + ae);
        }
        return transactionNumber;
    }
    
    public ArrayList<String> getAll(){
    
        Vector<? extends Transaction> transactionVector = null;
        try {
            transactionVector = account.getTransactions();
        } catch (AccountException ex) {
            Logger.getLogger(GetTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.transactionArray.add("timestamp"+","+"transactionNum"+","+ "type" + "," + "pair" + "," + 
                   "購入金額" + "," + "資産合計" + "," + "amount");
        
        for (Transaction a:transactionVector){
           this.transactionArray.add(a.getTimestamp()+","+a.getTransactionNumber()+","+a.getType() + "," + a.getPair() + "," + 
                   a.getPrice() + "," + a.getBalance() + "," + a.getAmount());
        }
        new TextWriter(this.transactionArray);
    
        return this.transactionArray;
        
    }
    
}
