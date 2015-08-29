package com.tatsuo.baseballrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.tatsuo.baseballrecorder.domain.BattingResult;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.util.Utility;

import java.util.List;

public class ShowGameResultActivity extends CommonAdsActivity implements View.OnClickListener {

    private static final int NO_TARGET = -999;
    private GameResult targetGameResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_game_result);

        Button shareButton = (Button)findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);

        Button deleteButton = (Button)findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this);

        Intent intent = getIntent();
        int resultId = intent.getIntExtra("RESULTID", NO_TARGET);

        if(resultId != NO_TARGET){
            targetGameResult = GameResultManager.loadGameResult(this, resultId);
        } else {
            targetGameResult = new GameResult();
        }

        updateResultView();
        ConfigManager.saveUpdateGameResultFlg(this, ConfigManager.VIEW_SHOW_GAME_RESULT, false);

        makeAdsView();
    }

    @Override
    protected void onResume(){
        super.onResume();

        // データが更新されていれば表示を更新する
        if(ConfigManager.loadUpdateGameResultFlg(this, ConfigManager.VIEW_SHOW_GAME_RESULT)) {
            targetGameResult = GameResultManager.loadGameResult(this, targetGameResult.getResultId());
            updateResultView();
            ConfigManager.saveUpdateGameResultFlg(this, ConfigManager.VIEW_SHOW_GAME_RESULT, false);
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
        TextView dateText = (TextView)findViewById(R.id.date_text);
        dateText.setText(targetGameResult.getYear() + "年" + targetGameResult.getMonth() + "月"
                + targetGameResult.getDay() + "日");

        TextView placeText = (TextView)findViewById(R.id.place_text);
        placeText.setText(targetGameResult.getPlace());

        TextView scoreText = (TextView)findViewById(R.id.score_text);
        scoreText.setText(targetGameResult.getMyteam()+" "+targetGameResult.getMyscore()+ " - "
                +targetGameResult.getOtherscore()+" "+targetGameResult.getOtherteam());

        showBattingResult();

        TextView etcText = (TextView)findViewById(R.id.etc_text);
        etcText.setText("  打点 " + targetGameResult.getDaten() + "  得点 " + targetGameResult.getTokuten()
                + "  盗塁 " + targetGameResult.getSteal());

        String memo = targetGameResult.getMemo();
        LinearLayout memoTitleLayout = (LinearLayout)findViewById(R.id.memo_title_layout);
        LinearLayout memoLayout = (LinearLayout)findViewById(R.id.memo_layout);
        TextView memoText = (TextView)findViewById(R.id.memo_text);

        if("".equals(memo)){
            memoTitleLayout.setVisibility(LinearLayout.GONE);
            memoLayout.setVisibility(LinearLayout.GONE);
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
            textView2.setText(battingResult.getBattingResultString());
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
        Log.i("shareButton", "shareButton");


    }

    private void deleteButton(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("試合結果を削除します。よろしいですか？");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("deleteButton", "deleteButton");
                GameResultManager.removeGameResult(ShowGameResultActivity.this, targetGameResult.getResultId());
                ConfigManager.saveUpdateGameResultFlg(ShowGameResultActivity.this, ConfigManager.VIEW_GAME_RESULT_LIST, true);
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
