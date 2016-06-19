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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;

/**
 * Created by tatsuo on 2016/05/08.
 */
public class InputPitchingResultActivity extends CommonAdsActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private GameResult targetGameResult = null;
    private int inning = 0;
    private int inning2 = 0;
    private int sekinin = 0;

    static final int RESULT_SAVED_REGIST = 101;
    static final int RESULT_SAVED_UPDATE = 102;
    static final int RESULT_TO_BATTING = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pitching_result);

        ((Button)findViewById(R.id.inning_input_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.inning_change_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.sekinin_input_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.sekinin_change_button)).setOnClickListener(this);
        ((Button)findViewById(R.id.save_button)).setOnClickListener(this);

        Intent intent = getIntent();
        targetGameResult = (GameResult)intent.getSerializableExtra("GAME_RESULT");
        inning = targetGameResult.getInning();
        inning2 = targetGameResult.getInning2();
        sekinin = targetGameResult.getSekinin();

        makePitchingResultView();

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
        getMenuInflater().inflate(R.menu.menu_input_pitching_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_batting:
                moveBattingActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void makePitchingResultView(){
        showInning();

        EditText hiandaText = (EditText)findViewById(R.id.hianda);
        hiandaText.setText(Integer.toString(targetGameResult.getHianda()));
        hiandaText.setOnFocusChangeListener(this);
        hiandaText.setNextFocusDownId(R.id.hihomerun);

        EditText hihomerunText = (EditText)findViewById(R.id.hihomerun);
        hihomerunText.setText(Integer.toString(targetGameResult.getHihomerun()));
        hihomerunText.setOnFocusChangeListener(this);
        hihomerunText.setNextFocusDownId(R.id.dassanshin);

        EditText dassanshinText = (EditText)findViewById(R.id.dassanshin);
        dassanshinText.setText(Integer.toString(targetGameResult.getDassanshin()));
        dassanshinText.setOnFocusChangeListener(this);
        dassanshinText.setNextFocusDownId(R.id.yoshikyu);

        EditText yoshikyuText = (EditText)findViewById(R.id.yoshikyu);
        yoshikyuText.setText(Integer.toString(targetGameResult.getYoshikyu()));
        yoshikyuText.setOnFocusChangeListener(this);
        yoshikyuText.setNextFocusDownId(R.id.yoshikyu2);

        EditText yoshikyu2Text = (EditText)findViewById(R.id.yoshikyu2);
        yoshikyu2Text.setText(Integer.toString(targetGameResult.getYoshikyu2()));
        yoshikyu2Text.setOnFocusChangeListener(this);
        yoshikyu2Text.setNextFocusDownId(R.id.shitten);

        EditText shittenText = (EditText)findViewById(R.id.shitten);
        shittenText.setText(Integer.toString(targetGameResult.getShitten()));
        shittenText.setOnFocusChangeListener(this);
        shittenText.setNextFocusDownId(R.id.jisekiten);

        EditText jisekitenText = (EditText)findViewById(R.id.jisekiten);
        jisekitenText.setOnFocusChangeListener(this);
        jisekitenText.setText(Integer.toString(targetGameResult.getJisekiten()));

        CheckBox kantoCheck = (CheckBox)findViewById(R.id.kanto);
        kantoCheck.setChecked(targetGameResult.isKanto());

        EditText tamakazuText = (EditText)findViewById(R.id.tamakazu);
        tamakazuText.setOnFocusChangeListener(this);
        if(targetGameResult.getTamakazu() == GameResult.TAMAKAZU_NONE) {
            tamakazuText.setText("---");
        } else {
            tamakazuText.setText(Integer.toString(targetGameResult.getTamakazu()));
        }

        showSekinin();
    }

    private void showInning(){
        TextView inningStr = (TextView)findViewById(R.id.inningstr);
        inningStr.setText(GameResult.getInningString(inning, inning2));
        Button inningInputButton = (Button)findViewById(R.id.inning_input_button);
        Button inningChangeButton = (Button)findViewById(R.id.inning_change_button);
        if(inning == 0 && inning2 == 0){
            inningInputButton.setVisibility(View.VISIBLE);
            inningChangeButton.setVisibility(View.GONE);
        } else {
            inningInputButton.setVisibility(View.GONE);
            inningChangeButton.setVisibility(View.VISIBLE);
        }
    }

    private void showSekinin(){
        TextView sekininStr = (TextView)findViewById(R.id.sekininstr);
        sekininStr.setText(Html.fromHtml(GameResult.getSekininString(sekinin, true)));
        Button sekininInputButton = (Button)findViewById(R.id.sekinin_input_button);
        Button sekininChangeButton = (Button)findViewById(R.id.sekinin_change_button);
        if(sekinin == 0){
            sekininInputButton.setVisibility(View.VISIBLE);
            sekininChangeButton.setVisibility(View.GONE);
        } else {
            sekininInputButton.setVisibility(View.GONE);
            sekininChangeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.inning_input_button:
                showInningPickker();
                break;
            case R.id.inning_change_button:
                showInningPickker();
                break;
            case R.id.sekinin_input_button:
                showSekininPicker();
                break;
            case R.id.sekinin_change_button:
                showSekininPicker();
                break;
            case R.id.save_button:
                saveButton();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText targetText = (EditText)v;
        if(hasFocus == true) {
            // 数値系の項目で「0」「---」ならフォーカスがあたったとき空にする
            if("0".equals(targetText.getText().toString()) ||
                    "---".equals(targetText.getText().toString())){
                targetText.setText("");
            }
        } else {
            // 数値系の項目で空ならフォーカスがはずれたとき「0」「---」にする
            if("".equals(targetText.getText().toString())){
                if(targetText == findViewById(R.id.tamakazu)){
                    targetText.setText("---");
                } else {
                    targetText.setText("0");
                }
            }
        }
    }

    private void showInningPickker(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("投球回を選択");
        builder.setItems(GameResult.INNING_PICKER, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showInning2Pickker(i);
            }
        });
        builder.show();
    }

    private void showInning2Pickker(final int inn){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("投球回を選択");
        String inningStr = GameResult.INNING_PICKER[inn];
        String[] pickerStr = {inningStr+GameResult.INNING2_PICKER[0],inningStr+GameResult.INNING2_PICKER[1],
                inningStr+GameResult.INNING2_PICKER[2],inningStr+GameResult.INNING2_PICKER[3]};
        builder.setItems(pickerStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inning = inn;
                inning2 = i;
                showInning();
            }
        });
        builder.show();
    }


    private void showSekininPicker(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("結果を選択");
        builder.setItems(GameResult.SEKININ_PICKER, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sekinin = i;
                showSekinin();
            }
        });
        builder.show();
    }

    private void saveButton() {
        // 入力エラーがあればToastを出して次に進まない
        String errorMsg = inputCheck();
        if ("".equals(errorMsg) == false) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        // 投球回が未入力ならワーニングダイアログを出す
        String alertMsg = "試合結果を保存します。\nよろしいですか？";
        if (inning == 0 && inning2 == 0) {
            alertMsg = "投球回が空のため投手成績の入力内容はクリアされます。\n保存してよろしいですか？";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMsg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveGameResult();
            }
        });
        builder.setNegativeButton("キャンセル", null);
        builder.show();
    }

    private void saveGameResult() {
        updateGameResult();

        // 登録か更新かを判断
        boolean registFlg = (targetGameResult.getResultId() == GameResult.NON_REGISTED);

        GameResultManager.saveGameResult(this, targetGameResult);
        ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_ALL, true);

        if(registFlg){
            Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Button").setAction("Push").setLabel("投手成績入力画面―登録").build());
            Toast.makeText(this, "登録しました", Toast.LENGTH_LONG).show();
        } else {
            Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Button").setAction("Push").setLabel("投手成績入力画面―更新").build());
            Toast.makeText(this, "保存しました", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent();
        if(registFlg){
            intent.putExtra("RESULTID", targetGameResult.getResultId());
            setResult(RESULT_SAVED_REGIST, intent);
        } else {
            setResult(RESULT_SAVED_UPDATE, intent);
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
        String hiandaStr = ((EditText)findViewById(R.id.hianda)).getText().toString();
        String hihomerunStr = ((EditText)findViewById(R.id.hihomerun)).getText().toString();
        String dassanshinStr = ((EditText)findViewById(R.id.dassanshin)).getText().toString();
        String yoshikyuStr = ((EditText)findViewById(R.id.yoshikyu)).getText().toString();
        String yoshikyu2Str = ((EditText)findViewById(R.id.yoshikyu2)).getText().toString();
        String shittenStr = ((EditText)findViewById(R.id.shitten)).getText().toString();
        String jisekitenStr = ((EditText)findViewById(R.id.jisekiten)).getText().toString();
        String tamakazuStr = ((EditText)findViewById(R.id.tamakazu)).getText().toString();

        boolean blankcheck = false;
        boolean numbercheck = false;

        if("".equals(hiandaStr) || "".equals(hihomerunStr) || "".equals(dassanshinStr) ||
                "".equals(yoshikyuStr) || "".equals(yoshikyu2Str) || "".equals(shittenStr) ||
                "".equals(jisekitenStr) || "".equals(tamakazuStr)){
            blankcheck = true;
        }

        // 数値型のチェック
        try {
            Integer.parseInt(hiandaStr);
            Integer.parseInt(hihomerunStr);
            Integer.parseInt(dassanshinStr);
            Integer.parseInt(yoshikyuStr);
            Integer.parseInt(yoshikyu2Str);
            Integer.parseInt(shittenStr);
            Integer.parseInt(jisekitenStr);
            if("---".equals(tamakazuStr) == false) {
                Integer.parseInt(tamakazuStr);
            }
        } catch (NumberFormatException e) {
            numbercheck = true;
        }

        if(numbercheck) {
            errorMsg = "数値の入力が正しくありません";
        }

        if(blankcheck){
            errorMsg = "入力されていない項目があります";
        }

        return errorMsg;
    }

    private void updateGameResult(){
        // 投球回が未入力なら反映しない
        if(inning == 0 && inning2 == 0){
            targetGameResult.setInning(inning);
            targetGameResult.setInning2(inning2);
            targetGameResult.setHianda(0);
            targetGameResult.setHihomerun(0);
            targetGameResult.setDassanshin(0);
            targetGameResult.setYoshikyu(0);
            targetGameResult.setYoshikyu2(0);
            targetGameResult.setShitten(0);
            targetGameResult.setJisekiten(0);
            targetGameResult.setKanto(false);
            targetGameResult.setTamakazu(GameResult.TAMAKAZU_NONE);
            targetGameResult.setSekinin(0);
            return;
        }

        // 入力値を取得
        String hiandaStr = ((EditText)findViewById(R.id.hianda)).getText().toString();
        String hihomerunStr = ((EditText)findViewById(R.id.hihomerun)).getText().toString();
        String dassanshinStr = ((EditText)findViewById(R.id.dassanshin)).getText().toString();
        String yoshikyuStr = ((EditText)findViewById(R.id.yoshikyu)).getText().toString();
        String yoshikyu2Str = ((EditText)findViewById(R.id.yoshikyu2)).getText().toString();
        String shittenStr = ((EditText)findViewById(R.id.shitten)).getText().toString();
        String jisekitenStr = ((EditText)findViewById(R.id.jisekiten)).getText().toString();
        boolean kanto = ((CheckBox)findViewById(R.id.kanto)).isChecked();
        String tamakazuStr = ((EditText)findViewById(R.id.tamakazu)).getText().toString();

        // targetGameResultに反映
        targetGameResult.setInning(inning);
        targetGameResult.setInning2(inning2);
        targetGameResult.setHianda(Integer.parseInt(hiandaStr));
        targetGameResult.setHihomerun(Integer.parseInt(hihomerunStr));
        targetGameResult.setDassanshin(Integer.parseInt(dassanshinStr));
        targetGameResult.setYoshikyu(Integer.parseInt(yoshikyuStr));
        targetGameResult.setYoshikyu2(Integer.parseInt(yoshikyu2Str));
        targetGameResult.setShitten(Integer.parseInt(shittenStr));
        targetGameResult.setJisekiten(Integer.parseInt(jisekitenStr));
        targetGameResult.setKanto(kanto);
        if("---".equals(tamakazuStr)){
            targetGameResult.setTamakazu(GameResult.TAMAKAZU_NONE);
        } else {
            targetGameResult.setTamakazu(Integer.parseInt(tamakazuStr));
        }
        targetGameResult.setSekinin(sekinin);
    }

    private void moveBattingActivity() {
        // 入力エラーがあればToastを出して次に進まない
        String errorMsg = inputCheck();
        if("".equals(errorMsg) == false) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        // 投球回が未入力ならワーニングダイアログを出す
        if (inning == 0 && inning2 == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("投球回が空のため投手成績の入力内容はクリアされます。\nよろしいですか？");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {doMoveBattingActivity();
                }
            });
            builder.setNegativeButton("キャンセル", null);
            builder.show();
        } else {
            doMoveBattingActivity();
        }
    }

    private void doMoveBattingActivity() {
        updateGameResult();

        Intent intent = new Intent();
        intent.putExtra("GAME_RESULT", targetGameResult);
        setResult(RESULT_TO_BATTING, intent);
        finish();
    }

}
