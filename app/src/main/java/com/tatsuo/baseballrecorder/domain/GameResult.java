package com.tatsuo.baseballrecorder.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tatsuo on 2015/08/12.
 */
public class GameResult implements Serializable {

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

    private int inning;
    private int inning2;
    private int hianda;
    private int hihomerun;
    private int dassanshin;
    private int yoshikyu;
    private int yoshikyu2;
    private int shitten;
    private int jisekiten;
    private boolean kanto;
    private int tamakazu;
    private int sekinin;

    public static final int NON_REGISTED = -999;
    public static final int TAMAKAZU_NONE = -999;

    public static final String[] INNING = {"","1回","2回","3回","4回","5回","6回","7回","8回","9回","10回","11回","12回"};
    public static final String[] INNING2 = {"","0/3","1/3","2/3"};
    public static final String[] INNING_PICKER = {"0回","1回","2回","3回","4回","5回","6回","7回","8回","9回","10回","11回","12回"};
    public static final String[] INNING2_PICKER = {"","0/3","1/3","2/3"};

    public static final String[] SEKININ = {"","勝利投手","敗戦投手","ホールド","セーブ"};
    public static final String[] SEKININ_PICKER = {"なし","勝利投手","敗戦投手","ホールド","セーブ"};
    public static final String[] SEKININ_COLOR = {"","#FF4444","","#4444FF","#4444FF"};

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

        inning = 0;
        inning2 = 0;
        hianda = 0;
        hihomerun = 0;
        dassanshin = 0;
        yoshikyu = 0;
        yoshikyu2 = 0;
        shitten = 0;
        jisekiten = 0;
        kanto = false;
        tamakazu = TAMAKAZU_NONE;
        sekinin = 0;
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

    public int getInning() {
        return inning;
    }

    public void setInning(int inning) {
        this.inning = inning;
    }

    public int getInning2() {
        return inning2;
    }

    public void setInning2(int inning2) {
        this.inning2 = inning2;
    }

    public int getHianda() {
        return hianda;
    }

    public void setHianda(int hianda) {
        this.hianda = hianda;
    }

    public int getHihomerun() {
        return hihomerun;
    }

    public void setHihomerun(int hihomerun) {
        this.hihomerun = hihomerun;
    }

    public int getDassanshin() {
        return dassanshin;
    }

    public void setDassanshin(int dassanshin) {
        this.dassanshin = dassanshin;
    }

    public int getYoshikyu() {
        return yoshikyu;
    }

    public void setYoshikyu(int yoshikyu) {
        this.yoshikyu = yoshikyu;
    }

    public int getYoshikyu2() {
        return yoshikyu2;
    }

    public void setYoshikyu2(int yoshikyu2) {
        this.yoshikyu2 = yoshikyu2;
    }

    public int getShitten() {
        return shitten;
    }

    public void setShitten(int shitten) {
        this.shitten = shitten;
    }

    public int getJisekiten() {
        return jisekiten;
    }

    public void setJisekiten(int jisekiten) {
        this.jisekiten = jisekiten;
    }

    public boolean isKanto() {
        return kanto;
    }

    public void setKanto(boolean kanto) {
        this.kanto = kanto;
    }

    public int getSekinin() {
        return sekinin;
    }

    public void setSekinin(int sekinin) {
        this.sekinin = sekinin;
    }

    public int getTamakazu() {
        return tamakazu;
    }

    public void setTamakazu(int tamakazu) {
        this.tamakazu = tamakazu;
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
        resultString.append(inning+","+inning2+","+hianda+","+hihomerun+","+dassanshin+","+yoshikyu+","+yoshikyu2+","
                +shitten+","+jisekiten+","+( kanto ? 1 : 0 )+","+sekinin+","+tamakazu+"\n");

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
        String[] stringList5 = stringList[4].split(",", -1);
        if(stringList5.length >= 12) {
            gameResult.setInning(Integer.parseInt(stringList5[0]));
            gameResult.setInning2(Integer.parseInt(stringList5[1]));
            gameResult.setHianda(Integer.parseInt(stringList5[2]));
            gameResult.setHihomerun(Integer.parseInt(stringList5[3]));
            gameResult.setDassanshin(Integer.parseInt(stringList5[4]));
            gameResult.setYoshikyu(Integer.parseInt(stringList5[5]));
            gameResult.setYoshikyu2(Integer.parseInt(stringList5[6]));
            gameResult.setShitten(Integer.parseInt(stringList5[7]));
            gameResult.setJisekiten(Integer.parseInt(stringList5[8]));
            gameResult.setKanto(Integer.parseInt(stringList5[9]) == 1);
            gameResult.setSekinin(Integer.parseInt(stringList5[10]));
            gameResult.setTamakazu(Integer.parseInt(stringList5[11]));
        }

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
            stringBuilder.append(battingResult.getBattingResultShortString(true));
            stringBuilder.append(" ");
        }

        if(inning != 0 || inning2 != 0){
            if(battingResultList.size() > 0){
                stringBuilder.append("/ ");
            }
            stringBuilder.append(getInningString());
            stringBuilder.append(" ");
            stringBuilder.append((shitten == 0 ? "無" : shitten)+"失点");
            stringBuilder.append(" ");
            stringBuilder.append(getSekininString(true));
        }

        return stringBuilder.toString();
    }

    public String getInningString(){
        return getInningString(inning, inning2);
    }

    public static String getInningString(int inning, int inning2){
        if(inning == 0 && inning2 == 0){
            return "";
        } else if(inning == 0){
            return INNING2[inning2]+"回";
        } else {
            return INNING[inning]+INNING2[inning2];
        }
    }

    public String getSekininString(boolean withColor){
        return getSekininString(sekinin, withColor);
    }

    public static String getSekininString(int sekinin, boolean withColor){
        String resultString = SEKININ[sekinin];

        if(withColor && SEKININ_COLOR[sekinin].equals("") == false){
            String beforeTag = "<font color=\""+SEKININ_COLOR[sekinin]+"\">";
            String afterTag = "</font>";
            return beforeTag+resultString+afterTag;
        } else {
            return resultString;
        }
    }
}
