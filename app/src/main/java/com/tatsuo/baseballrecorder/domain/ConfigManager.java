package com.tatsuo.baseballrecorder.domain;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tatsuo on 2015/08/23.
 */
public class ConfigManager {

    public static final int VIEW_ALL = 0;
    public static final int VIEW_GAME_RESULT_LIST = 1;
    public static final int VIEW_SHOW_GAME_RESULT = 2;
    public static final int[] VIEW_LIST = {VIEW_GAME_RESULT_LIST, VIEW_SHOW_GAME_RESULT};


    public static StatRange loadStatRange(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        String typeStr = sharedPreferences.getString("STAT_TIME_TYPE", Integer.toString(StatRange.TYPE_ALL));
        String yearStr = sharedPreferences.getString("STAT_TIME_YEAR", "");
        String monthStr = sharedPreferences.getString("STAT_TIME_MONTH", "");

        String teamStr = sharedPreferences.getString("STAT_TEAM","");

        StatRange statRange = new StatRange(typeStr, yearStr, monthStr, teamStr);

        return statRange;
    }

    public static void saveStatRange(Activity activity, StatRange statRange){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("STAT_TIME_TYPE",Integer.toString(statRange.getType()));
        editor.putString("STAT_TIME_YEAR",Integer.toString(statRange.getYear()));
        editor.putString("STAT_TIME_MONTH",Integer.toString(statRange.getMonth()));
        editor.putString("STAT_TEAM",statRange.getTeam());

        editor.commit();
    }

    public static boolean loadUpdateGameResultFlg(Activity activity, int view){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getBoolean("UPDATE_GAME_RESULT_FLG_"+view, false);
    }

    public static void saveUpdateGameResultFlg(Activity activity, int view, boolean updateFlg){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
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

}
