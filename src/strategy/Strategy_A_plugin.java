/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import fxratechecker.*;
import java.util.ArrayList;

/**
 * ストラテジー　デモプラグイン。newして使用する。
 * これはテストストラテジーです。
 * 一目均衡表とRSIを計算するパッケージfxratecheckerを呼び出します。
 * @author maruhachi
 */
public class Strategy_A_plugin extends StrategyTemplate {


    Strategy_A_plugin(ArrayList<Object[]> arraylist) {

        FXrateChecker fxratechecker = new FXrateChecker(arraylist,"M","M");
    }

}
