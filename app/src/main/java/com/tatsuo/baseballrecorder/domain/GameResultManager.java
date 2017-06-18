package com.tatsuo.baseballrecorder.domain;

import android.app.Activity;

import com.tatsuo.baseballrecorder.aws.S3Manager;

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

            android.util.Log.i(fileName, writeString);

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
            if(fileName != null && fileName.contains("gameresult")){
                selectedFileList.add(fileName);
            }
        }

        for(String fileName : selectedFileList){
            GameResult gameResult = loadGameResult(activity, fileName);
            if(gameResult != null) {
                resultList.add(gameResult);
            }
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
                int count = 0;
                for(int i=0;count < 5 && i < teamSelectedList.size();i++){
                    GameResult gameResult = teamSelectedList.get(i);
                    if(pitching == true){
                        // 投手成績があるかどうかの判定
                        if(gameResult.getInning() != 0 || gameResult.getInning2() != 0) {
                            returnList.add(gameResult);
                            count++;
                        }
                    } else {
                        returnList.add(gameResult);
                        count++;
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

    public static void makeTestData(Activity activity){

        GameResult gameResult = new GameResult();
        gameResult.setResultId(0);
        gameResult.setYear(2016);
        gameResult.setMonth(3);
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
        gameResult.setInning(6);
        gameResult.setInning2(0);
        gameResult.setHianda(5);
        gameResult.setHihomerun(0);
        gameResult.setDassanshin(4);
        gameResult.setYoshikyu(2);
        gameResult.setYoshikyu2(1);
        gameResult.setShitten(4);
        gameResult.setJisekiten(1);
        gameResult.setKanto(true);
        gameResult.setSekinin(1);
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(1);
        gameResult.setYear(2016);
        gameResult.setMonth(3);
        gameResult.setDay(12);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("蒲田オリオンズ");
        gameResult.setMyscore(4);
        gameResult.setOtherscore(4);
        gameResult.setDaten(0);
        gameResult.setTokuten(1);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(6, 1), new BattingResult(0, 14),
                new BattingResult(6, 2), new BattingResult(4, 1)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(2);
        gameResult.setYear(2016);
        gameResult.setMonth(3);
        gameResult.setDay(13);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("練馬ダイコンズ");
        gameResult.setMyscore(3);
        gameResult.setOtherscore(7);
        gameResult.setDaten(1);
        gameResult.setTokuten(0);
        gameResult.setSteal(1);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(5, 6), new BattingResult(7, 7),
                new BattingResult(9, 2), new BattingResult(4, 1)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(3);
        gameResult.setYear(2016);
        gameResult.setMonth(3);
        gameResult.setDay(26);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("品川ファイターズ");
        gameResult.setMyscore(7);
        gameResult.setOtherscore(0);
        gameResult.setDaten(3);
        gameResult.setTokuten(1);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(7, 2), new BattingResult(10, 10),
                new BattingResult(0, 14), new BattingResult(8, 8)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(4);
        gameResult.setYear(2016);
        gameResult.setMonth(4);
        gameResult.setDay(3);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("世田谷ジャイアンツ");
        gameResult.setMyscore(5);
        gameResult.setOtherscore(4);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(7, 7), new BattingResult(6, 1),
                new BattingResult(5, 7), new BattingResult(3, 1)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(5);
        gameResult.setYear(2016);
        gameResult.setMonth(5);
        gameResult.setDay(21);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("練馬ダイコンズ");
        gameResult.setMyscore(2);
        gameResult.setOtherscore(4);
        gameResult.setDaten(1);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(6, 1), new BattingResult(9, 7),
                new BattingResult(3, 2), new BattingResult(0, 15), new BattingResult(8, 2)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(6);
        gameResult.setYear(2016);
        gameResult.setMonth(5);
        gameResult.setDay(5);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("世田谷ジャイアンツ");
        gameResult.setMyscore(5);
        gameResult.setOtherscore(5);
        gameResult.setDaten(1);
        gameResult.setTokuten(1);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(7, 8), new BattingResult(6, 3),
                new BattingResult(4, 1), new BattingResult(6, 7)
        ));
        gameResult.setInning(1);
        gameResult.setInning2(0);
        gameResult.setHianda(2);
        gameResult.setHihomerun(0);
        gameResult.setDassanshin(1);
        gameResult.setYoshikyu(1);
        gameResult.setYoshikyu2(0);
        gameResult.setShitten(1);
        gameResult.setJisekiten(1);
        gameResult.setKanto(false);
        gameResult.setSekinin(0);
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(7);
        gameResult.setYear(2016);
        gameResult.setMonth(5);
        gameResult.setDay(8);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("品川ファイターズ");
        gameResult.setMyscore(5);
        gameResult.setOtherscore(4);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(0, 14), new BattingResult(5, 2),
                new BattingResult(8, 4), new BattingResult(0, 12), new BattingResult(3, 7)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(8);
        gameResult.setYear(2016);
        gameResult.setMonth(4);
        gameResult.setDay(29);
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
                new BattingResult(6, 5), new BattingResult(0, 15), new BattingResult(11, 8)
        ));
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(9);
        gameResult.setYear(2016);
        gameResult.setMonth(5);
        gameResult.setDay(15);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("蒲田オリオンズ");
        gameResult.setMyscore(6);
        gameResult.setOtherscore(3);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(4, 1), new BattingResult(8, 7),
                new BattingResult(0, 12), new BattingResult(8, 2)
        ));
        gameResult.setInning(1);
        gameResult.setInning2(0);
        gameResult.setHianda(1);
        gameResult.setHihomerun(0);
        gameResult.setDassanshin(2);
        gameResult.setYoshikyu(1);
        gameResult.setYoshikyu2(0);
        gameResult.setShitten(1);
        gameResult.setJisekiten(0);
        gameResult.setKanto(false);
        gameResult.setSekinin(0);
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(10);
        gameResult.setYear(2016);
        gameResult.setMonth(4);
        gameResult.setDay(23);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("蒲田オリオンズ");
        gameResult.setMyscore(3);
        gameResult.setOtherscore(2);
        gameResult.setDaten(0);
        gameResult.setTokuten(0);
        gameResult.setSteal(1);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(9, 2), new BattingResult(0, 16),
                new BattingResult(6, 7)
        ));
        gameResult.setInning(7);
        gameResult.setInning2(0);
        gameResult.setHianda(5);
        gameResult.setHihomerun(0);
        gameResult.setDassanshin(3);
        gameResult.setYoshikyu(2);
        gameResult.setYoshikyu2(0);
        gameResult.setShitten(2);
        gameResult.setJisekiten(1);
        gameResult.setKanto(false);
        gameResult.setSekinin(1);
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(11);
        gameResult.setYear(2016);
        gameResult.setMonth(4);
        gameResult.setDay(16);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("品川ファイターズ");
        gameResult.setMyscore(2);
        gameResult.setOtherscore(7);
        gameResult.setDaten(1);
        gameResult.setTokuten(0);
        gameResult.setSteal(0);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(9, 7), new BattingResult(2, 2),
                new BattingResult(6, 1)
        ));
        gameResult.setInning(2);
        gameResult.setInning2(0);
        gameResult.setHianda(1);
        gameResult.setHihomerun(0);
        gameResult.setDassanshin(1);
        gameResult.setYoshikyu(1);
        gameResult.setYoshikyu2(0);
        gameResult.setShitten(2);
        gameResult.setJisekiten(2);
        gameResult.setKanto(false);
        gameResult.setSekinin(2);
        saveGameResult(activity, gameResult);

        gameResult = new GameResult();
        gameResult.setResultId(12);
        gameResult.setYear(2016);
        gameResult.setMonth(5);
        gameResult.setDay(22);
        gameResult.setPlace("光が丘公園");
        gameResult.setMyteam("杉並タイガース");
        gameResult.setOtherteam("世田谷ジャイアンツ");
        gameResult.setMyscore(12);
        gameResult.setOtherscore(3);
        gameResult.setDaten(3);
        gameResult.setTokuten(2);
        gameResult.setSteal(1);
        gameResult.setBattingResultList(Arrays.asList(
                new BattingResult(11, 9), new BattingResult(6, 1),
                new BattingResult(0, 12), new BattingResult(8, 7)
        ));
        gameResult.setInning(4);
        gameResult.setInning2(2);
        gameResult.setHianda(4);
        gameResult.setHihomerun(0);
        gameResult.setDassanshin(4);
        gameResult.setYoshikyu(2);
        gameResult.setYoshikyu2(0);
        gameResult.setShitten(2);
        gameResult.setJisekiten(1);
        gameResult.setKanto(false);
        gameResult.setSekinin(1);
        saveGameResult(activity, gameResult);

