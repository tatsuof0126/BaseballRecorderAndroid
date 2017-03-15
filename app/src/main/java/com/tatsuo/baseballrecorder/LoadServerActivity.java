package com.tatsuo.baseballrecorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tatsuo.baseballrecorder.aws.S3Manager;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.util.Utility;

import java.util.Date;
import java.util.List;

/**
 * Created by tatsuo on 2017/02/07.
 */

public class LoadServerActivity  extends CommonAdsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_server);

        Button loadServerButton = (Button)findViewById(R.id.load_server_button);
        loadServerButton.setOnClickListener(this);

        Button updateInfoButton = (Button)findViewById(R.id.update_button);
        updateInfoButton.setOnClickListener(this);

        checkUpdateS3Info();
    }

    private void checkUpdateS3Info(){
        Date s3InfoUpdateDate = ConfigManager.getS3InfoUpdateDate();
        Date nowDate = new Date();

        // 一日以上更新していなければお知らせを再読込
        if(s3InfoUpdateDate == null || nowDate.getTime() - s3InfoUpdateDate.getTime() > 1000*60*60*24){
            updateS3Info(false);
        } else {
            updateS3InfoView();
        }
    }

    private void updateS3InfoView() {
        TextView infomationText = (TextView)findViewById(R.id.infomation);
        String s3Infomation = ConfigManager.getS3Info();
        infomationText.setText(s3Infomation);
    }

    private void updateS3Info(boolean showDialog){
        LoadS3InfoTask task = new LoadS3InfoTask(showDialog);
        task.execute();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.load_server_button:
                loadServer();
                break;
            case R.id.update_button:
                updateS3Info(true);
                break;
        }
    }

    private void loadServer(){
        // TextViewにフォーカスを移して、キーボードを隠す
        TextView textMessage = (TextView)findViewById(R.id.text_message);
        textMessage.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(textMessage.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        EditText migrationCdText = (EditText)findViewById(R.id.migration_cd);
        String migrationCd = migrationCdText.getText().toString();

        if(inputCheck() == false){
            return;
        }

        LoadServerTask task = new LoadServerTask(migrationCd);
        task.execute();
    }

    private boolean inputCheck(){
        TextView migrationCdText = (TextView)findViewById(R.id.migration_cd);
        String migrationCd = migrationCdText.getText().toString();

        if(migrationCd == null || "".equals(migrationCd)){
            Toast.makeText(this, "機種変更コードを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }

        int migrationCdInt = 0;
        try {
            migrationCdInt = Integer.parseInt(migrationCd);
        } catch (NumberFormatException nfe){}
        if(migrationCdInt < 10000000 || migrationCdInt > 99999999){
            Toast.makeText(this, "機種変更コードは8桁の数字を入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    class LoadServerTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;

        private String migrationCd;

        LoadServerTask(String migrationCd){
            this.migrationCd = migrationCd;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(LoadServerActivity.this);
            dialog.setMessage("通信中");
            dialog.show();
            return;
        }

        @Override
        protected String doInBackground(Void... temp) {
            List<String> filelist = S3Manager.S3GetFileList(migrationCd+"/");
            if(filelist == null){
                return "データの取得に失敗しました。ネットワーク接続を確認して再度お試しください。";
            }

            if(filelist.size() == 0){
                return "データが存在しません。機種変更コードが正しいかどうかを確認してください。";
            }

            int datacount = 0;
            int savecount = 0;

            for(String fileKey : filelist){
                if(fileKey == null || fileKey.contains("gameresult") == false){
                    continue;
                }

                String filePath = S3Manager.S3Download(fileKey);
                if(filePath == null){
                    continue;
                }

                String fileString = Utility.getStringFromFile(filePath);

                Log.e("DOWNLOAD KEY", fileKey);
                Log.e("DOWNLOAD FILE", fileString);

                GameResult gameResult = GameResult.makeGameResult(fileString);
                if(gameResult == null){
                    continue;
                }

                datacount++;

                // 重複登録チェック
                List<GameResult> gameResultList = GameResultManager.loadGameResultList(LoadServerActivity.this);
                String uuid = gameResult.getUuid();
                boolean exists = false;
                for(GameResult checkResult : gameResultList){
                    if(checkResult != null && uuid != null && uuid.equals(checkResult.getUuid())){
                        exists = true;
                    }
                }

                // 重複していなければ保存
                if(exists == false){
                    gameResult.setResultId(GameResult.NON_REGISTED);
                    GameResultManager.saveGameResult(LoadServerActivity.this, gameResult);
                    savecount++;
                }

            }

            // 保存が１件以上あれば一覧画面を更新する
            if(savecount > 0) {
                ConfigManager.saveUpdateGameResultFlg(ConfigManager.VIEW_ALL, true);
            }

            String message = savecount+"件のデータを取り込みました。";
            if(savecount != datacount){
                message = message + "\n"+(datacount-savecount)+"件は重複データのため取り込みませんでした。";
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            if (dialog != null && this.dialog.isShowing()) {
                dialog.dismiss();
            }

            Toast.makeText(LoadServerActivity.this, message, Toast.LENGTH_LONG).show();
        }

    }

    class LoadS3InfoTask extends AsyncTask<Void, Void, Void> {
        private boolean showDialog = false;
        private ProgressDialog dialog;

        LoadS3InfoTask(boolean showDialog){
            this.showDialog = showDialog;
        }

        @Override
        protected void onPreExecute() {
            if(showDialog) {
                dialog = new ProgressDialog(LoadServerActivity.this);
                dialog.setMessage("通信中");
                dialog.show();
            }
            return;
        }

        @Override
        protected Void doInBackground(Void... temp) {
            String s3InfoFilePath = S3Manager.S3Download("","info_android.txt");
            if(s3InfoFilePath == null){
                return null;
            }

            String infoFileString = Utility.getStringFromFile(s3InfoFilePath);
            if(infoFileString != null) {
                ConfigManager.setS3Info(infoFileString);
                ConfigManager.setS3InfoUpdateDate(new Date());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void temp) {
            updateS3InfoView();

            if (dialog != null && this.dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }

}
