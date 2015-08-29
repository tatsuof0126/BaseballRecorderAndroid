package com.tatsuo.baseballrecorder.domain;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.games.Game;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tatsuo on 2015/08/12.
 */
public class GameResultManager {

    public static void saveGameResult(Activity activity, GameResult gameResult) {
        if(gameResult.getResultId() == GameResult.NON_REGISTED){
            gameResult.setResultId(getNewResultId(activity));
        }

        try {
            String fileName = "gameresult"+gameResult.getResultId()+".dat";
            String writeString = gameResult.getGameResultString();

android.util.Log.i(fileName, writeString);

            FileOutputStream fileOutputStream = activity.openFileOutput(fileName, Activity.MODE_PRIVATE);
            fileOutputStream.write(writeString.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
        }
    }

    public static List loadGameResultList(Activity activity){
        String[] fileList = activity.fileList();

        List resultList = new ArrayList();
        for(String fileName : fileList){
            resultList.add(loadGameResult(activity, fileName));
        }

        Collections.sort(resultList, new GameResultComparator());

        return resultList;
    }

    public static List loadGameResultList(Activity activity, StatRange statRange, boolean pitching){
        List<GameResult> orgList = loadGameResultList(activity);
        List<GameResult> teamSelectedList = new ArrayList();
        List<GameResult> returnList = new ArrayList();

        // チーム名で選別
        if("".equals(statRange.getTeam())) {
            teamSelectedList = orgList;
        } else {
            for (GameResult gameResult : orgList) {
                if(gameResult.getMyteam().equals(statRange.getTeam())){
                    teamSelectedList.add(gameResult);
                }
            }
        }

        // 期間で選別
        switch (statRange.getType()){
            case StatRange.TYPE_ALL:
                returnList = teamSelectedList;
                break;
            case StatRange.TYPE_YEAR:
                for(GameResult gameResult : teamSelectedList){
                    if(gameResult.getYear() == statRange.getYear()){
                        returnList.add(gameResult);
                    }
                }
                break;
            case StatRange.TYPE_MONTH:
                for(GameResult gameResult : teamSelectedList){
                    if(gameResult.getYear() == statRange.getYear()
                            && gameResult.getMonth() == statRange.getMonth()){
                        returnList.add(gameResult);
                    }
                }
                break;
            case StatRange.TYPE_RECENT5:
                for(int i=0;i < 5 && i < teamSelectedList.size();i++){
                    GameResult gameResult = (GameResult)teamSelectedList.get(i);
                    if(pitching == true){
                        // TODO 投手成績があるかどうかの判定
                        returnList.add(gameResult);
                    } else {
                        returnList.add(gameResult);
                    }
                }
                break;
        }

        return returnList;
    }

    public static GameResult loadGameResult(Activity activity, int resultId){
        return loadGameResult(activity, "gameresult"+resultId+".dat");
    }

    public static GameResult loadGameResult(Activity activity, String fileName){
        GameResult gameResult = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(activity.openFileInput(fileName)));

            StringBuilder stringBuilder = new StringBuilder();
            String string = "";
            while ((string = reader.readLine()) != null) {
                stringBuilder.append(string);
                stringBuilder.append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1); // 最終行の改行を削除
            reader.close();

android.util.Log.i(fileName, stringBuilder.toString());

            gameResult = GameResult.makeGameResult(stringBuilder.toString());
        } catch (IOException e) {
        }

        return gameResult;
    }

    public static void removeGameResult(Activity activity, int resultId){
        activity.deleteFile("gameresult"+resultId+".dat");
    }

    public static List<StatRange> getStatRangeListForTimePicker(Activity activity){
        List<StatRange> statRangeList = new ArrayList<StatRange>();
        statRangeList.add(new StatRange(StatRange.TYPE_ALL));
        statRangeList.add(new StatRange(StatRange.TYPE_RECENT5));

        List<GameResult> gameResultList = loadGameResultList(activity);
        int year = -999;
        for(GameResult gameResult : gameResultList){
            if(gameResult.getYear() != year){
                StatRange statRange = new StatRange(StatRange.TYPE_YEAR);
                statRange.setYear(gameResult.getYear());
                statRangeList.add(statRange);
                year = gameResult.getYear();
            }
        }

        year = -999;
        int month = -999;
        for(GameResult gameResult : gameResultList){
            if(gameResult.getYear() != year || gameResult.getMonth() != month){
                StatRange statRange = new StatRange(StatRange.TYPE_MONTH);
                statRange.setYear(gameResult.getYear());
                statRange.setMonth(gameResult.getMonth());
                statRangeList.add(statRange);
                year = gameResult.getYear();
                month = gameResult.getMonth();
            }
        }

        return statRangeList;
    }

    public static List<String> getMyTeamList(Activity activity){
        List<String> teamList = new ArrayList<String>();

        List<GameResult> gameResultList = loadGameResultList(activity);
        for(GameResult gameResult : gameResultList) {
            String teamStr = gameResult.getMyteam();
            if(teamList.contains(teamStr) == false){
                teamList.add(teamStr);
            }
        }

        return teamList;
    }

    private static int getNewResultId(Activity activity){
        String[] fileList = activity.fileList();

        int id = 0;
        while(true){
            String targetFileName = "gameresult"+id+".dat";
            boolean found = false;

            for(String fileName : fileList){
                if(fileName.equals(targetFileName)){
                    found = true;
                    break;
                }
            }

            if(found == false){
                break;
            }
            id++;
        }

        return id;
    }

}
