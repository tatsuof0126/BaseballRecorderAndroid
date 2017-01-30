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
import com.tatsuo.baseballrecorder.domain.BattingStatistics;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.domain.StatRange;
import com.tatsuo.baseballrecorder.domain.TeamStatistics;
import com.tatsuo.baseballrecorder.util.Utility;

import java.io.File;
import java.util.List;

public class BattingStatisticsActivity extends CommonStatisticsActivity implements View.OnClickListener {

    private TeamStatistics teamStatistics = null;
    private BattingStatistics battingStatistics = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batting_statistics);

        ((Button)findViewById(R.id.change_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.share_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.image_share_button)).setOnClickListener(this);

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
        StatRange statRange = ConfigManager.loadStatRange();

        TextView timeText = (TextView)findViewById(R.id.time);
        timeText.setText(statRange.getStatTimeString());

        TextView teamText = (TextView)findViewById(R.id.team);
        teamText.setText(statRange.getTeamString());

        List<GameResult> gameResultList = GameResultManager.loadGameResultList(this, statRange, false);

        teamStatistics = TeamStatistics.calculateTeamStatistics(gameResultList);
        battingStatistics = BattingStatistics.calculateBattingStatistics(gameResultList);

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
        sacrificesText.setText(""+(battingStatistics.getSacrifices() - battingStatistics.getSacrificeflies()));

        TextView datenText = (TextView)findViewById(R.id.daten);
        datenText.setText(""+battingStatistics.getDaten());

        TextView tokutenText = (TextView)findViewById(R.id.tokuten);
        tokutenText.setText(""+battingStatistics.getTokuten());

        TextView sacrificeFlyText = (TextView)findViewById(R.id.sacrificefly);
        sacrificeFlyText.setText(""+battingStatistics.getSacrificeflies());

        TextView stealText = (TextView)findViewById(R.id.steal);
        stealText.setText(""+battingStatistics.getSteal());

        TextView stealOutText = (TextView)findViewById(R.id.stealout);
        stealOutText.setText(""+battingStatistics.getStealOut());

        TextView errorText = (TextView)findViewById(R.id.error);
        errorText.setText(""+battingStatistics.getError());

        TextView stats1Text = (TextView)findViewById(R.id.stats1);
        stats1Text.setText("打率 "+Utility.getFloatString3(battingStatistics.getAverage())
                +"  出塁率 "+Utility.getFloatString3(battingStatistics.getObp())
                +"  長打率 "+Utility.getFloatString3(battingStatistics.getSlg()));

        TextView stats2Text = (TextView)findViewById(R.id.stats2);
        stats2Text.setText("OPS "+Utility.getFloatString3(battingStatistics.getOps())
                +"   IsoD "+Utility.getFloatString3(battingStatistics.getIsod())
                +"   IsoP "+Utility.getFloatString3(battingStatistics.getIsop()));

        TextView stats3Text = (TextView)findViewById(R.id.stats3);
        stats3Text.setText("盗塁成功率 " + Utility.getFloatString3(battingStatistics.getStealrate())
                +"  RC27 " + Utility.getFloatString2(battingStatistics.getRc27()));

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

        shareString.append(battingStatistics.getBoxs() + "打席" +
                battingStatistics.getAtbats() + "打数" + battingStatistics.getHits() + "安打" +
                " 打率" + Utility.getFloatString3(battingStatistics.getAverage()) +
                " 出塁率" + Utility.getFloatString3(battingStatistics.getObp()) +
                " 長打率" + Utility.getFloatString3(battingStatistics.getSlg()) +
                " OPS" + Utility.getFloatString3(battingStatistics.getOps())+" ");

        if(battingStatistics.getDoubles() > 0) {
            shareString.append("二塁打"+battingStatistics.getDoubles()+" ");
        }

        if(battingStatistics.getTriples() > 0) {
            shareString.append("三塁打"+battingStatistics.getTriples()+" ");
        }

        if(battingStatistics.getHomeruns() > 0) {
            shareString.append("本塁打"+battingStatistics.getHomeruns()+" ");
        }

        if(battingStatistics.getStrikeouts() > 0) {
            shareString.append("三振"+battingStatistics.getStrikeouts()+" ");
        }

        if(battingStatistics.getWalks() > 0) {
            shareString.append("四死球"+battingStatistics.getWalks()+" ");
        }

        if(battingStatistics.getSacrifices() > 0) {
            shareString.append("犠打"+battingStatistics.getSacrifices()+" ");
        }

        if(battingStatistics.getDaten() > 0) {
            shareString.append("打点"+battingStatistics.getDaten()+" ");
        }

        if(battingStatistics.getTokuten() > 0) {
            shareString.append("得点"+battingStatistics.getTokuten()+" ");
        }

        if(battingStatistics.getSteal() > 0) {
            shareString.append("盗塁"+battingStatistics.getSteal()+" ");
        }
        shareString.append("です。 #ベボレコ");

        // Intentで送信
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareString.toString());
        startActivity(intent);

        Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button").setAction("Push").setLabel("打撃成績画面―シェア").build());

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

        shareString.append(battingStatistics.getBoxs() + "打席" +
                battingStatistics.getAtbats() + "打数" + battingStatistics.getHits() + "安打" +
                " 打率" + Utility.getFloatString3(battingStatistics.getAverage()) +
                " 出塁率" + Utility.getFloatString3(battingStatistics.getObp()) +
                " 長打率" + Utility.getFloatString3(battingStatistics.getSlg()) +
                " OPS" + Utility.getFloatString3(battingStatistics.getOps()) + " ");

        shareString.append(" です。 #ベボレコ https://play.google.com/store/apps/details?id=com.tatsuo.baseballrecorder");

        // Intentで送信
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TEXT, shareString.toString());
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

        Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button").setAction("Push").setLabel("打撃成績画面―画像でシェア").build());

    }

    private void addStatTitle(StringBuilder stringBuilder){
        StatRange statRange = ConfigManager.loadStatRange();

        switch (statRange.getType()){
            case StatRange.TYPE_ALL:
                if("".equals(statRange.getTeam()) == false){
                    stringBuilder.append(statRange.getTeam()+"での");
                }
                stringBuilder.append("通算打撃成績は、");
                break;
            case StatRange.TYPE_YEAR:
            case StatRange.TYPE_MONTH:
            case StatRange.TYPE_RECENT5:
                stringBuilder.append(statRange.getStatTimeString()+"の");
                if("".equals(statRange.getTeam()) == false){
                    stringBuilder.append(statRange.getTeam()+"での");
                }
                stringBuilder.append("打撃成績は、");
                break;
        }
    }

}
