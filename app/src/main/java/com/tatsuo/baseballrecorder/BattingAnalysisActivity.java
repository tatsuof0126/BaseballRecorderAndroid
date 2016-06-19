package com.tatsuo.baseballrecorder;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.domain.StatRange;
import com.tatsuo.baseballrecorder.util.Utility;
import com.tatsuo.baseballrecorder.view.AnalysisView;

import java.io.File;
import java.util.List;

public class BattingAnalysisActivity extends CommonStatisticsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batting_analysis);

        /*
        Button changeButton = (Button)findViewById(R.id.change_button);
        changeButton.setOnClickListener(this);

        Button shareButton = (Button)findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);
        */

        ((Button)findViewById(R.id.change_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.share_button)).setOnClickListener(this);

        makeStatView();

        makeAdsView();
    }

    @Override
    protected void makeStatView(){
        StatRange statRange = ConfigManager.loadStatRange();

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
        File file = new File(Environment.getExternalStorageDirectory() + "/capture.png");
        file.getParentFile().mkdir();

        // ボタンを一時的に消す
        Button changeButton = (Button)findViewById(R.id.change_button);
        Button shareButton = (Button)findViewById(R.id.share_button);
        changeButton.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);

        // 「草野球日記 ベボレコ」という文言を一時的に出す
        TextView signatureText = (TextView)findViewById(R.id.signature_text);
        signatureText.setVisibility(View.VISIBLE);

        // Viewをキャプチャ
        LinearLayout targetView = (LinearLayout)findViewById(R.id.main_layout);
        Utility.saveCapture(targetView, file);

        // 表示を戻す
        changeButton.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);
        signatureText.setVisibility(View.INVISIBLE);

        // Intentで送信
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TEXT, "「草野球日記 ベボレコ」で打撃分析を行いました。 #ベボレコ https://play.google.com/store/apps/details?id=com.tatsuo.baseballrecorder");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

        Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button").setAction("Push").setLabel("打撃分析画面―画像でシェア").build());

    }

}
