package com.tatsuo.baseballrecorder.domain;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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

            // android.util.Log.i(fileName, writeString);

            FileOutputStream fileOutputStream = activity.openFileOutput(fileName, Activity.MODE_PRIVATE);
            fileOutputStream.write(writeString.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
        }
    }

    public static List loadGameResultList(Activity activity){
        List resultList = new ArrayList();

        String[] fileList = activity.fileList();
        List<String> selectedFileList = new ArrayList<String>();

        // 試合結果ファイルに絞る
        for(String fileName : fileList) {
            if(fileName.contains("gameresult")){
                selectedFileList.add(fileName);
            }
        }

        for(String fileName : selectedFileList){
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

            // android.util.Log.i(fileName, stringBuilder.toString());

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

    public static void makeTestData(Activity activity){
        GameResult gameResult = new GameResult();
        gameResult.setResultId(0);
        gameResult.setYear(2015);
        gameResult.setMonth(8);
        gameResult.setDay(30);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("蒲田オリオンズ");
        gameResult.setMyscore(5);
        gameResult.setOtherscore(2);
        gameResult.setDaten(1);
        gameResult.setTokuten(2);
        gameResult.setSteal(1);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(6, 1), new BattingResult(7, 7),
                new BattingResult(9, 8), new BattingResult(0, 12),
                new BattingResult(4, 4)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(1);
        gameResult.setYear(2015);
        gameResult.setMonth(8);
        gameResult.setDay(5);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("練馬ダイコンズ");
        gameResult.setMyscore(6);
        gameResult.setOtherscore(3);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(1, 11), new BattingResult(9, 2),
                new BattingResult(6, 5), new BattingResult(0, 15),
                new BattingResult(11, 8)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(2);
        gameResult.setYear(2015);
        gameResult.setMonth(7);
        gameResult.setDay(25);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("蒲田オリオンズ");
        gameResult.setMyscore(4);
        gameResult.setOtherscore(6);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(4, 1), new BattingResult(8, 7),
                new BattingResult(0, 12), new BattingResult(8, 2)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(3);
        gameResult.setYear(2015);
        gameResult.setMonth(7);
        gameResult.setDay(10);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("品川ファイターズ");
        gameResult.setMyscore(7);
        gameResult.setOtherscore(0);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(7, 2), new BattingResult(10, 10),
                new BattingResult(0, 14), new BattingResult(8, 8)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(4);
        gameResult.setYear(2015);
        gameResult.setMonth(6);
        gameResult.setDay(15);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("世田谷ジャイアンツ");
        gameResult.setMyscore(5);
        gameResult.setOtherscore(5);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(7, 8), new BattingResult(6, 3),
                new BattingResult(4, 1), new BattingResult(6, 7)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(5);
        gameResult.setYear(2015);
        gameResult.setMonth(5);
        gameResult.setDay(22);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("練馬ダイコンズ");
        gameResult.setMyscore(3);
        gameResult.setOtherscore(7);
        gameResult.setDaten(3);
        gameResult.setTokuten(1);
        gameResult.setSteal(1);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(5, 6), new BattingResult(7, 7),
                new BattingResult(9, 2), new BattingResult(4, 1)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(6);
        gameResult.setYear(2015);
        gameResult.setMonth(5);
        gameResult.setDay(5);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("練馬ダイコンズ");
        gameResult.setMyscore(6);
        gameResult.setOtherscore(4);
        gameResult.setDaten(1);
        gameResult.setTokuten(0);
        gameResult.setSteal(1);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(6, 2), new BattingResult(7, 2),
                new BattingResult(8, 7), new BattingResult(0, 13)
        ));
        saveGameResult(activity, gameResult);
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
