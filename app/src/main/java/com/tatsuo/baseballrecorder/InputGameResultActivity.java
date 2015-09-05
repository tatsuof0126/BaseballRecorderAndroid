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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tatsuo.baseballrecorder.domain.BattingResult;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.util.Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InputGameResultActivity extends CommonAdsActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private static final int NO_TARGET = -999;
    private GameResult targetGameResult = null;
    private int targetBattingResult = NO_TARGET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_game_result);

        Intent intent = getIntent();
        int resultId = intent.getIntExtra("RESULTID", NO_TARGET);

        if(resultId != NO_TARGET){
            targetGameResult = GameResultManager.loadGameResult(this, resultId);
            makeGameResultView();
        } else {
            targetGameResult = new GameResult();

            Calendar calendar = Calendar.getInstance();
            targetGameResult.setYear(calendar.get(Calendar.YEAR));
            targetGameResult.setMonth(calendar.get(Calendar.MONTH) + 1);
            targetGameResult.setDay(calendar.get(Calendar.DATE));

            makeGameResultView();
        }

        Button saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);

//        Button backButton = (Button)findViewById(R.id.back_button);
//        backButton.setOnClickListener(this);

        Button addButton = (Button)findViewById(R.id.add_button);
        addButton.setOnClickListener(this);

        Button modifyButton = (Button)findViewById(R.id.modify_button);
        modifyButton.setOnClickListener(this);

        Button deleteButton = (Button)findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_game_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
