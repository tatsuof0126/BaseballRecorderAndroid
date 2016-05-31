package com.tatsuo.baseballrecorder.domain;

import java.util.List;

/**
 * Created by tatsuo on 2016/05/17.
 */
public class PitchingStatistics {

    private int games;
    private int win;
    private int lose;
    private int save;
    private int hold;
    private int inning;
    private int inning2;
    private int hianda;
    private int hihomerun;
    private int dassanshin;
    private int yoshikyu;
    private int yoshikyu2;
    private int shitten;
    private int jisekiten;
    private int kanto;

    private float era;
    private float shoritsu;
    private float whip;
    private float k9;
    private float kbb;
    private float fip;

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getSave() {
        return save;
    }

    public void setSave(int save) {
        this.save = save;
    }

    public int getHold() {
        return hold;
    }

    public void setHold(int hold) {
        this.hold = hold;
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

    public int getKanto() {
        return kanto;
    }

    public void setKanto(int kanto) {
        this.kanto = kanto;
    }

    public float getEra() {
        return era;
    }

    public void setEra(float era) {
        this.era = era;
    }

    public float getShoritsu() {
        return shoritsu;
    }

    public void setShoritsu(float shoritsu) {
        this.shoritsu = shoritsu;
    }

    public float getWhip() {
        return whip;
    }

    public void setWhip(float whip) {
        this.whip = whip;
    }

    public float getK9() {
        return k9;
    }

    public void setK9(float k9) {
        this.k9 = k9;
    }

    public float getKbb() {
        return kbb;
    }

    public void setKbb(float kbb) {
        this.kbb = kbb;
    }

    public float getFip() {
        return fip;
    }

    public void setFip(float fip) {
        this.fip = fip;
    }

    public static PitchingStatistics calculatePitchingStatistics(List<GameResult> gameResultList){
        PitchingStatistics pitchingStatistics = new PitchingStatistics();

        for(GameResult gameResult : gameResultList) {
            if(gameResult.getInning() != 0 || gameResult.getInning2() != 0) {
                pitchingStatistics.games++;
            }

            switch (gameResult.getSekinin()) {
                case 1:
                    pitchingStatistics.win++;
                    break;
                case 2:
                    pitchingStatistics.lose++;
                    break;
                case 3:
                    pitchingStatistics.hold++;
                    break;
                case 4:
                    pitchingStatistics.save++;
                    break;
            }

            pitchingStatistics.inning += gameResult.getInning();
            switch (gameResult.getInning2()) {
                case 2: // 1/3
                    pitchingStatistics.inning2++;
                    break;
                case 3: // 2/3
                    pitchingStatistics.inning2+=2;
                    break;
            }

            pitchingStatistics.hianda     += gameResult.getHianda();
            pitchingStatistics.hihomerun  += gameResult.getHihomerun();
            pitchingStatistics.dassanshin += gameResult.getDassanshin();
            pitchingStatistics.yoshikyu   += gameResult.getYoshikyu();
            pitchingStatistics.yoshikyu2  += gameResult.getYoshikyu2();
            pitchingStatistics.shitten    += gameResult.getShitten();
            pitchingStatistics.jisekiten  += gameResult.getJisekiten();

            if(gameResult.isKanto()){
                pitchingStatistics.kanto++;
            }
        }

        pitchingStatistics.inning += (int)(pitchingStatistics.inning2 / 3);
        pitchingStatistics.inning2 = pitchingStatistics.inning2 % 3;

        pitchingStatistics.calculateStatistics();

        return pitchingStatistics;
    }

    private void calculateStatistics(){
        float realinning = (float)(inning*3 + inning2) / 3.0f;

        // ９回固定（将来は７回での計算も）
        float gameinning = 9.0f;

        // 勝率
        shoritsu = (float)win / (win + lose);

        // 防御率＝自責点／投球回数×９
        era = (float)jisekiten / realinning * gameinning;

        // WHIP＝（被安打＋与四球）／投球回数
        whip = (float)(hianda + yoshikyu) / realinning;

        // 奪三振率＝奪三振／投球回数×９
        k9 = (float)dassanshin / realinning * gameinning;

        // K/BB＝奪三振／与四球
        kbb = (float)dassanshin / yoshikyu;

        // FIP=（被本塁打×13＋四死球（敬遠除く）×3−奪三振×2）÷イニング数+3.12（リーグ定数）
        fip = (float)(hihomerun*13+(yoshikyu+yoshikyu2)*3-dassanshin*2) / realinning + 3.12f;
    }

    public String getInningString(){
        String[] INNING_STR = {"","1/3","2/3"};
        if(inning != 0){
            return inning+"回"+INNING_STR[inning2];
        } else if(inning2 != 0){
            return INNING_STR[inning2];
        } else {
            return "0回";
        }
    }

}
