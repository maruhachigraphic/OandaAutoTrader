/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxratechecker;

/**
 *
 * @author kimuratadashi
 */
enum RSIParameter {

    //tenkan_day, kijun_day, senkou1_day, senkou2_day, chikouSpan_day

    FXPARAMETER_L(14),//enumは各々メソッドと同じ使い方ができる
    FXPARAMETER_M(12),//なので、()を付けてコンストラクタに引数を渡せる
    FXPARAMETER_S(5);

    private final int RSI;//RSIの期間パラメータ

    RSIParameter(int param) {//コンストラクタ 引数はenumの引数が入る
        this.RSI = param;//引数paramを期間パラメータに代入
    }

    public int getStatus() {//期間パラメータを取得するgetter
        int status = RSI;
        return status;
    }
}
