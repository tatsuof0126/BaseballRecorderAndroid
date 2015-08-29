package com.tatsuo.baseballrecorder.domain;

import com.tatsuo.baseballrecorder.util.Utility;

import java.util.List;

/**
 * Created by tatsuo on 2015/08/21.
 */
public class TeamStatistics {

    private int win;
    private int lose;
    private int draw;

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

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public static TeamStatistics calculateTeamStatistics(List<GameResult> gameResultList){
        TeamStatistics teamStatistics = new TeamStatistics();

        for(GameResult gameResult : gameResultList) {
            if (gameResult.getMyscore() > gameResult.getOtherscore()) {
                teamStatistics.win++;
            } else if (gameResult.getMyscore() == gameResult.getOtherscore()){
                teamStatistics.draw++;
            } else {
                teamStatistics.lose++;
            }
        }

        return teamStatistics;
    }

    public String getShoritsuString(){
        float shoritsu = (float)win / (float)(win+lose);
        return Utility.getFloatString3(shoritsu);
    }
}
