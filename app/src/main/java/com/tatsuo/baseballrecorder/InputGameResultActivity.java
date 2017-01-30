package com.tatsuo.baseballrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
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

public class InputGameResultActivity extends CommonAdsActivity
        implements View.OnFocusChangeListener, View.OnClickListener {

    private static final int NO_TARGET = -999;
    private GameResult targetGameResult = null;
    private int targetBattingResult = NO_TARGET;
    private int spinnerMode = SPINNER_MODE_NONE;

    private static final int SPINNER_MODE_NONE = 0;
    private static final int SPINNER_MODE_1 = 1;
    private static final int SPINNER_MODE_2 = 2;
    private static final int SPINNER_MODE_3 = 3;

    private static final int MOVE_PITCHING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_game_result);

        Intent intent = getIntent();
        int resultId = intent.getIntExtra("RESULTID", NO_TARGET);

        if(resultId != NO_TARGET){
            targetGameResult = GameResultManager.loadGameResult(this, resultId);
        } else {
            targetGameResult = new GameResult();

            Calendar calendar = Calendar.getInstance();
            targetGameResult.setYear(calendar.get(Calendar.YEAR));
            targetGameResult.setMonth(calendar.get(Calendar.MONTH) + 1);
            targetGameResult.setDay(calendar.get(Calendar.DATE));

            // 場所と自チームについては過去データが１種類ならそれをデフォルトにする
            List<GameResult> gameResultList = GameResultManager.loadGameResultList(this);
            String defaultPlace = null;
            String defaultMyteam = null;
            for(GameResult gameResult : gameResultList){
                if(defaultPlace == null){
                    defaultPlace = gameResult.getPlace();
                } else if(defaultPlace.equals(gameResult.getPlace()) == false){
                    defaultPlace = "";
                }

                if(defaultMyteam == null){
                    defaultMyteam = gameResult.getMyteam();
                } else if(defaultMyteam.equals(gameResult.getMyteam()) == false){
                    defaultMyteam = "";
                }
            }
            targetGameResult.setPlace(defaultPlace);
            targetGameResult.setMyteam(defaultMyteam);
        }

        makeGameResultView();

        ((Button)findViewById(R.id.save_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.add_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.modify_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.delete_button)).setOnClickListener(this);

        // インタースティシャル広告（動画）を準備
        // prepareVideoAds();
        // prepareInterstitial();

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
            case R.id.action_pitching:
                movePitchingActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void movePitchingActivity(){
        // 入力チェックを実施
        String errorMsg = inputCheck();

        // 入力エラーがあればToastを出して次に進まない
        if("".equals(errorMsg) == false) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        // targetGameResultを更新
        updateTargetGameResult();

        Intent intent = new Intent(this, InputPitchingResultActivity.class);
        intent.putExtra("GAME_RESULT", targetGameResult);
        startActivityForResult(intent, MOVE_PITCHING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
            case InputPitchingResultActivity.RESULT_TO_BATTING:
                targetGameResult = (GameResult)intent.getSerializableExtra("GAME_RESULT");
                break;
            case InputPitchingResultActivity.RESULT_SAVED_REGIST:
                // インタースティシャル広告（動画）を表示
                showInterstitial = true;

                // 試合結果照会画面に遷移
                int resultId = intent.getIntExtra("RESULTID", NO_TARGET);
                Intent newIntent = new Intent(this, ShowGameResultActivity.class);
                newIntent.putExtra("RESULTID", resultId);
                startActivity(newIntent);

                finish();
                break;
            case InputPitchingResultActivity.RESULT_SAVED_UPDATE:
                // インタースティシャル広告（動画）を表示
                showInterstitial = true;

                finish();
                break;
            case RESULT_CANCELED:
                break;
        }
    }

    private void makeGameResultView(){
        EditText yearText = (EditText)findViewById(R.id.year);
        yearText.setText(Integer.toString(targetGameResult.getYear()));
        // yearText.setOnFocusChangeListener(this);
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

        RadioButton semeRadio1 = (RadioButton)findViewById(R.id.semeradio1);
        RadioButton semeRadio2 = (RadioButton)findViewById(R.id.semeradio2);
        semeRadio1.setChecked(false);
        semeRadio2.setChecked(false);
        if(targetGameResult.getSeme() == 1){
            semeRadio1.setChecked(true);
        } else if(targetGameResult.getSeme() == 2){
            semeRadio2.setChecked(true);
        }

        Spinner dajunSpinner = (Spinner)findViewById(R.id.dajunspinner);
        ArrayAdapter<String> dajunAdapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, GameResult.DAJUN_SPINNER);
        dajunSpinner.setAdapter(dajunAdapter);
        dajunSpinner.setSelection(targetGameResult.getDajun());

        Spinner shubi1Spinner = (Spinner)findViewById(R.id.shubi1spinner);
        shubi1Spinner.setSelection(targetGameResult.getShubi1());

        Spinner shubi2Spinner = (Spinner)findViewById(R.id.shubi2spinner);
        shubi2Spinner.setSelection(targetGameResult.getShubi2());

        Spinner shubi3Spinner = (Spinner)findViewById(R.id.shubi3spinner);
        shubi3Spinner.setSelection(targetGameResult.getShubi3());
        spinnerMode = SPINNER_MODE_NONE;

        adjustShubiSpinnerVisibility();

        OnItemSelectedListener listener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                adjustShubiSpinnerVisibility();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        shubi1Spinner.setOnItemSelectedListener(listener);
        shubi2Spinner.setOnItemSelectedListener(listener);
        shubi3Spinner.setOnItemSelectedListener(listener);

        showBattingResult();

        EditText datenText = (EditText)findViewById(R.id.daten);
        datenText.setText(Integer.toString(targetGameResult.getDaten()));
        datenText.setNextFocusDownId(R.id.tokuten);
        datenText.setOnFocusChangeListener(this);

        EditText tokutenText = (EditText)findViewById(R.id.tokuten);
        tokutenText.setText(Integer.toString(targetGameResult.getTokuten()));
        tokutenText.setNextFocusDownId(R.id.error);
        tokutenText.setOnFocusChangeListener(this);

        EditText errorText = (EditText)findViewById(R.id.error);
        errorText.setText(Integer.toString(targetGameResult.getError()));
        errorText.setNextFocusDownId(R.id.steal);
        errorText.setOnFocusChangeListener(this);

        EditText stealText = (EditText)findViewById(R.id.steal);
        stealText.setText(Integer.toString(targetGameResult.getSteal()));
        stealText.setNextFocusDownId(R.id.stealout);
        stealText.setOnFocusChangeListener(this);

        EditText stealOutText = (EditText)findViewById(R.id.stealout);
        stealOutText.setText(Integer.toString(targetGameResult.getStealOut()));
        stealOutText.setNextFocusDownId(R.id.memo);
        stealOutText.setOnFocusChangeListener(this);

        EditText memoText = (EditText)findViewById(R.id.memo);
        memoText.setText(targetGameResult.getMemo());
    }

    private void adjustShubiSpinnerVisibility(){
        ArrayAdapter<String> shubiAdapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, GameResult.SHUBI_SPINNER);

        ArrayAdapter<String> shubiShortAdapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, GameResult.SHUBI_SHORT_SPINNER);

        Spinner shubi1Spinner = (Spinner)findViewById(R.id.shubi1spinner);
        Spinner shubi2Spinner = (Spinner)findViewById(R.id.shubi2spinner);
        Spinner shubi3Spinner = (Spinner)findViewById(R.id.shubi3spinner);

        int shubi1 = shubi1Spinner.getSelectedItemPosition();
        int shubi2 = shubi2Spinner.getSelectedItemPosition();
        int shubi3 = shubi3Spinner.getSelectedItemPosition();

        int afterMode = getShubiSpinnerMode(shubi1, shubi2, shubi3);

        Log.e("[ADJUST SPINNER]", "before:"+spinnerMode+" after:"+afterMode+"  "+shubi1+":"+shubi2+":"+shubi3);

        if(spinnerMode == afterMode){
            return;
        }
        spinnerMode = afterMode;

        if(spinnerMode == SPINNER_MODE_1){
            shubi1Spinner.setAdapter(shubiAdapter);
            shubi1Spinner.setSelection(shubi1);
            shubi2Spinner.setVisibility(View.GONE);
            shubi3Spinner.setVisibility(View.GONE);
        } else if(spinnerMode == SPINNER_MODE_2){
            shubi1Spinner.setAdapter(shubiAdapter);
            shubi2Spinner.setAdapter(shubiShortAdapter);
            shubi1Spinner.setSelection(shubi1);
            shubi2Spinner.setSelection(shubi2);
            shubi2Spinner.setVisibility(View.VISIBLE);
            shubi3Spinner.setVisibility(View.GONE);
        } else if(spinnerMode == SPINNER_MODE_3){
            shubi1Spinner.setAdapter(shubiShortAdapter);
            shubi2Spinner.setAdapter(shubiShortAdapter);
            shubi3Spinner.setAdapter(shubiShortAdapter);
            shubi1Spinner.setSelection(shubi1);
            shubi2Spinner.setSelection(shubi2);
            shubi3Spinner.setSelection(shubi3);
            shubi2Spinner.setVisibility(View.VISIBLE);
            shubi3Spinner.setVisibility(View.VISIBLE);
        }
    }

    /*
    private void adjustShubiSpinnerVisibility(Spinner parent, int position){
        Spinner shubi1Spinner = (Spinner)findViewById(R.id.shubi1spinner);
        Spinner shubi2Spinner = (Spinner)findViewById(R.id.shubi2spinner);
        Spinner shubi3Spinner = (Spinner)findViewById(R.id.shubi3spinner);

        int shubi1 = shubi1Spinner.getSelectedItemPosition();
        int shubi2 = shubi2Spinner.getSelectedItemPosition();
        int shubi3 = shubi3Spinner.getSelectedItemPosition();

        int afterMode = getShubiSpinnerMode(shubi1, shubi2, shubi3);


        // ログコード
        int target = 0;
        if(parent == shubi1Spinner){
            target = 1;
        } else if(parent == shubi2Spinner){
            target = 2;
        } else if(parent == shubi3Spinner){
            target = 3;
        }
        Log.e("MODE(ADJUST:"+target+")", "before:"+spinnerMode+" after:"+afterMode+"  "+shubi1+":"+shubi2+":"+shubi3);



        if(spinnerMode == afterMode){
            return;
        }

        spinnerMode = afterMode;

        ArrayAdapter<String> shubiAdapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, GameResult.SHUBI_SPINNER);

        ArrayAdapter<String> shubiShortAdapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, GameResult.SHUBI_SHORT_SPINNER);

        if(afterMode == SPINNER_MODE_1){
            shubi1Spinner.setAdapter(shubiAdapter);
            shubi1Spinner.setSelection(shubi1);
            shubi2Spinner.setVisibility(View.GONE);
            shubi3Spinner.setVisibility(View.GONE);
        } else if(afterMode == SPINNER_MODE_2){
            shubi1Spinner.setAdapter(shubiAdapter);
            shubi2Spinner.setAdapter(shubiShortAdapter);
            shubi1Spinner.setSelection(shubi1);
            shubi2Spinner.setSelection(shubi2);
            shubi2Spinner.setVisibility(View.VISIBLE);
            shubi3Spinner.setVisibility(View.GONE);
        } else if(afterMode == SPINNER_MODE_3){
            shubi1Spinner.setAdapter(shubiShortAdapter);
            shubi2Spinner.setAdapter(shubiShortAdapter);
            shubi3Spinner.setAdapter(shubiShortAdapter);
            shubi1Spinner.setSelection(shubi1);
            shubi2Spinner.setSelection(shubi2);
            shubi3Spinner.setSelection(shubi3);
            shubi2Spinner.setVisibility(View.VISIBLE);
            shubi3Spinner.setVisibility(View.VISIBLE);
        }
    }
*/
    private int getShubiSpinnerMode(int shubi1, int shubi2, int shubi3) {
        if (shubi1 == 0 && shubi2 == 0 && shubi3 == 0) {
            return SPINNER_MODE_1;
        } else if (shubi2 == 0 && shubi3 == 0) {
            return SPINNER_MODE_2;
        } else {
            return SPINNER_MODE_3;
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("入力内容を保存せずに戻ります。\nよろしいですか？");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("キャンセル", null);
            builder.show();

            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText myscoreText = (EditText)findViewById(R.id.myscore);
        EditText otherscoreText = (EditText)findViewById(R.id.otherscore);
        EditText datenText = (EditText)findViewById(R.id.daten);
        EditText tokutenText = (EditText)findViewById(R.id.tokuten);
        EditText errorText = (EditText)findViewById(R.id.error);
        EditText stealText = (EditText)findViewById(R.id.steal);
        EditText stealOutText = (EditText)findViewById(R.id.stealout);

        if(hasFocus == true) {
            // 数値系の項目で「0」ならフォーカスがあたったとき空にする
            if((v == myscoreText || v == otherscoreText || v == datenText || v == tokutenText ||
                    v == errorText || v == stealText || v == stealOutText) &&
                    "0".equals(((EditText)v).getText().toString())) {
                ((EditText)v).setText("");
            }
        } else {
            // 数値系の項目で空ならフォーカスがはずれたとき0にする
            if((v == myscoreText || v == otherscoreText || v == datenText || v == tokutenText ||
                    v == errorText || v == stealText || v == stealOutText) &&
                    "".equals(((EditText)v).getText().toString())) {
                ((EditText)v).setText("0");
            }
        }
    }

    private void saveButton() {
        // 入力チェックを実施
        String errorMsg = inputCheck();

        // 入力エラーがあればToastを出して次に進まない
        if ("".equals(errorMsg) == false) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("試合結果を保存します。\nよろしいですか？");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveGameResult();
            }
        });
        builder.setNegativeButton("キャンセル", null);
        builder.show();
    }

    private void saveGameResult(){
        // targetGameResultを更新
        updateTargetGameResult();

        // 登録か更新かを判断
        boolean registFlg = (targetGameResult.getResultId() == GameResult.NON_REGISTED);

        GameResultManager.saveGameResult(this, targetGameResult);
        ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_ALL, true);

        if(registFlg){
            Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Button").setAction("Push").setLabel("打撃成績入力画面―登録").build());
            Toast.makeText(this, "登録しました", Toast.LENGTH_LONG).show();
        } else {
            Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Button").setAction("Push").setLabel("打撃成績入力画面―更新").build());
            Toast.makeText(this, "保存しました", Toast.LENGTH_LONG).show();
        }

        // インタースティシャル広告（動画）を表示
        // showVideoAds();
        showInterstitial = true;

        // 試合結果照会画面に遷移
        if(registFlg) {
            Intent intent = new Intent(this, ShowGameResultActivity.class);
            intent.putExtra("RESULTID", targetGameResult.getResultId());
            startActivity(intent);
        }

        finish();
    }

    private String inputCheck(){
        String errorMsg = "";

        // キーボードを閉じてフォーカスを移動
        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(scrollView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        LinearLayout adsView = (LinearLayout)findViewById(R.id.ads);
        adsView.requestFocus();

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
        String errorStr = ((EditText)findViewById(R.id.error)).getText().toString();
        String stealStr = ((EditText)findViewById(R.id.steal)).getText().toString();
        String stealOutStr = ((EditText)findViewById(R.id.stealout)).getText().toString();
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

        // 場所とチーム・相手チームは必須 → 場所は必須しないことにした
        if("".equals(myteamStr) || "".equals(otherteamStr)){
            noinputcheck = true;
        }

        // 日付のチェック
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
            Integer.parseInt(errorStr);
            Integer.parseInt(stealStr);
            Integer.parseInt(stealOutStr);
        } catch (NumberFormatException e) {
            numbercheck = true;
        }

        if(noinputcheck) {
            errorMsg = "入力されていない項目があります";
        }

        if(datecheck) {
            errorMsg = "日付が正しくありません";
        }

        if(numbercheck) {
            errorMsg = "数値の入力が正しくありません";
        }

        return errorMsg;
    }

    private void updateTargetGameResult(){
        // 入力値を取得
        String yearStr = ((EditText)findViewById(R.id.year)).getText().toString();
        String monthStr = ((EditText)findViewById(R.id.month)).getText().toString();
        String dayStr = ((EditText)findViewById(R.id.day)).getText().toString();
        String placeStr = ((EditText)findViewById(R.id.place)).getText().toString();
        String myteamStr = ((EditText)findViewById(R.id.myteam)).getText().toString();
        String otherteamStr = ((EditText)findViewById(R.id.otherteam)).getText().toString();
        String myscoreStr = ((EditText)findViewById(R.id.myscore)).getText().toString();
        String otherscoreStr = ((EditText)findViewById(R.id.otherscore)).getText().toString();
        RadioButton semeRadio1 = (RadioButton)findViewById(R.id.semeradio1);
        RadioButton semeRadio2 = (RadioButton)findViewById(R.id.semeradio2);
        int semeInt = 0;
        if(semeRadio1.isChecked()){
            semeInt = 1;
        } else if(semeRadio2.isChecked()){
            semeInt = 2;
        }
        int dajunInt = ((Spinner)findViewById(R.id.dajunspinner)).getSelectedItemPosition();
        int shubi1Int = ((Spinner)findViewById(R.id.shubi1spinner)).getSelectedItemPosition();
        int shubi2Int = ((Spinner)findViewById(R.id.shubi2spinner)).getSelectedItemPosition();
        int shubi3Int = ((Spinner)findViewById(R.id.shubi3spinner)).getSelectedItemPosition();
        // 守備が空の場合は詰める
        if(shubi1Int == 0 && shubi2Int == 0){
            shubi1Int = shubi3Int;
            shubi3Int = 0;
        } else if(shubi1Int == 0){
            shubi1Int = shubi2Int;
            shubi2Int = shubi3Int;
            shubi3Int = 0;
        } else if(shubi2Int == 0){
            shubi2Int = shubi3Int;
            shubi3Int = 0;
        }
        String datenStr = ((EditText)findViewById(R.id.daten)).getText().toString();
        String tokutenStr = ((EditText)findViewById(R.id.tokuten)).getText().toString();
        String errorStr = ((EditText)findViewById(R.id.error)).getText().toString();
        String stealStr = ((EditText)findViewById(R.id.steal)).getText().toString();
        String stealOutStr = ((EditText)findViewById(R.id.stealout)).getText().toString();
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

        // targetGameResultを更新
        targetGameResult.setYear(Integer.parseInt(yearStr));
        targetGameResult.setMonth(Integer.parseInt(monthStr));
        targetGameResult.setDay(Integer.parseInt(dayStr));
        targetGameResult.setPlace(placeStr);
        targetGameResult.setMyteam(myteamStr);
        targetGameResult.setOtherteam(otherteamStr);
        targetGameResult.setMyscore(Integer.parseInt(myscoreStr));
        targetGameResult.setOtherscore(Integer.parseInt(otherscoreStr));
        targetGameResult.setSeme(semeInt);
        targetGameResult.setDajun(dajunInt);
        targetGameResult.setShubi1(shubi1Int);
        targetGameResult.setShubi2(shubi2Int);
        targetGameResult.setShubi3(shubi3Int);
        targetGameResult.setDaten(Integer.parseInt(datenStr));
        targetGameResult.setTokuten(Integer.parseInt(tokutenStr));
        targetGameResult.setError(Integer.parseInt(errorStr));
        targetGameResult.setSteal(Integer.parseInt(stealStr));
        targetGameResult.setStealOut(Integer.parseInt(stealOutStr));
        targetGameResult.setMemo(memoStr);
    }

    private void addButton() {
        showResultPicker1(NO_TARGET);
//        showResultPicker(NO_TARGET);
    }

    private void modifyButton(){
        showSelectResultPickerForModify();
    }

    private void deleteButton(){
        showSelectResultPickerForDelete();
    }

    private void showResultPicker1(int target){
        targetBattingResult = target;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("打席の結果を選択");
        builder.setItems(BattingResult.PICKER1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(BattingResult.NEEDS_POSITION[i]){
                    showResultPicker2(i);
                } else {
                    makeBattingResult(i);
                }
            }
        });
        builder.show();
    }

    private void showResultPicker2(final int result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("打席の結果を選択");
        builder.setItems(BattingResult.PICKER2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                makeBattingResult(result, i);
            }
        });
        builder.show();
    }

    private void makeBattingResult(int picker1){
        BattingResult battingResult = null;
        if(picker1 == 0){
            battingResult = new BattingResult(12); // 三振
        } else if(picker1 == 1){
            battingResult = new BattingResult(14); // 四球
        } else if(picker1 == 2){
            battingResult = new BattingResult(15); // 死球
        } else if(picker1 == 16){
            battingResult = new BattingResult(13); // 振り逃げ
        } else if(picker1 == 17){
            battingResult = new BattingResult(16); // 打撃妨害
        }

        if(battingResult != null) {
            updateBattingResult(battingResult);
        }
    }

    private void makeBattingResult(int picker1, int picker2){
        int position = picker1 - 2; // -2するとpositionの値になる

        int result = 0;
        if(picker2 == 11){
            result = 17; // 併殺の場合のみ固定
        } else {
            result = picker2 + 1; // 併殺以外は+1するとresultの値になる
        }

        updateBattingResult(new BattingResult(position, result));
    }

    /*
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
*/

    private void showSelectResultPickerForModify() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("変更する結果を選択");
        builder.setItems(makeResultStringList(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // showResultPicker(i);
                showResultPicker1(i);
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
            String pickerString = "第"+(i+1)+"打席 "+battingResult.getBattingResultString(false);
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
            textView2.setText(Html.fromHtml(battingResult.getBattingResultString(true)));
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
