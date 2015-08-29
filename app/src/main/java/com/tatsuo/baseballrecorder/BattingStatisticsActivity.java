package com.tatsuo.baseballrecorder;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tatsuo.baseballrecorder.domain.BattingStatistics;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.domain.StatRange;
import com.tatsuo.baseballrecorder.domain.TeamStatistics;
import com.tatsuo.baseballrecorder.util.Utility;

import java.util.List;

public class BattingStatisticsActivity extends CommonStatisticsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batting_statistics);

        Button changeButton = (Button)findViewById(R.id.change_button);
        changeButton.setOnClickListener(this);

        Button shareButton = (Button)findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);

        makeStatView();

        makeAdsView();
    }

    @Override
    protected void adjustViewHeight(){
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        int height = dm.heightPixels;
        float density = dm.density;

        int header = 81; //dp
        int headerpx = (int)(header * density + 0.5f); // 0.5fは四捨五入用

        int ads = isAds ? 50 : 0; //dp
        int adspx = (int)(ads * density + 0.5f);

        int viewHeight = height - (headerpx + adspx);

        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, viewHeight));
    }

    @Override
    protected void makeStatView(){
        StatRange statRange = ConfigManager.loadStatRange(this);

        TextView timeText = (TextView)findViewById(R.id.time);
        timeText.setText(statRange.getStatTimeString());

        TextView teamText = (TextView)findViewById(R.id.team);
        teamText.setText(statRange.getTeamString());

        List<GameResult> gameResultList = GameResultManager.loadGameResultList(this, statRange, false);

        TeamStatistics teamStatistics = TeamStatistics.calculateTeamStatistics(gameResultList);
        BattingStatistics battingStatistics = BattingStatistics.calculateBattingStatistics(gameResultList);

        TextView gameText = (TextView)findViewById(R.id.game);
        gameText.setText(teamStatistics.getWin()+"勝 "+teamStatistics.getLose()+"敗 "
                +teamStatistics.getDraw()+"分  勝率 "+teamStatistics.getShoritsuString());

        TextView battingStatsText = (TextView)findViewById(R.id.batting);
        battingStatsText.setText(battingStatistics.getBoxs()+"打席 "+battingStatistics.getAtbats()+"打数 "+
                battingStatistics.getHits()+"安打");

        TextView doublesText = (TextView)findViewById(R.id.doubles);
        doublesText.setText(""+battingStatistics.getDoubles());

        TextView triplesText = (TextView)findViewById(R.id.triples);
        triplesText.setText(""+battingStatistics.getTriples());

        TextView homerunsText = (TextView)findViewById(R.id.homeruns);
        homerunsText.setText(""+battingStatistics.getHomeruns());

        TextView strikeoutsText = (TextView)findViewById(R.id.strikeouts);
        strikeoutsText.setText(""+battingStatistics.getStrikeouts());

        TextView walksText = (TextView)findViewById(R.id.walks);
        walksText.setText(""+battingStatistics.getWalks());

        TextView sacrificesText = (TextView)findViewById(R.id.sacrifices);
        sacrificesText.setText(""+battingStatistics.getSacrifices());

        TextView datenText = (TextView)findViewById(R.id.daten);
        datenText.setText(""+battingStatistics.getDaten());

        TextView tokutenText = (TextView)findViewById(R.id.tokuten);
        tokutenText.setText(""+battingStatistics.getTokuten());

        TextView stealText = (TextView)findViewById(R.id.steal);
        stealText.setText(""+battingStatistics.getSteal());

        TextView stats1Text = (TextView)findViewById(R.id.stats1);
        stats1Text.setText("打率 "+Utility.getFloatString3(battingStatistics.getAverage())
            +"  出塁率 "+Utility.getFloatString3(battingStatistics.getObp())
                +"  長打率 "+Utility.getFloatString3(battingStatistics.getSlg()));

        TextView stats2Text = (TextView)findViewById(R.id.stats2);
        stats2Text.setText("OPS " + Utility.getFloatString3(battingStatistics.getOps()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_batting_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.change_button:
                changeButton();
                break;
            case R.id.share_button:
                shareButton();
                break;
        }
    }

    private void changeButton() {
        timePicker();
    }

    private void shareButton(){


    }

}
