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
import com.tatsuo.baseballrecorder.domain.PitchingStatistics;
import com.tatsuo.baseballrecorder.domain.StatRange;
import com.tatsuo.baseballrecorder.util.Utility;

import java.io.File;
import java.util.List;

/**
 * Created by tatsuo on 2016/05/08.
 */
public class PitchingStatisticsActivity extends CommonStatisticsActivity implements View.OnClickListener {

    private PitchingStatistics pitchingStatistics = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitching_statistics);

        ((Button)findViewById(R.id.change_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.share_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.image_share_button)).setOnClickListener(this);

        makeStatView();

        makeAdsView();
    }

    @Override
    protected void adjustViewHeight() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        int height = dm.heightPixels;
        float density = dm.density;

        int header = 81; //dp
        int headerpx = (int) (header * density + 0.5f); // 0.5fは四捨五入用

        int ads = isAds ? 50 : 0; //dp
        int adspx = (int) (ads * density + 0.5f);

        int viewHeight = height - (headerpx + adspx);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, viewHeight));
    }

    @Override
    protected void makeStatView() {
        StatRange statRange = ConfigManager.loadStatRange(this);

        TextView timeText = (TextView)findViewById(R.id.time);
        timeText.setText(statRange.getStatTimeString());

        TextView teamText = (TextView)findViewById(R.id.team);
        teamText.setText(statRange.getTeamString());

        List<GameResult> gameResultList = GameResultManager.loadGameResultList(this, statRange, true);
        pitchingStatistics = PitchingStatistics.calculatePitchingStatistics(gameResultList);

        TextView pitchingResultText = (TextView)findViewById(R.id.pitching_result);
        pitchingResultText.setText(pitchingStatistics.getGames()+"試合 "
                +pitchingStatistics.getWin()+"勝 "+pitchingStatistics.getLose()+"敗 "
                +pitchingStatistics.getSave()+"セーブ "+pitchingStatistics.getHold()+"ホールド");

        TextView eraText = (TextView)findViewById(R.id.era);
        eraText.setText(Utility.getFloatString2(pitchingStatistics.getEra()));

        TextView shoritsuext = (TextView)findViewById(R.id.shoritsu);
        shoritsuext.setText(Utility.getFloatString3(pitchingStatistics.getShoritsu()));

        TextView inningStrText = (TextView)findViewById(R.id.inningstr);
        inningStrText.setText(pitchingStatistics.getInningString());

        TextView hiandaText = (TextView)findViewById(R.id.hianda);
        hiandaText.setText(String.valueOf(pitchingStatistics.getHianda()));

        TextView hihomerunText = (TextView)findViewById(R.id.hihomerun);
        hihomerunText.setText(String.valueOf(pitchingStatistics.getHihomerun()));

        TextView dassanshinText = (TextView)findViewById(R.id.dassanshin);
        dassanshinText.setText(String.valueOf(pitchingStatistics.getDassanshin()));

        TextView yoshikyuText = (TextView)findViewById(R.id.yoshikyu);
        yoshikyuText.setText(String.valueOf(pitchingStatistics.getYoshikyu()));

        TextView yoshikyu2Text = (TextView)findViewById(R.id.yoshikyu2);
        yoshikyu2Text.setText(String.valueOf(pitchingStatistics.getYoshikyu2()));

        TextView shittenText = (TextView)findViewById(R.id.shitten);
        shittenText.setText(String.valueOf(pitchingStatistics.getShitten()));

        TextView jisekitenText = (TextView)findViewById(R.id.jisekiten);
        jisekitenText.setText(String.valueOf(pitchingStatistics.getJisekiten()));

        TextView kantoText = (TextView)findViewById(R.id.kanto);
        kantoText.setText(String.valueOf(pitchingStatistics.getKanto()));

        TextView whipText = (TextView)findViewById(R.id.whip);
        whipText.setText(Utility.getFloatString2(pitchingStatistics.getWhip()));

        TextView k9Text = (TextView)findViewById(R.id.k9);
        k9Text.setText(Utility.getFloatString2(pitchingStatistics.getK9()));

        TextView kbbText = (TextView)findViewById(R.id.kbb);
        kbbText.setText(Utility.getFloatString2(pitchingStatistics.getKbb()));

        TextView fipText = (TextView)findViewById(R.id.fip);
        fipText.setText(Utility.getFloatString2(pitchingStatistics.getFip()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pitching_statistics, menu);
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
            case R.id.image_share_button:
                imageShareButton();
                break;
        }
    }

    private void changeButton() {
        timePicker();
    }

    private void shareButton(){
        StringBuilder shareString = new StringBuilder();

        addStatTitle(shareString);

        shareString.append(pitchingStatistics.getGames() + "試合"
                + pitchingStatistics.getWin() + "勝" + pitchingStatistics.getLose() + "敗"
                + pitchingStatistics.getSave() + "S" + pitchingStatistics.getHold() + "H");

        shareString.append(" 防御率" + Utility.getFloatString2(pitchingStatistics.getEra())
                + " 勝率" + Utility.getFloatString3(pitchingStatistics.getShoritsu()));

        shareString.append(" 投球回"+pitchingStatistics.getInningString());

        if(pitchingStatistics.getHianda() != 0) {
            shareString.append(" 被安打" + pitchingStatistics.getHianda());
        }
        if(pitchingStatistics.getHihomerun() != 0){
            shareString.append(" 被本塁打"+pitchingStatistics.getHihomerun());
        }
        if(pitchingStatistics.getDassanshin() != 0) {
            shareString.append(" 奪三振" + pitchingStatistics.getDassanshin());
        }
        if(pitchingStatistics.getYoshikyu() != 0) {
            shareString.append(" 与四球" + pitchingStatistics.getYoshikyu());
        }
        if(pitchingStatistics.getYoshikyu2() != 0) {
            shareString.append(" 与死球" + pitchingStatistics.getYoshikyu2());
        }

        shareString.append(" 失点"+pitchingStatistics.getShitten());
        shareString.append(" 自責点"+pitchingStatistics.getJisekiten());
        shareString.append(" 完投"+pitchingStatistics.getKanto());
        shareString.append(" WHIP"+Utility.getFloatString2(pitchingStatistics.getWhip()));
        shareString.append(" 奪三振率"+Utility.getFloatString2(pitchingStatistics.getK9()));
        shareString.append(" K/BB"+Utility.getFloatString2(pitchingStatistics.getKbb()));
        shareString.append(" FIP"+Utility.getFloatString2(pitchingStatistics.getFip()));

        shareString.append(" です。 #ベボレコ");

        // Intentで送信
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareString.toString());
        startActivity(intent);

        Tracker tracker = ((AnalyticsApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button").setAction("Push").setLabel("投手成績画面―シェア").build());
    }

    private void imageShareButton(){
        File file = new File(Environment.getExternalStorageDirectory() + "/capture.png");
        file.getParentFile().mkdir();

        // ボタンを一時的に消す
        Button changeButton = (Button)findViewById(R.id.change_button);
        Button shareButton = (Button)findViewById(R.id.share_button);
        Button imageShareButton = (Button)findViewById(R.id.image_share_button);
        changeButton.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);
        imageShareButton.setVisibility(View.GONE);

        // 「草野球日記 ベボレコ」という文言を一時的に出す
        TextView signatureText = (TextView)findViewById(R.id.signature_text);
        signatureText.setVisibility(View.VISIBLE);

        // Viewをキャプチャ
        LinearLayout targetView = (LinearLayout)findViewById(R.id.main_layout);
        Utility.saveCapture(targetView, file);

        // 表示を戻す
        changeButton.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);
        imageShareButton.setVisibility(View.VISIBLE);
        signatureText.setVisibility(View.INVISIBLE);

        // シェアする文字列を作成
        StringBuilder shareString = new StringBuilder();

        addStatTitle(shareString);

        shareString.append(pitchingStatistics.getGames() + "試合" + pitchingStatistics.getWin() + "勝"
                + pitchingStatistics.getLose() + "敗" + pitchingStatistics.getSave() + "セーブ"
                + pitchingStatistics.getHold() + "ホールド");

        shareString.append(" 防御率" + Utility.getFloatString2(pitchingStatistics.getEra())
                + " 勝率" + Utility.getFloatString3(pitchingStatistics.getShoritsu()));

        shareString.append(" です。 #ベボレコ https://play.google.com/store/apps/details?id=com.tatsuo.baseballrecorder");

        // Intentで送信
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TEXT, shareString.toString());
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

        Tracker tracker = ((AnalyticsApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button").setAction("Push").setLabel("投手成績画面―画像でシェア").build());

    }

    private void addStatTitle(StringBuilder stringBuilder){
        StatRange statRange = ConfigManager.loadStatRange(this);

        switch (statRange.getType()){
            case StatRange.TYPE_ALL:
                if("".equals(statRange.getTeam()) == false){
                    stringBuilder.append(statRange.getTeam()+"での");
                }
                stringBuilder.append("通算投手成績は、");
                break;
            case StatRange.TYPE_YEAR:
            case StatRange.TYPE_MONTH:
            case StatRange.TYPE_RECENT5:
                stringBuilder.append(statRange.getStatTimeString()+"の");
                if("".equals(statRange.getTeam()) == false){
                    stringBuilder.append(statRange.getTeam()+"での");
                }
                stringBuilder.append("投手成績は、");
                break;
        }
    }

}