//            case R.id.action_save:
//                saveButton();
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeGameResultView(){
        EditText yearText = (EditText)findViewById(R.id.year);
        yearText.setText(Integer.toString(targetGameResult.getYear()));
        yearText.setOnFocusChangeListener(this);
        yearText.setNextFocusDownId(R.id.month);

        EditText monthText = (EditText)findViewById(R.id.month);
        monthText.setText(Integer.toString(targetGameResult.getMonth()));
        monthText.setNextFocusDownId(R.id.day);

        EditText dayText = (EditText)findViewById(R.id.day);
        dayText.setText(Integer.toString(targetGameResult.getDay()));
        dayText.setNextFocusDownId(R.id.place);

        EditText placeText = (EditText)findViewById(R.id.place);
        placeText.setText(targetGameResult.getPlace());
        placeText.setNextFocusDownId(R.id.myteam);

        EditText myteamText = (EditText)findViewById(R.id.myteam);
        myteamText.setText(targetGameResult.getMyteam());
        myteamText.setNextFocusDownId(R.id.otherteam);

        EditText otherteamText = (EditText)findViewById(R.id.otherteam);
        otherteamText.setText(targetGameResult.getOtherteam());
        otherteamText.setNextFocusDownId(R.id.myscore);

        EditText myscoreText = (EditText)findViewById(R.id.myscore);
        myscoreText.setText(Integer.toString(targetGameResult.getMyscore()));
        myscoreText.setNextFocusDownId(R.id.otherscore);
        myscoreText.setOnFocusChangeListener(this);

        EditText otherscoreText = (EditText)findViewById(R.id.otherscore);
        otherscoreText.setText(Integer.toString(targetGameResult.getOtherscore()));
        otherscoreText.setNextFocusDownId(R.id.daten);
        otherscoreText.setOnFocusChangeListener(this);

        showBattingResult();

        EditText datenText = (EditText)findViewById(R.id.daten);
        datenText.setText(Integer.toString(targetGameResult.getDaten()));
        datenText.setNextFocusDownId(R.id.tokuten);
        datenText.setOnFocusChangeListener(this);

        EditText tokutenText = (EditText)findViewById(R.id.tokuten);
        tokutenText.setText(Integer.toString(targetGameResult.getTokuten()));
        tokutenText.setNextFocusDownId(R.id.steal);
        tokutenText.setOnFocusChangeListener(this);

        EditText stealText = (EditText)findViewById(R.id.steal);
        stealText.setText(Integer.toString(targetGameResult.getSteal()));
        stealText.setNextFocusDownId(R.id.memo);
        stealText.setOnFocusChangeListener(this);

        EditText memoText = (EditText)findViewById(R.id.memo);
        memoText.setText(targetGameResult.getMemo());
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
//            case R.id.back_button:
//                backButton();
//                break;
            case R.id.save_button:
                saveButton();
                break;
            case R.id.add_button:
                addButton();
                break;
            case R.id.modify_button:
                modifyButton();
                break;
            case R.id.delete_button:
                deleteButton();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText myscoreText = (EditText)findViewById(R.id.myscore);
        EditText otherscoreText = (EditText)findViewById(R.id.otherscore);
        EditText datenText = (EditText)findViewById(R.id.daten);
        EditText tokutenText = (EditText)findViewById(R.id.tokuten);
        EditText stealText = (EditText)findViewById(R.id.steal);

        if(hasFocus == true) {
            // 数値系の項目で「0」ならフォーカスがあたったとき空にする
            if((v == myscoreText || v == otherscoreText || v == datenText ||
                    v == tokutenText || v == stealText) &&
                    "0".equals(((EditText)v).getText().toString())) {
                ((EditText) v).setText("");
            }
        } else {
            // 数値系の項目で空ならフォーカスがはずれたとき0にする
            if((v == myscoreText || v == otherscoreText || v == datenText ||
                    v == tokutenText || v == stealText) &&
                    "".equals(((EditText)v).getText().toString())) {
                ((EditText) v).setText("0");
            }
        }
    }

    private void backButton(){
        finish();
    }

    private void saveButton(){
        // 入力値を取得
        String yearStr = ((EditText)findViewById(R.id.year)).getText().toString();
        String monthStr = ((EditText)findViewById(R.id.month)).getText().toString();
        String dayStr = ((EditText)findViewById(R.id.day)).getText().toString();
        String placeStr = ((EditText)findViewById(R.id.place)).getText().toString();
        String myteamStr = ((EditText)findViewById(R.id.myteam)).getText().toString();
        String otherteamStr = ((EditText)findViewById(R.id.otherteam)).getText().toString();
        String myscoreStr = ((EditText)findViewById(R.id.myscore)).getText().toString();
        String otherscoreStr = ((EditText)findViewById(R.id.otherscore)).getText().toString();
        String datenStr = ((EditText)findViewById(R.id.daten)).getText().toString();
        String tokutenStr = ((EditText)findViewById(R.id.tokuten)).getText().toString();
        String stealStr = ((EditText)findViewById(R.id.steal)).getText().toString();
        String memoStr = ((EditText)findViewById(R.id.memo)).getText().toString();

        // 半角カンマを全角に変換
        placeStr = Utility.replaceComma(placeStr);
        myteamStr = Utility.replaceComma(myteamStr);
        otherteamStr = Utility.replaceComma(otherteamStr);
        memoStr = Utility.replaceComma(memoStr);

        // 必須項目はtrim
        placeStr = placeStr.trim();
        myteamStr = myteamStr.trim();
        otherteamStr = otherteamStr.trim();

        // 入力チェック
        boolean noinputcheck = false;
        boolean datecheck = false;
        boolean numbercheck = false;

        // 場所とチーム・相手チームは必須
        if("".equals(placeStr) || "".equals(myteamStr) || "".equals(otherteamStr)){
            noinputcheck = true;
        }

        // 日付のチェック
        try {
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            format.parse(yearStr+"-"+monthStr+"-"+dayStr);
        } catch (ParseException e) {
            datecheck = true;
        }

        // 数値型のチェック
        try {
            Integer.parseInt(myscoreStr);
            Integer.parseInt(otherscoreStr);
            Integer.parseInt(datenStr);
            Integer.parseInt(tokutenStr);
            Integer.parseInt(stealStr);
        } catch (NumberFormatException e) {
            numbercheck = true;
        }

        if(noinputcheck) {
            Toast.makeText(this, "入力されていない項目があります", Toast.LENGTH_SHORT).show();
            return;
        }

        if(datecheck) {
            Toast.makeText(this, "日付が正しくありません", Toast.LENGTH_SHORT).show();
            return;
        }

        if(numbercheck) {
            Toast.makeText(this, "数値の入力が正しくありません", Toast.LENGTH_SHORT).show();
            return;
        }

        targetGameResult.setYear(Integer.parseInt(yearStr));
        targetGameResult.setMonth(Integer.parseInt(monthStr));
        targetGameResult.setDay(Integer.parseInt(dayStr));
        targetGameResult.setPlace(placeStr);
        targetGameResult.setMyteam(myteamStr);
        targetGameResult.setOtherteam(otherteamStr);
        targetGameResult.setMyscore(Integer.parseInt(myscoreStr));
        targetGameResult.setOtherscore(Integer.parseInt(otherscoreStr));
        targetGameResult.setDaten(Integer.parseInt(datenStr));
        targetGameResult.setTokuten(Integer.parseInt(tokutenStr));
        targetGameResult.setSteal(Integer.parseInt(stealStr));
        targetGameResult.setMemo(memoStr);

        // 登録か更新かを判断
        boolean registFlg = (targetGameResult.getResultId() == GameResult.NON_REGISTED);

        GameResultManager.saveGameResult(this, targetGameResult);
        ConfigManager.saveUpdateGameResultFlg(this, ConfigManager.VIEW_ALL, true);

        if(registFlg){
            Tracker tracker = ((AnalyticsApplication)getApplication()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Button").setAction("Push").setLabel("打撃成績入力画面―登録").build());
            Toast.makeText(this, "登録しました", Toast.LENGTH_LONG).show();
        } else {
            Tracker tracker = ((AnalyticsApplication)getApplication()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Button").setAction("Push").setLabel("打撃成績入力画面―更新").build());
            Toast.makeText(this, "保存しました", Toast.LENGTH_LONG).show();
        }

        finish();
    }

    private void addButton(){
        showResultPicker(NO_TARGET);
    }

    private void modifyButton(){
        showSelectResultPickerForModify();
    }

    private void deleteButton(){
        showSelectResultPickerForDelete();
    }

    private void showResultPicker(int target){
        targetBattingResult = target;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("打席の結果を選択");
        builder.setItems(BattingResult.RESULTS_PICKER, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                makeBattingResult(i+1);
            }
        });
        builder.show();
    }

    private void showPositionPicker(final int result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("打球方向を選択");
        builder.setItems(BattingResult.POSITIONS_PICKER, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                makeBattingResult(i+1, result);
            }
        });
        builder.show();
    }

    // 結果を選択した時
    private void makeBattingResult(int result){
        boolean needPosition = BattingResult.NEED_POSITION[result];
        if(needPosition == false){
            updateBattingResult(new BattingResult(result));
        } else {
            showPositionPicker(result);
        }
    }

    // 結果を選択後、打球方向も選択したとき
    private void makeBattingResult(int position, int result) {
        updateBattingResult(new BattingResult(position, result));
    }

    private void showSelectResultPickerForModify() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("変更する結果を選択");
        builder.setItems(makeResultStringList(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showResultPicker(i);
            }
        });
        builder.show();
    }

    private void showSelectResultPickerForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("削除する結果を選択");
        builder.setItems(makeResultStringList(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBattingResult(i);
            }
        });
        builder.show();
    }

    private String[] makeResultStringList(){
        List<BattingResult> battingResultList = targetGameResult.getBattingResultList();
        List<String> pickerStringList = new ArrayList<String>();
        for(int i=0;i<battingResultList.size();i++){
            BattingResult battingResult = battingResultList.get(i);
            String pickerString = "第"+(i+1)+"打席 "+battingResult.getBattingResultString();
            pickerStringList.add(pickerString);
        }

        return (String[])pickerStringList.toArray(new String[0]);
    }

    private void updateBattingResult(BattingResult battingResult){
        List battingResultList = targetGameResult.getBattingResultList();
        if(targetBattingResult == NO_TARGET) {
            battingResultList.add(battingResult);
        } else {
            battingResultList.remove(targetBattingResult);
            battingResultList.add(targetBattingResult, battingResult);
        }
        showBattingResult();
    }

    private void deleteBattingResult(int target){
        List battingResultList = targetGameResult.getBattingResultList();
        battingResultList.remove(target);
        showBattingResult();
    }

    private void showBattingResult(){
        LinearLayout battingResultListLayout = (LinearLayout)findViewById(R.id.batting_result_list);
        List<BattingResult> battingResultList = targetGameResult.getBattingResultList();

        battingResultListLayout.removeAllViews();

        for(int i=0;i<battingResultList.size();i++){
            BattingResult battingResult = battingResultList.get(i);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setText(" 第" + (i+1) + "打席  ");
            textView.setWidth((int) (80 * Utility.getDensity()));
            textView.setTextSize(17f);

            TextView textView2 = new TextView(this);
            textView2.setText(battingResult.getBattingResultString());
            textView2.setTextSize(17f);

            layout.addView(textView);
            layout.addView(textView2);

            battingResultListLayout.addView(layout);
        }

        Button modifyButton = (Button)findViewById(R.id.modify_button);
        Button deleteButton = (Button)findViewById(R.id.delete_button);
        if(battingResultList.size() > 0){
            modifyButton.setVisibility(Button.VISIBLE);
            deleteButton.setVisibility(Button.VISIBLE);
        } else {
            modifyButton.setVisibility(Button.INVISIBLE);
            deleteButton.setVisibility(Button.INVISIBLE);
        }
    }
}
