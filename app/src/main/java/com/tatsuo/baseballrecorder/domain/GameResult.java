package com.tatsuo.baseballrecorder.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tatsuo on 2015/08/12.
 */
public class GameResult {

    private String uuid;
    private int resultId;
    private int year;
    private int month;
    private int day;
    private String place;
    private String myteam;
    private String otherteam;
    private int myscore;
    private int otherscore;
    private int daten;
    private int tokuten;
    private int steal;
    private String memo;

    private List<BattingResult> battingResultList;

    public static final int NON_REGISTED = -999;

    public GameResult(){
        uuid = "";
        resultId = NON_REGISTED;
        year = 0;
        month = 0;
        day = 0;
        place = "";
        myteam = "";
        otherteam = "";
        myscore = 0;
        otherscore = 0;
        daten = 0;
        tokuten = 0;
        steal = 0;
        memo = "";
        battingResultList = new ArrayList();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMyteam() {
        return myteam;
    }

    public void setMyteam(String myteam) {
        this.myteam = myteam;
    }

    public String getOtherteam() {
        return otherteam;
    }

    public void setOtherteam(String otherteam) {
        this.otherteam = otherteam;
    }

    public int getMyscore() {
        return myscore;
    }

    public void setMyscore(int myscore) {
        this.myscore = myscore;
    }

    public int getOtherscore() {
        return otherscore;
    }

    public void setOtherscore(int otherscore) {
        this.otherscore = otherscore;
    }

    public int getDaten() {
        return daten;
    }

    public void setDaten(int daten) {
        this.daten = daten;
    }

    public int getTokuten() {
        return tokuten;
    }

    public void setTokuten(int tokuten) {
        this.tokuten = tokuten;
    }

    public int getSteal() {
        return steal;
    }

    public void setSteal(int steal) {
        this.steal = steal;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<BattingResult> getBattingResultList() {
        return battingResultList;
    }

    public void setBattingResultList(List<BattingResult> battingResultList) {
        this.battingResultList = battingResultList;
    }

    public String getGameResultString(){
        StringBuilder resultString = new StringBuilder();

        if("".equals(uuid) == true){
            // UUIDを作成
            uuid = UUID.randomUUID().toString();
        }

        // １行目：ファイル形式バージョン（V7）、UUID
        resultString.append("V7,"+uuid+"\n");

        // ２行目：試合情報（ID、年、月、日、場所、自チーム、相手チーム、自チーム得点、相手チーム得点、打点、得点、盗塁）
        resultString.append(resultId+","+year+","+month+","+day+","+place+","+myteam+","+otherteam+","
                +myscore+","+otherscore+","+daten+","+tokuten+","+steal+"\n");

        // ３行目：タグ（カンマ区切り）
        resultString.append("\n"); // 今のところ空

        // ４行目：打撃成績（場所、結果、場所、結果・・・・）
        for(BattingResult battingResult : battingResultList){
            resultString.append(battingResult.getPosition()+","+battingResult.getResult()+",");
        }
        if(battingResultList.size() > 0) {
            resultString.deleteCharAt(resultString.lastIndexOf(","));
        }
        resultString.append("\n");

        // ５行目：投手成績（投球回、投球回小数点以下、安打、本塁打、奪三振、与四球、与死球、失点、自責点、完投、責任投手、投球数）
        resultString.append("\n"); // 今のところ空

        // ６行目以降：メモ
        resultString.append(memo);

        return resultString.toString();
    }

    public static GameResult makeGameResult(String string){
        GameResult gameResult = new GameResult();

        String[] stringList = string.split("\n", -1);

        // １行目：ファイル形式バージョン（V7）、UUID
        String[] stringList1 = stringList[0].split(",", -1);
        gameResult.setUuid(stringList1[1]);

        // ２行目：試合情報（ID、年、月、日、場所、自チーム、相手チーム、自チーム得点、相手チーム得点、打点、得点、盗塁）
        String[] stringList2 = stringList[1].split(",", -1);
        gameResult.setResultId(Integer.parseInt(stringList2[0]));
        gameResult.setYear(Integer.parseInt(stringList2[1]));
        gameResult.setMonth(Integer.parseInt(stringList2[2]));
        gameResult.setDay(Integer.parseInt(stringList2[3]));
        gameResult.setPlace(stringList2[4]);
        gameResult.setMyteam(stringList2[5]);
        gameResult.setOtherteam(stringList2[6]);
        gameResult.setMyscore(Integer.parseInt(stringList2[7]));
        gameResult.setOtherscore(Integer.parseInt(stringList2[8]));
        gameResult.setDaten(Integer.parseInt(stringList2[9]));
        gameResult.setTokuten(Integer.parseInt(stringList2[10]));
        gameResult.setSteal(Integer.parseInt(stringList2[11]));

        // ３行目：タグ（カンマ区切り）
        // とりあえずスルー

        // ４行目：打撃成績（場所、結果、場所、結果・・・・）
        List<BattingResult> battingResultList = new ArrayList<BattingResult>();
        String[] stringList4 = stringList[3].split(",", -1);
        for(int i=0;i<stringList4.length/2;i++){
            BattingResult battingResult = new BattingResult();
            battingResult.setPosition(Integer.parseInt(stringList4[i*2]));
            battingResult.setResult(Integer.parseInt(stringList4[i*2+1]));
            battingResultList.add(battingResult);
        }
        gameResult.setBattingResultList(battingResultList);

        // ５行目：投手成績（投球回、投球回小数点以下、安打、本塁打、奪三振、与四球、与死球、失点、自責点、完投、責任投手、投球数）
        // とりあえずスルー

        // ６行目以降：メモ
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=5;i<stringList.length;i++){
            stringBuilder.append(stringList[i]);
            // 最終行じゃないなら改行を足す
            if(i != stringList.length - 1) {
                stringBuilder.append("\n");
            }
        }
        gameResult.setMemo(stringBuilder.toString());

        return gameResult;
    }

    public String getTitleString(){
        String returnString = "";

        String resultStr = "";
        if(myscore > otherscore){
            resultStr = "◯";
        } else if(myscore == otherscore){
            resultStr = "△";
        } else {
            resultStr = "●";
        }

//        returnString = year+"年"+month+"月"+day+"日 "+resultStr+" "+myscore+"-"+otherscore+" "+otherteam;
        returnString = month+"月"+day+"日 "+resultStr+" "+myscore+"-"+otherscore+" "+otherteam;

        return returnString;
    }

    public String getSubTitleString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(BattingResult battingResult : battingResultList){
            stringBuilder.append(battingResult.getBattingResultShortString());
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

}
