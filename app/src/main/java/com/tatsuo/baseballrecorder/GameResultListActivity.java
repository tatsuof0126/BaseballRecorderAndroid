package com.tatsuo.baseballrecorder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;

import java.util.List;

public class GameResultListActivity extends CommonAdsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GoogleAnalytics初期化
        ((BaseballRecorderApplication)getApplication()).getTracker();

        // テストデータ作成用
        if(ConfigManager.makeTestData){
            GameResultManager.makeTestData(this);
        }

        setContentView(R.layout.activity_game_result_list);

        ((Button)findViewById(R.id.to_batting_stat)).setOnClickListener(this);
        ((Button)findViewById(R.id.to_batting_ana)).setOnClickListener(this);
        ((Button)findViewById(R.id.to_pitching_stat)).setOnClickListener(this);
        ((Button)findViewById(R.id.to_config)).setOnClickListener(this);

        makeListView();
        ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_GAME_RESULT_LIST, false);

        makeAdsView();

        // インタースティシャル広告（動画）を準備
        prepareInterstitial();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // GoogleAnalytics開始
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // GoogleAnalytics停止
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    protected void onResume(){
        super.onResume();

        // データが更新されていれば表示を更新する
        if(ConfigManager.loadUpdateGameResultFlg(ConfigManager.VIEW_GAME_RESULT_LIST)) {
            updateListView();
            ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_GAME_RESULT_LIST, false);
        }

        // インタースティシャル広告（動画）を表示
        if(showInterstitial){
            showInterstitial();
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
                GameResult gameResult = (GameResult)parent.getItemAtPosition(position);

                // 試合結果照会画面に遷移
                Intent intent = new Intent(GameResultListActivity.this, ShowGameResultActivity.class);
                intent.putExtra("RESULTID", gameResult.getResultId());
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

        GameResultListAdapter adapter = new GameResultListAdapter(this, gameResultList);

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
            case R.id.to_pitching_stat:
                movePitchingStatisticsActivity();
                break;
            case R.id.to_config:
                moveConfigActivity();
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

    private void movePitchingStatisticsActivity(){
        Intent intent = new Intent(this, PitchingStatisticsActivity.class);
        startActivity(intent);
    }

    private void moveConfigActivity(){
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }

    class GameResultListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater = null;
        private List<GameResult> gameResultList = null;

        public GameResultListAdapter(Context context, List<GameResult> gameResultList) {
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.gameResultList = gameResultList;
        }

        @Override
        public int getCount() {
            return gameResultList.size();
        }

        @Override
        public Object getItem(int position) {
            return gameResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return gameResultList.get(position).getResultId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.game_result_list_row, parent, false);

            TextView titleView = (TextView)convertView.findViewById(R.id.row_title);
            TextView subtitleView = (TextView)convertView.findViewById(R.id.row_subtitle);
            GameResult gameResult = gameResultList.get(position);

            titleView.setText(gameResult.getTitleString());
            subtitleView.setText(Html.fromHtml(gameResult.getSubTitleString()));

            // リスト項目のタップを横幅全体にきかせるようにするためTextViewの横幅を広げる
            DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
            titleView.setWidth(dm.widthPixels);

            return convertView;
        }

    }

}
