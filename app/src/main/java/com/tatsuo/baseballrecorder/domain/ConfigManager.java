package com.tatsuo.baseballrecorder.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tatsuo.baseballrecorder.BaseballRecorderApplication;

/**
 * Created by tatsuo on 2015/08/23.
 */
public class ConfigManager {

    public static final int VIEW_ALL = 0;
    public static final int VIEW_GAME_RESULT_LIST = 1;
    public static final int VIEW_SHOW_GAME_RESULT = 2;
    public static final int[] VIEW_LIST = {VIEW_GAME_RESULT_LIST, VIEW_SHOW_GAME_RESULT};

    // private static boolean showAds = true;
    public static boolean makeTestData = false;

    public static StatRange loadStatRange() {
        Context context = BaseballRecorderApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String typeStr = sharedPreferences.getString("STAT_TIME_TYPE", Integer.toString(StatRange.TYPE_ALL));
        String yearStr = sharedPreferences.getString("STAT_TIME_YEAR", "");
        String monthStr = sharedPreferences.getString("STAT_TIME_MONTH", "");

        String teamStr = sharedPreferences.getString("STAT_TEAM","");

        StatRange statRange = new StatRange(typeStr, yearStr, monthStr, teamStr);

        return statRange;
    }

    public static void saveStatRange(StatRange statRange){
        Context context = BaseballRecorderApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("STAT_TIME_TYPE",Integer.toString(statRange.getType()));
        editor.putString("STAT_TIME_YEAR",Integer.toString(statRange.getYear()));
        editor.putString("STAT_TIME_MONTH",Integer.toString(statRange.getMonth()));
        editor.putString("STAT_TEAM",statRange.getTeam());

        editor.commit();
    }

    public static boolean loadUpdateGameResultFlg(int view){
        Context context = BaseballRecorderApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("UPDATE_GAME_RESULT_FLG_"+view, false);
    }

    public static void saveUpdateGameResultFlg(int view, boolean updateFlg){
        Context context = BaseballRecorderApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(view == VIEW_ALL){
            for(int i : VIEW_LIST) {
                editor.putBoolean("UPDATE_GAME_RESULT_FLG_" + i, updateFlg);
            }
        } else {
            editor.putBoolean("UPDATE_GAME_RESULT_FLG_" + view, updateFlg);
        }

        editor.commit();
    }

    public static boolean loadCalc7Flg(){
        Context context = BaseballRecorderApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("CALC7_FLG", false);
    }

    public static void saveCalc7Flg(boolean calc7Flg){
        Context context = BaseballRecorderApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("CALC7_FLG", calc7Flg);
        editor.commit();
    }

    public static boolean isShowAds(){
        Context context = BaseballRecorderApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("SHOW_ADS", true);
    }

    public static void saveShowAds(boolean showAds){
        Context context = BaseballRecorderApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("SHOW_ADS", showAds);
        editor.commit();
    }

}
