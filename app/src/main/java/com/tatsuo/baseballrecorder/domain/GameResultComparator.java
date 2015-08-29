package com.tatsuo.baseballrecorder.domain;

import java.util.Comparator;

/**
 * Created by tatsuo on 2015/08/15.
 */
public class GameResultComparator implements Comparator<GameResult> {

    public int compare(GameResult a, GameResult b) {
        if(a.getYear() > b.getYear()){
            return -1;
        } else if(a.getYear() < b.getYear()){
            return 1;
        }

        if(a.getMonth() > b.getMonth()){
            return -1;
        } else if(a.getMonth() < b.getMonth()){
            return 1;
        }

        if(a.getDay() > b.getDay()){
            return -1;
        } else if(a.getDay() < b.getDay()){
            return 1;
        }

        if(a.getResultId() > b.getResultId()){
            return -1;
        } else {
            return 1;
        }
    }
}
