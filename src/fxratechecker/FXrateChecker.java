/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxratechecker;

import java.util.ArrayList;

/**
 *一目均衡表とRSIのデータを作成し、ストラテジーを作る
 * FXrateCheckerをnewして使用する
 * @author kimuratadashi
 */
public class FXrateChecker {

    static ArrayList<Object[]> FXarrayData = new ArrayList<>();
    //Object[]の中身 [0]=タイムスタンプ,[1]=open,[2]=max,[3]=min,[4]=close

    /**
     * FXrateChecker(ArrayList＜Object[]＞ A,String B,String C)<br>
     * A:日足データ　B:一目のスパン　S/M/L　C:RSIのスパン　S/M/L
     * @param hiashiset
     * ArrayList＜Object[]＞　日足データ
     * @param ichimokuspan
     * String 一目均衡表用のスパン設定 S/M/L　のいずれかを代入
     * @param rsispan
     * String RSI用のスパン設定 S/M/L のいずれかを代入
     */
    public FXrateChecker(ArrayList<Object[]> hiashiset, String ichimokuspan, String rsispan) {
        // TODO code application logic here
        FXarrayData = hiashiset;

        //パラメータの呼び出し　//短期 1：FXPARAMETER_S 標準 2：FXPARAMETER_M 長期 3：FXPARAMETER_L
        int[] ichparam;//一目パラメータの変数
        int rsiparameter;//RSIパラメータの変数

        ichparam = ichimokuParameterCheck(ichimokuspan);
        rsiparameter = RsiParameterCheck(rsispan);

        System.out.println("一目　転換:" + ichparam[0] + "基準:" + ichparam[1] + "先行1:" + ichparam[2] + "先行2:" + ichparam[3] + "遅行:" + ichparam[4]);
        System.out.println("rsi期間:" + rsiparameter);

        //出力
        FXRule fxrule = new FXRule(FXarrayData, ichparam, rsiparameter);//ArrayList<Object[]>:日足データ,int[]:一目のパラメータ,int:RSIのパラメータ
        //ストラテジー起動
        Strategy strategy = new Strategy(FXarrayData, fxrule.getIchimokuRule(), fxrule.getRSIRule());//ArrayList<Object[]>hiashi,List<Object[]>ichimoku,List<Object[]>rsi
    }

    /**
     *一目のパラメータをチェックする
     *引数はStringで S/M/L のいづれかを代入する。それ以外の引数の場合はMとしてチェックをはじめる
     * @param chk
     * S/M/Lを代入できる
     * @return
     * **保留**
     */
    public static int[] ichimokuParameterCheck(String chk) {
        int[] ichimokuparameter;
        switch (chk) {
            case "S":
                ichimokuparameter = IchimokuParameter.FXPARAMETER_S.getStatus();//ここでFXPARAMETER_Lオブジェクトとなる
                break;
            case "M":
                ichimokuparameter = IchimokuParameter.FXPARAMETER_M.getStatus();
                break;
            case "L":
                ichimokuparameter = IchimokuParameter.FXPARAMETER_L.getStatus();
                break;
            default:
                ichimokuparameter = IchimokuParameter.FXPARAMETER_M.getStatus();
                break;
        }
        return ichimokuparameter;
    }

    /**
     *RSIのパラメータをチェックする
     *引数はStringで S/M/L のいづれかを代入する。それ以外の引数の場合はMとしてチェックをはじめる
     * @param chk
     * 引数はStringで S/M/L のいづれかを代入する。それ以外の引数の場合はMとしてチェックをはじめる
     * @return
     * 保留
     */
    public static int RsiParameterCheck(String chk) {
        int rsiparameter;
        switch (chk) {
            case "S":
                rsiparameter = RSIParameter.FXPARAMETER_S.getStatus();
                break;
            case "M":
                rsiparameter = RSIParameter.FXPARAMETER_M.getStatus();
                break;
            case "L":
                rsiparameter = RSIParameter.FXPARAMETER_L.getStatus();
                break;
            default:
                rsiparameter = RSIParameter.FXPARAMETER_M.getStatus();
                break;
        }
        return rsiparameter;
    }

}
