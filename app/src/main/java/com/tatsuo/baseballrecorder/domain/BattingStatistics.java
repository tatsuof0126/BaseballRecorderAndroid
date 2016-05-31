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
    private float isod;
    private float isop;
    private float rc27;

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

    public float getIsod() {
        return isod;
    }

    public void setIsod(float isod) {
        this.isod = isod;
    }

    public float getIsop() {
        return isop;
    }

    public void setIsop(float isop) {
        this.isop = isop;
    }

    public float getRc27() {
        return rc27;
    }

    public void setRc27(float rc27) {
        this.rc27 = rc27;
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

        // IsoD＝出塁率ー打率
        isod = obp - average;

        // IsoP＝長打率ー打率
        isop = slg - average;

        // RC27＝https://ja.wikipedia.org/wiki/RC_%28%E9%87%8E%E7%90%83%29
        // 出塁能力A = 安打 + 四球 + 死球 - 盗塁死 - 併殺打
        // 進塁能力B = 塁打 + 0.26 ×（四球 + 死球） + 0.53 ×（犠飛 + 犠打） + 0.64 × 盗塁 - 0.03 × 三振
        // 出塁機会C = 打数 + 四球 + 死球 + 犠飛 + 犠打
        // RC =（A+2.4×C）×（B+3×C）÷(9×C)－0.9×C
        // RC27 = RC÷（打数－安打＋盗塁死＋犠打＋犠飛＋併殺打）×27
        float a = hits + walks; // TODO 盗塁死を併殺打を引く
        float b = (float)((singles+doubles*2+triples*3+homeruns*4) + walks*0.26 + sacrifices*0.53 + steal*0.64 - strikeouts*0.03);
        float c = atbats + walks + sacrifices;
        float rc = (float)((a+2.4*c) * (b+3*c) / (9*c) - 0.9*c);
        rc27 = rc / (float)(atbats - hits + sacrifices) * 27; // TODO 盗塁死を併殺打を加える
    }

}
