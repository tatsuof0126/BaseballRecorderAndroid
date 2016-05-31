package com.tatsuo.baseballrecorder.domain;

import java.io.Serializable;

/**
 * Created by tatsuo on 2015/08/12.
 */
public class BattingResult implements Serializable {

    public static final String[] POSITIONS = {"","ピッチャー","キャッチャー","ファースト","セカンド","サード","ショート","レフト","センター","ライト","左中間","右中間","レフト線","ライト線"};
    public static final String[] POSITIONS_SHORT = {"","投","捕","一","二","三","遊","左","中","右","左中","右中","左線","右線"};
    public static final String[] POSITIONS_PICKER = {"ピッチャー","キャッチャー","ファースト","セカンド","サード","ショート","レフト","センター","ライト","左中間","右中間","レフト線","ライト線"};

    public static final String[] RESULTS = {"","ゴロ","フライ","ファールフライ","ライナー","エラー","フィルダースチョイス","ヒット","二塁打","三塁打","ホームラン","犠打","三振","振り逃げ","四球","死球","打撃妨害"};
    public static final String[] RESULTS_SHORT = {"","ゴ","飛","邪飛","直","失","野選","安","二","三","本","犠","三振","振逃","四球","死球","打妨"};
    public static final String[] RESULTS_PICKER = {"ゴロ","フライ","ファールフライ","ライナー","エラー","フィルダースチョイス","ヒット","二塁打","三塁打","ホームラン","犠打","三振","振り逃げ","四球","死球","打撃妨害"};
    public static final String[] RESULTS_COLOR = {"","","","","","","","#FF4444","#FF4444","#FF4444","#FF4444","#4444FF","","","#4444FF","#4444FF",""};

    public static final String[] PICKER1 = {"三振","四球","死球","ピッチャー","キャッチャー","ファースト","セカンド","サード","ショート","レフト","センター","ライト","左中間","右中間","レフト線","ライト線","振り逃げ","打撃妨害"};
    public static final String[] PICKER2 = {"ゴロ","フライ","ファールフライ","ライナー","エラー","フィルダースチョイス","ヒット","二塁打","三塁打","ホームラン","犠打"};
    public static final boolean[] NEEDS_POSITION = {false,false,false,true,true,true,true,true,true,true,true,true,true,true,true,true,false,false};

    public static final boolean[] NEED_POSITION = {false,true,true,true,true,true,true,true,true,true,true,true,false,false,false,false,true};

    public static final int TYPE_ATBATS = 0;
    public static final int TYPE_HITS = 1;
    public static final int TYPE_SINGLES = 2;
    public static final int TYPE_DOUBLES = 3;
    public static final int TYPE_TRIPLES = 4;
    public static final int TYPE_HOMERUNS = 5;
    public static final int TYPE_STRIKEOUTS = 6;
    public static final int TYPE_WALKS = 7;
    public static final int TYPE_SACRIFICES = 8;
    public static final int TYPE_SACRIFICEFLIES = 9;

    public static final int[][] BATTING_COUNT = {
            {0,1,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0}, // 打数
            {0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0}, // 安打
            {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}, // ヒット
            {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0}, // 二塁打
            {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0}, // 三塁打
            {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0}, // ホームラン
            {0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0}, // 三振
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0}, // 四死球
            {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0}  // 犠打
    };

    private int position;
    private int result;

    public static final int NON_REGISTED = 0;

    public BattingResult(){
        position = NON_REGISTED;
        result = NON_REGISTED;
    }

    public BattingResult(int result){
        this.position = NON_REGISTED;
        this.result = result;
    }

    public BattingResult(int position, int result){
        this.position = position;
        this.result = result;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getBattingResultString(boolean withColor){
        String resultString = "";
        if(position == NON_REGISTED){
            resultString = RESULTS[result];
        } else {
            resultString = POSITIONS[position]+RESULTS[result];
        }
        if(withColor && RESULTS_COLOR[result].equals("") == false){
            String beforeTag = "<font color=\""+RESULTS_COLOR[result]+"\">";
            String afterTag = "</font>";
            return beforeTag+resultString+afterTag;
        } else {
            return resultString;
        }
    }

    public String getBattingResultShortString(boolean withColor){
        String resultString = "";
        if(position == NON_REGISTED){
            resultString = RESULTS_SHORT[result];
        } else {
            resultString = POSITIONS_SHORT[position]+RESULTS_SHORT[result];
        }

        if(withColor && RESULTS_COLOR[result].equals("") == false){
            String beforeTag = "<font color=\""+RESULTS_COLOR[result]+"\">";
            String afterTag = "</font>";
            return beforeTag+resultString+afterTag;
        } else {
            return resultString;
        }
    }

    public int getStatisticsCounts(int type){
        if(type != TYPE_SACRIFICEFLIES){
            // 犠飛以外はBATTING_COUNTの値を返す
            return BATTING_COUNT[type][result];
        } else {
            // 犠打かつレフト・センター・ライト・左中間・右中間・レフト線・ライト線を犠飛として返す
            if(result == 11 &&
                    (position == 7 || position == 8 || position == 9 || position == 10 ||
                            position == 11 || position == 12 || position == 13)){
                return 1;
            }
            return 0;
        }
    }

}
