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

import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.domain.StatRange;
import com.tatsuo.baseballrecorder.view.AnalysisView;

import java.util.List;

public class BattingAnalysisActivity extends CommonStatisticsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batting_analysis);

        Button changeButton = (Button)findViewById(R.id.change_button);
        changeButton.setOnClickListener(this);

        Button shareButton = (Button)findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);

        makeStatView();

        makeAdsView();
    }

    @Override
    protected void makeStatView(){
        StatRange statRange = ConfigManager.loadStatRange(this);

        TextView timeText = (TextView)findViewById(R.id.time);
        timeText.setText(statRange.getStatTimeString());

        TextView teamText = (TextView)findViewById(R.id.team);
        teamText.setText(statRange.getTeamString());

        List<GameResult> gameResultList = GameResultManager.loadGameResultList(this, statRange, false);

        AnalysisView analysisView = (AnalysisView)findViewById(R.id.analysis_view);
        analysisView.setGameResultList(gameResultList);
        analysisView.invalidate();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_batting_analysis, menu);
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

    private void shareButton(){


    }

}
