package com.tatsuo.baseballrecorder.domain;

/**
 * Created by tatsuo on 2015/08/23.
 */
public class StatRange {

    private int type;
    private int year;
    private int month;

    private String team;

    public static final int TYPE_ALL = 1;
    public static final int TYPE_YEAR = 2;
    public static final int TYPE_MONTH = 3;
    public static final int TYPE_RECENT5 = 4;

    public StatRange(int type){
        this.type = type;
        this.team = "";
    }

    public StatRange(String typeStr, String yearStr, String monthStr){
        type = Integer.parseInt(typeStr);

        if(type == TYPE_YEAR){
            year = Integer.parseInt(yearStr);
        } else if(type == TYPE_MONTH) {
            year = Integer.parseInt(yearStr);
            month = Integer.parseInt(monthStr);
        }

        this.team = "";
    }

    public StatRange(String typeStr, String yearStr, String monthStr, String teamStr){
        this(typeStr, yearStr, monthStr);
        this.team = teamStr;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getStatTimeString(){
        switch (type){
            case TYPE_ALL:
                return "すべて";
            case TYPE_YEAR:
                return year+"年";
            case TYPE_MONTH:
                return year+"年"+month+"月";
            case TYPE_RECENT5:
                return "最近５試合";
        }
        return "すべて";
    }

    public String getTeamString(){
        if(team == null || "".equals(team)){
            return "すべて";
        }
        return team;
    }

}