/*
        2015,3,27,光が丘公園,杉並タイガース,世田谷ジャイアンツ,12,3,3,2,1\n\n11,9,6,1,4,7,0,12,8,7\n4,2,4,0,4,2,0,2,1,0,1\n",
        2015,3,10,光が丘公園,杉並タイガース,品川ファイターズ,2,7,1,0,0\n\n9,7,2,2,6,1\n2,0,1,0,1,1,0,2,2,0,2\n",
        2015,3,23,光が丘公園,杉並タイガース,蒲田オリオンズ,3,2,0,0,1\n\n9,2,0,16,6,7\n7,0,5,0,3,2,0,2,1,0,1\n",
        2015,3,25,光が丘公園,杉並タイガース,蒲田オリオンズ,4,6,0,0,0\n\n4,1,8,7,0,12,8,2\n1,0,1,0,2,1,0,1,0,0,0\n",
        2015,6,21,光が丘公園,杉並タイガース,練馬ダイコンズ,6,3,0,0,0\n\n1,11,9,2,6,5,0,15,11,8\n0,0,0,0,0,0,0,0,0,0,0\n",
        2015,11,10,光が丘公園,杉並タイガース,品川ファイターズ,5,4,0,0,0\n\n0,14,5,2,8,4,0,12,3,7\n0,0,0,0,0,0,0,0,0,0,0\n",
        2015,5,5,光が丘公園,杉並タイガース,世田谷ジャイアンツ,5,5,1,1,0\n\n7,8,6,3,4,1,6,7\n1,0,2,0,1,1,0,1,1,0,0\n",
        2015,10,19,光が丘公園,杉並タイガース,練馬ダイコンズ,2,4,1,0,0\n\n6,1,9,7,3,2,0,15,8,2\n0,0,0,0,0,0,0,0,0,0,0\n",
        2015,2,3,光が丘公園,杉並タイガース,世田谷ジャイアンツ,5,4,0,0,0\n\n7,7,6,1,5,7,3,1\n1,0,1,0,0,0,0,0,0,0,4\n",
        2015,3,15,光が丘公園,杉並タイガース,品川ファイターズ,7,0,3,1,0\n\n7,2,10,10,0,14,8,8\n0,0,0,0,0,0,0,0,0,0,0\n",
        2015,3,15,光が丘公園,杉並タイガース,練馬ダイコンズ,3,7,1,0,1\n\n5,6,7,7,9,2,4,1\n0,0,0,0,0,0,0,0,0,0,0\n",
        2015,3,8,光が丘公園,杉並タイガース,練馬ダイコンズ,6,4,1,0,1\n\n6,2,7,2,8,7,0,13\n6,0,5,0,4,2,1,4,1,1,1\n",
        2015,3,1,光が丘公園,杉並タイガース,蒲田オリオンズ,4,4,0,1,0\n\n6,1,0,14,6,2,4,1\n0,0,0,0,0,0,0,0,0,0,0\n",
*/
/*
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
        gameResult.setInning(5);
        gameResult.setInning2(2);
        gameResult.setHianda(10);
        gameResult.setHihomerun(2);
        gameResult.setDassanshin(8);
        gameResult.setYoshikyu(3);
        gameResult.setYoshikyu2(1);
        gameResult.setShitten(5);
        gameResult.setJisekiten(4);
        gameResult.setKanto(false);
        gameResult.setTamakazu(123);
        gameResult.setSekinin(2);
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
        gameResult.setInning(0);
        gameResult.setInning2(3);
        gameResult.setHianda(3);
        gameResult.setHihomerun(0);
        gameResult.setDassanshin(2);
        gameResult.setYoshikyu(1);
        gameResult.setYoshikyu2(0);
        gameResult.setShitten(2);
        gameResult.setJisekiten(1);
        gameResult.setKanto(false);
        gameResult.setTamakazu(15);
        gameResult.setSekinin(3);
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
        gameResult.setInning(9);
        gameResult.setInning2(0);
        gameResult.setHianda(6);
        gameResult.setHihomerun(1);
        gameResult.setDassanshin(10);
        gameResult.setYoshikyu(5);
        gameResult.setYoshikyu2(3);
        gameResult.setShitten(4);
        gameResult.setJisekiten(2);
        gameResult.setKanto(true);
        gameResult.setTamakazu(-999);
        gameResult.setSekinin(1);
        saveGameResult(activity, gameResult);

        */
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

    public static String getMigrationCd(){
        int migrationCdInt = 0;
        while(true){
            migrationCdInt = (int) (Math.random() * 90000000) + 10000000;

            // Androidで発行するIDは3で割ると余りが1であることにする
            if(migrationCdInt % 3 != 1){
                continue;
            }

            List<String> filelist = S3Manager.S3GetFileList(migrationCdInt+"/");

            // nullが返ってきた場合は通信失敗
            if(filelist == null){
                return null;
            }

            // 空のリストが返ってきた場合はそのIDは使える
            if(filelist.size() == 0){
                break;
            }
        }

        return ""+migrationCdInt;
    }

    // MigrationPasswordの生成方法
    // １桁目：１桁目〜４桁目を７倍したときの先頭１桁
    // ２桁目：１桁目〜４桁目を７倍したときの末尾１桁
    // ３桁目：１桁目〜４桁目を７倍したときの末尾２桁
    // ４桁目：５桁目〜８桁目を３倍したときの末尾１桁
    // ５桁目：５桁目〜８桁目を３倍したときの末尾２桁
    // ６桁目：５桁目〜８桁目を３倍したときの末尾３桁
    public static String getMigrationPassword(String migrationCd){
        if(migrationCd == null || migrationCd.length() < 8){
            return null;
        }

        String id1234 = migrationCd.substring(0, 4);
        String id5678 = migrationCd.substring(4, 8);

        String id1234By7 = ""+Integer.parseInt(id1234)*7;
        String id5678By3 = ""+Integer.parseInt(id5678)*3;

        String password1 = id1234By7.substring(0, 1);
        String password2 = id1234By7.substring(id1234By7.length()-1, id1234By7.length());
        String password3 = "0";
        if(id1234By7.length() >= 2) {
            password3 = id1234By7.substring(id1234By7.length()-2, id1234By7.length()-1);
        }
        String password4 = id5678By3.substring(id5678By3.length()-1, id5678By3.length());
        String password5 = "0";
        if(id5678By3.length() >= 2) {
            password5 = id5678By3.substring(id5678By3.length()-2, id5678By3.length()-1);
        }
        String password6 = "0";
        if(id5678By3.length() >= 3) {
            password6 = id5678By3.substring(id5678By3.length()-3, id5678By3.length()-2);
        }

        // Log.e("MIGRATIONCD", migrationCd);
        // Log.e("id1234By7", id1234By7);
        // Log.e("id5678By3", id5678By3);
        // Log.e("PASSWORD", password1 + password2 + password3 + password4 + password5 + password6);

        return password1 + password2 + password3 + password4 + password5 + password6;
    }

}
