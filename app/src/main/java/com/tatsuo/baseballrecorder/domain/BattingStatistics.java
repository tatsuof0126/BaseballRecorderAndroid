package com.tatsuo.baseballrecorder.domain;

import java.util.List;

/**
 * Created by tatsuo on 2015/08/21.
 */
public class BattingStatistics {

    private int boxs;
    private int atbats;
    private int hits;
    private int singles;
    private int doubles;
    private int triples;
    private int homeruns;
    private int strikeouts;
    private int walks;
    private int sacrifices;
    private int sacrificeflies;
    private int daten;
    private int tokuten;
    private int steal;

    private float average;
    private float obp;
    private float slg;
    private float ops;

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getBoxs() {
        return boxs;
    }

    public void setBoxs(int boxs) {
        this.boxs = boxs;
    }

    public int getAtbats() {
        return atbats;
    }

    public void setAtbats(int atbats) {
        this.atbats = atbats;
    }

    public int getSingles() {
        return singles;
    }

    public void setSingles(int singles) {
        this.singles = singles;
    }

    public int getDoubles() {
        return doubles;
    }

    public void setDoubles(int doubles) {
        this.doubles = doubles;
    }

    public int getTriples() {
        return triples;
    }

    public void setTriples(int triples) {
        this.triples = triples;
    }

    public int getHomeruns() {
        return homeruns;
    }

    public void setHomeruns(int homeruns) {
        this.homeruns = homeruns;
    }

    public int getStrikeouts() {
        return strikeouts;
    }

    public void setStrikeouts(int strikeouts) {
        this.strikeouts = strikeouts;
    }

    public int getWalks() {
        return walks;
    }

    public void setWalks(int walks) {
        this.walks = walks;
    }

    public int getSacrifices() {
        return sacrifices;
    }

    public void setSacrifices(int sacrifices) {
        this.sacrifices = sacrifices;
    }

    public int getSacrificeflies() {
        return sacrificeflies;
    }

    public void setSacrificeflies(int sacrificeflies) {
        this.sacrificeflies = sacrificeflies;
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

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public float getObp() {
        return obp;
    }

    public void setObp(float obp) {
        this.obp = obp;
    }

    public float getSlg() {
        return slg;
    }

    public void setSlg(float slg) {
        this.slg = slg;
    }

    public float getOps() {
        return ops;
    }

    public void setOps(float ops) {
        this.ops = ops;
    }

    public static BattingStatistics calculateBattingStatistics(List<GameResult> gameResultList){
        BattingStatistics battingStatistics = new BattingStatistics();

        for(GameResult gameResult : gameResultList) {
            for(BattingResult battingResult : gameResult.getBattingResultList()){
                battingStatistics.boxs++;
                battingStatistics.atbats   += battingResult.getStatisticsCounts(BattingResult.TYPE_ATBATS);
                battingStatistics.hits     += battingResult.getStatisticsCounts(BattingResult.TYPE_HITS);
                battingStatistics.singles  += battingResult.getStatisticsCounts(BattingResult.TYPE_SINGLES);
                battingStatistics.doubles  += battingResult.getStatisticsCounts(BattingResult.TYPE_DOUBLES);
                battingStatistics.triples  += battingResult.getStatisticsCounts(BattingResult.TYPE_TRIPLES);
                battingStatistics.homeruns += battingResult.getStatisticsCounts(BattingResult.TYPE_HOMERUNS);
                battingStatistics.strikeouts += battingResult.getStatisticsCounts(BattingResult.TYPE_STRIKEOUTS);
                battingStatistics.walks    += battingResult.getStatisticsCounts(BattingResult.TYPE_WALKS);
                battingStatistics.sacrifices += battingResult.getStatisticsCounts(BattingResult.TYPE_SACRIFICES);
                battingStatistics.sacrificeflies += battingResult.getStatisticsCounts(BattingResult.TYPE_SACRIFICEFLIES);
            }

            battingStatistics.daten += gameResult.getDaten();
            battingStatistics.tokuten += gameResult.getTokuten();
            battingStatistics.steal += gameResult.getSteal();
        }

        battingStatistics.calculateStatistics();

        return battingStatistics;
    }

    private void calculateStatistics(){
        // 打率＝安打／打数
        average = (float)hits / (float)atbats;

        // 出塁率＝（安打＋四死球）／（打数＋四死球＋犠飛）
        obp = (float)(hits+walks) / (float)(atbats+walks+sacrificeflies);

        // 長打率＝（単打＋二塁打×２＋三塁打×３＋本塁打×４）／打数
        slg = (float)(singles+doubles*2+triples*3+homeruns*4) / (float)atbats;

        // OPS＝出塁率＋長打率
        ops = obp + slg;
    }

}
