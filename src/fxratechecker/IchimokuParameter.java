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
enum IchimokuParameter {

    //tenkan_day, kijun_day, senkou1_day, senkou2_day, chikouSpan_day
    FXPARAMETER_S(3, 9, 9, 18, 9),//enumは各々メソッドと同じ使い方ができる
    FXPARAMETER_M(7, 22, 22, 44, 22),//なので、()を付けてコンストラクタに引数を渡せる
    FXPARAMETER_L(9, 26, 26, 52, 26);

    private final int tenkan, kijun, senkou1, senkou2, chikouSpan;//一目の期間パラメータ

    IchimokuParameter(int tenkanInt, int kijunInt, int senkou1Int, int senkou2Int, int chikouSpanInt) {//コンストラクタ 引数はenumの引数が入る
        this.tenkan = tenkanInt;
        this.kijun = kijunInt;
        this.senkou1 = senkou1Int;
        this.senkou2 = senkou2Int;
        this.chikouSpan = chikouSpanInt;
    }

    public int[] getStatus() {//一目パラメータを取得するgetter 一目は項目が多いのでint[]型の配列に代入
        int[] status = {tenkan, kijun, senkou1, senkou2, chikouSpan};
        return status;
    }

}
