package com.tatsuo.baseballrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.domain.StatRange;

import java.util.List;

/**
 * Created by tatsuo on 2015/08/26.
 */
public class CommonStatisticsActivity extends CommonAdsActivity {

    protected String[] timeString = {};
    protected String[] teamString = {};
    protected List<StatRange> statRangeList = null;
    protected StatRange selectedStatRange = null;

    protected void makeStatView() {
        // Override前提
    }

    protected void timePicker() {
        statRangeList = GameResultManager.getStatRangeListForTimePicker(this);

        timeString = new String[statRangeList.size()];
        for(int i=0;i<statRangeList.size();i++){
            StatRange statRange = (StatRange)statRangeList.get(i);
            timeString[i] = statRange.getStatTimeString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("期間を選択");
        builder.setItems(timeString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedStatRange = statRangeList.get(i);
                teamPicker();
            }
        });
        builder.show();
    }

    protected void teamPicker() {
        List<String> teamList = GameResultManager.getMyTeamList(this);
        teamList.add(0,"すべて");

        teamString = (String[])teamList.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("チームを選択");
        builder.setItems(teamString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    selectedStatRange.setTeam("");
                } else {
                    selectedStatRange.setTeam(teamString[i]);
                }

                ConfigManager.saveStatRange(selectedStatRange);
                makeStatView();
            }
        });
        builder.show();
    }


}