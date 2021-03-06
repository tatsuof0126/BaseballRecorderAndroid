package com.tatsuo.baseballrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
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
import com.tatsuo.baseballrecorder.domain.BattingResult;
import com.tatsuo.baseballrecorder.domain.BattingStatistics;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

public class ShowGameResultActivity extends CommonAdsActivity implements View.OnClickListener {

    private static final int NO_TARGET = -999;
    private GameResult targetGameResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_game_result);

        ((Button)findViewById(R.id.share_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.delete_button)).setOnClickListener(this);

        Intent intent = getIntent();
        int resultId = intent.getIntExtra("RESULTID", NO_TARGET);

        if(resultId != NO_TARGET){
            targetGameResult = GameResultManager.loadGameResult(this, resultId);
        } else {
            targetGameResult = new GameResult();
        }

        updateResultView();
        ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_SHOW_GAME_RESULT, false);

        // インタースティシャル広告（動画）を準備
        // prepareVideoAds();
        prepareInterstitial();

        makeAdsView();
    }

    @Override
    protected void onResume(){
        super.onResume();

        // データが更新されていれば表示を更新する
        if(ConfigManager.loadUpdateGameResultFlg(ConfigManager.VIEW_SHOW_GAME_RESULT)) {
            targetGameResult = GameResultManager.loadGameResult(this, targetGameResult.getResultId());
            updateResultView();
            ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_SHOW_GAME_RESULT, false);
        }

        // インタースティシャル広告（動画）を表示
        if(showInterstitial){
            showInterstitial();
        }
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

    private void updateResultView() {
        // 試合成績＆打撃成績
        TextView dateText = (TextView)findViewById(R.id.date_text);
        dateText.setText(targetGameResult.getYear() + "年" + targetGameResult.getMonth() + "月"
                + targetGameResult.getDay() + "日");

        TextView scoreText = (TextView)findViewById(R.id.score_text);
        scoreText.setText(targetGameResult.getMyteam()+" "+targetGameResult.getMyscore()+ " - "
                +targetGameResult.getOtherscore()+" "+targetGameResult.getOtherteam());

        // 場所
        TextView placeText = (TextView)findViewById(R.id.place_text);
        if("".equals(targetGameResult.getPlace())){
            placeText.setVisibility(View.GONE);
        } else {
            placeText.setVisibility(View.VISIBLE);
        }
        placeText.setText(targetGameResult.getPlace());

        // 先攻/後攻
        TextView semeText = (TextView)findViewById(R.id.seme_text);
        if(targetGameResult.getSeme() == 0){
            semeText.setVisibility(GONE);
            semeText.setText("");
        } else if(targetGameResult.getSeme() == 1){
            semeText.setVisibility(View.VISIBLE);
            semeText.setText("  (先攻)");
        } else if(targetGameResult.getSeme() == 2){
            semeText.setVisibility(View.VISIBLE);
            semeText.setText("  (後攻)");
        }

        // 打順と守備位置
        TextView dajunShubiText = (TextView)findViewById(R.id.dajunshubi_text);
        if(targetGameResult.getDajun() == 0 && targetGameResult.getShubi1() == 0){
            dajunShubiText.setVisibility(View.GONE);
        } else {
            dajunShubiText.setVisibility(View.VISIBLE);
        }

        StringBuilder dajunShubiStr = new StringBuilder("  ");
        if(targetGameResult.getDajun() != 0) {
            dajunShubiStr.append(GameResult.DAJUN_STRING[targetGameResult.getDajun()]);
            dajunShubiStr.append(" ");
        }
        String[] shubiString = targetGameResult.getShubi3() == 0 ? GameResult.SHUBI_STRING : GameResult.SHUBI_SHORT_STRING;
        if(targetGameResult.getShubi1() != 0 ){
            dajunShubiStr.append(shubiString[targetGameResult.getShubi1()]);
        }
        if(targetGameResult.getShubi2() != 0 ){
            dajunShubiStr.append("→");
            dajunShubiStr.append(shubiString[targetGameResult.getShubi2()]);
        }
        if(targetGameResult.getShubi3() != 0 ){
            dajunShubiStr.append("→");
            dajunShubiStr.append(shubiString[targetGameResult.getShubi3()]);
        }
        dajunShubiText.setText(dajunShubiStr.toString());

        // 打席の結果
        showBattingResult();

        // その他の打撃成績
        TextView etcText = (TextView)findViewById(R.id.etc_text);
        etcText.setText("  打点 " + targetGameResult.getDaten() + "  得点 " + targetGameResult.getTokuten()
                + "  失策 " + targetGameResult.getError());

        TextView etc2Text = (TextView)findViewById(R.id.etc2_text);
        etc2Text.setText("  盗塁 " + targetGameResult.getSteal() + "  盗塁死 " + targetGameResult.getStealOut());

        // 投手成績
        LinearLayout pitchingTitleLayout = (LinearLayout)findViewById(R.id.pitching_title_layout);
        LinearLayout pitchingResult1Layout = (LinearLayout)findViewById(R.id.pitching_result1_layout);
        LinearLayout pitchingResult2Layout = (LinearLayout)findViewById(R.id.pitching_result2_layout);
        LinearLayout pitchingResult3Layout = (LinearLayout)findViewById(R.id.pitching_result3_layout);
        LinearLayout pitchingResult4Layout = (LinearLayout)findViewById(R.id.pitching_result4_layout);
        LinearLayout pitchingResult5Layout = (LinearLayout)findViewById(R.id.pitching_result5_layout);

        if(targetGameResult.getInning() == 0 && targetGameResult.getInning2() == 0){
            pitchingTitleLayout.setVisibility(GONE);
            pitchingResult1Layout.setVisibility(GONE);
            pitchingResult2Layout.setVisibility(GONE);
            pitchingResult3Layout.setVisibility(GONE);
            pitchingResult4Layout.setVisibility(GONE);
            pitchingResult5Layout.setVisibility(GONE);
        } else {
            pitchingTitleLayout.setVisibility(View.VISIBLE);
            pitchingResult1Layout.setVisibility(View.VISIBLE);
            pitchingResult2Layout.setVisibility(View.VISIBLE);
            pitchingResult3Layout.setVisibility(View.VISIBLE);
            pitchingResult4Layout.setVisibility(View.VISIBLE);
            if(targetGameResult.getTamakazu() == GameResult.TAMAKAZU_NONE) {
                pitchingResult5Layout.setVisibility(GONE);
            } else {
                pitchingResult5Layout.setVisibility(View.VISIBLE);
            }

            TextView inningText = (TextView)findViewById(R.id.inning);
            TextView sekininText = (TextView)findViewById(R.id.sekinin);
            TextView hiandaText = (TextView)findViewById(R.id.hianda);
            TextView hihomerunText = (TextView)findViewById(R.id.hihomerun);
            TextView dassanshinText = (TextView)findViewById(R.id.dassanshin);
            TextView yoshikyuText = (TextView)findViewById(R.id.yoshikyu);
            TextView yoshikyu2Text = (TextView)findViewById(R.id.yoshikyu2);
            TextView shittenText = (TextView)findViewById(R.id.shitten);
            TextView jisekitenText = (TextView)findViewById(R.id.jisekiten);
            TextView tamakazuText = (TextView)findViewById(R.id.tamakazu);

            inningText.setText(targetGameResult.getInningString()
                +(targetGameResult.isKanto() ? "(完投)" : ""));
            sekininText.setText(Html.fromHtml(targetGameResult.getSekininString(true)));
            hiandaText.setText(String.valueOf(targetGameResult.getHianda()));
            hihomerunText.setText(String.valueOf(targetGameResult.getHihomerun()));
            dassanshinText.setText(String.valueOf(targetGameResult.getDassanshin()));
            yoshikyuText.setText(String.valueOf(targetGameResult.getYoshikyu()));
            yoshikyu2Text.setText(String.valueOf(targetGameResult.getYoshikyu2()));
            shittenText.setText(String.valueOf(targetGameResult.getShitten()));
            jisekitenText.setText(String.valueOf(targetGameResult.getJisekiten()));
            tamakazuText.setText(String.valueOf(targetGameResult.getTamakazu()));
        }

        // メモ
        String memo = targetGameResult.getMemo();
        LinearLayout memoTitleLayout = (LinearLayout)findViewById(R.id.memo_title_layout);
        LinearLayout memoLayout = (LinearLayout)findViewById(R.id.memo_layout);
        TextView memoText = (TextView)findViewById(R.id.memo_text);

        if("".equals(memo)){
            memoTitleLayout.setVisibility(GONE);
            memoLayout.setVisibility(GONE);
        } else {
            memoTitleLayout.setVisibility(LinearLayout.VISIBLE);
            memoLayout.setVisibility(LinearLayout.VISIBLE);
            memoText.setText(targetGameResult.getMemo());
        }

    }

    private void showBattingResult() {
        LinearLayout battingResultListLayout = (LinearLayout) findViewById(R.id.batting_result_list_text);
        List<BattingResult> battingResultList = targetGameResult.getBattingResultList();

        battingResultListLayout.removeAllViews();

        for (int i = 0; i < battingResultList.size(); i++) {
            BattingResult battingResult = battingResultList.get(i);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setText("  第" + (i + 1) + "打席  ");
            textView.setWidth((int)(80 * Utility.getDensity()));
            textView.setTextSize(17f);

            TextView textView2 = new TextView(this);
            textView2.setText(Html.fromHtml(battingResult.getBattingResultString(true)));
            textView2.setTextSize(17f);

            layout.addView(textView);
            layout.addView(textView2);

            battingResultListLayout.addView(layout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_game_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_edit:
                moveInputActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.share_button:
                shareButton();
                break;
            case R.id.delete_button:
                deleteButton();
                break;
        }
    }

    private void shareButton(){
        StringBuilder shareString = new StringBuilder();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);

        if(targetGameResult.getYear() == year && targetGameResult.getMonth() == month
                && targetGameResult.getDay() == day){
            shareString.append("今日");
        } else if(targetGameResult.getYear() == year){
            shareString.append(targetGameResult.getMonth()+"月"+targetGameResult.getDay()+"日");
        } else {
            shareString.append(targetGameResult.getYear()+"年"+targetGameResult.getMonth()+"月"+targetGameResult.getDay()+"日");
        }

        shareString.append("の試合は "+targetGameResult.getMyteam()+" "+targetGameResult.getMyscore()+"-"+
            targetGameResult.getOtherscore()+" "+targetGameResult.getOtherteam()+" で");

        if(targetGameResult.getMyscore() > targetGameResult.getOtherscore()){
            shareString.append("勝ちました。");
        } else if(targetGameResult.getMyscore() == targetGameResult.getOtherscore()){
            shareString.append("引き分けでした。");
        } else {
            shareString.append("負けました。");
        }

        boolean hasBatting = (targetGameResult.getBattingResultList().size() > 0);
        boolean hasPitching = false;
        if(targetGameResult.getInning() != 0 || targetGameResult.getInning2() != 0){
            hasPitching = true;
        }

        if(hasBatting){
            List<GameResult> list = new ArrayList<GameResult>();
            list.add(targetGameResult);
            BattingStatistics stat = BattingStatistics.calculateBattingStatistics(list);

            shareString.append("打撃成績は" + stat.getAtbats() + "打数" + stat.getHits() + "安打");

            if(targetGameResult.getDaten() > 0){
                shareString.append(targetGameResult.getDaten()+"打点");
            }
            if(targetGameResult.getSteal() > 0){
                shareString.append(targetGameResult.getSteal()+"盗塁");
            }

            shareString.append("(");
            for(BattingResult battingResult : targetGameResult.getBattingResultList()) {
                shareString.append(battingResult.getBattingResultShortString(false));
                shareString.append("、");
            }
            shareString.deleteCharAt(shareString.lastIndexOf("、"));
            shareString.append(")");
        }

        if(hasPitching) {
            if(hasBatting) {
                shareString.append("、");
            }

            shareString.append("投手成績は "+targetGameResult.getInningString()+" "
                    +targetGameResult.getShitten()+"失点 自責点"+targetGameResult.getJisekiten()+" "
                    +targetGameResult.getSekininString(false));

            if(targetGameResult.getSekinin() != 0){
                shareString.append(" ");
            }
        }

        if(hasBatting || hasPitching) {
            shareString.append("でした。");
        }

        shareString.append(" #ベボレコ https://play.google.com/store/apps/details?id=com.tatsuo.baseballrecorder");

        // Intentで送信
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareString.toString());
        startActivity(intent);

        Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button").setAction("Push").setLabel("試合結果参照画面―シェア").build());

    }

    private void deleteButton(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("試合結果を削除します。よろしいですか？");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameResultManager.removeGameResult(ShowGameResultActivity.this, targetGameResult.getResultId());
                ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_GAME_RESULT_LIST, true);

                // インタースティシャル広告（動画）を表示
                // showVideoAds();
                showInterstitial = true;

                finish();
            }
        });
        builder.setNegativeButton("キャンセル", null);
        builder.show();
    }

    private void moveInputActivity(){
        Intent intent = new Intent(this, InputGameResultActivity.class);
        intent.putExtra("RESULTID", targetGameResult.getResultId());
        startActivity(intent);
    }

}
