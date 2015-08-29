package com.tatsuo.baseballrecorder;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameResultListActivity extends CommonAdsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result_list);

        Button toBattingStatButton = (Button)findViewById(R.id.to_batting_stat);
        toBattingStatButton.setOnClickListener(this);

        Button toBattingAnaButton = (Button)findViewById(R.id.to_batting_ana);
        toBattingAnaButton.setOnClickListener(this);

        makeListView();
        ConfigManager.saveUpdateGameResultFlg(this, ConfigManager.VIEW_GAME_RESULT_LIST, false);

        makeAdsView();
    }

    @Override
    protected void onResume(){
        super.onResume();

        // データが更新されていれば表示を更新する
        if(ConfigManager.loadUpdateGameResultFlg(this, ConfigManager.VIEW_GAME_RESULT_LIST)) {
            updateListView();
            ConfigManager.saveUpdateGameResultFlg(this, ConfigManager.VIEW_GAME_RESULT_LIST, false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_result_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_regist:
                moveInputActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeListView(){
        adjustViewHeight();

        updateListView();

        //リスト項目がクリックされた時の処理
        ListView resultListView = (ListView)findViewById(R.id.result_list);
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> gameTitleMap = (Map<String, String>) parent.getItemAtPosition(position);
                int targetResultId = Integer.parseInt(gameTitleMap.get("resultId"));

                // 試合結果照会画面に遷移
                Intent intent = new Intent(GameResultListActivity.this, ShowGameResultActivity.class);
                intent.putExtra("RESULTID", targetResultId);
                startActivity(intent);
            }
        });

    }

    private void updateListView(){
        final List<GameResult> gameResultList = GameResultManager.loadGameResultList(this);
        ListView resultListView = (ListView)findViewById(R.id.result_list);
        TextView tutorialText = (TextView)findViewById(R.id.tutorial_text);
        if(gameResultList.size() == 0){
            resultListView.setVisibility(View.GONE);
            tutorialText.setVisibility(View.VISIBLE);
            return;
        } else {
            resultListView.setVisibility(View.VISIBLE);
            tutorialText.setVisibility(View.GONE);
        }

        List<Map<String, String>> gameTitleMapList = new ArrayList<Map<String, String>>();
        for(GameResult gameResult : gameResultList) {
            Map<String, String> gameTitleMap = new HashMap<String, String>();
            gameTitleMap.put("Title", gameResult.getTitleString());
            gameTitleMap.put("SubTitle", gameResult.getSubTitleString());
            gameTitleMap.put("resultId", Integer.toString(gameResult.getResultId()));
            gameTitleMapList.add(gameTitleMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, gameTitleMapList,
                android.R.layout.simple_list_item_2,
                new String[] { "Title", "SubTitle" },
                new int[] { android.R.id.text1, android.R.id.text2 });

        resultListView.setAdapter(adapter);
    }

    @Override
    protected void adjustViewHeight(){
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        int height = dm.heightPixels;
        float density = dm.density;

        int header = 81; //dp
        int headerpx = (int)(header * density + 0.5f); // 0.5fは四捨五入用

        int footer = 50; //dp
        int footerpx = (int)(footer * density + 0.5f);

        int ads = isAds ? 50 : 0; //dp
        int adspx = (int)(ads * density + 0.5f);

        int listViewHeight = height - (headerpx + footerpx + adspx);

        LinearLayout resultListLayout = (LinearLayout)findViewById(R.id.result_list_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, listViewHeight);
        resultListLayout.setLayoutParams(params);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.to_batting_stat:
                moveBattingStatisticsActivity();
                break;
            case R.id.to_batting_ana:
                moveBattingAnalysisActivity();
                break;
        }
    }

    private void moveInputActivity(){
        Intent intent = new Intent(this, InputGameResultActivity.class);
        startActivity(intent);
    }

    private void moveBattingStatisticsActivity(){
        Intent intent = new Intent(this, BattingStatisticsActivity.class);
        startActivity(intent);
    }

    private void moveBattingAnalysisActivity(){
        Intent intent = new Intent(this, BattingAnalysisActivity.class);
        startActivity(intent);
    }

}
