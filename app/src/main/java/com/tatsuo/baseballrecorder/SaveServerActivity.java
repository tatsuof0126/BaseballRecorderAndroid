package com.tatsuo.baseballrecorder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tatsuo.baseballrecorder.aws.S3Manager;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.domain.GameResultManager;
import com.tatsuo.baseballrecorder.util.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tatsuo on 2017/02/07.
 */

public class SaveServerActivity extends CommonAdsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_server);

        Button saveServerButton = (Button)findViewById(R.id.save_server_button);
        saveServerButton.setOnClickListener(this);

        Button updateInfoButton = (Button)findViewById(R.id.update_button);
        updateInfoButton.setOnClickListener(this);

        updateLastMigrationInfo();
        checkUpdateS3Info();
    }

    private void updateLastMigrationInfo() {
        String migrationCd = ConfigManager.getLastMigrationCd();
        String migrationPassword = ConfigManager.getLastMigrationPassword();
        // 機種変更コード時代向けの対応（パスワードだけ空の場合は999999にする）
        if(migrationCd != null && "".equals(migrationCd) == false &&
                (migrationPassword == null || "".equals(migrationPassword))){
            migrationPassword = "999999";
        }

        TextView migrationCdText = (TextView) findViewById(R.id.migration_cd);
        migrationCdText.setText("ＩＤ：" + migrationCd);

        TextView migrationPasswordText = (TextView) findViewById(R.id.migration_password);
        migrationPasswordText.setText("パスワード：" + migrationPassword);

        TextView migrationDateText = (TextView) findViewById(R.id.migration_date);
        String migrationDateStr = "";
        Date migrationDate = ConfigManager.getLastMigrationDate();
        if (migrationDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            migrationDateStr = dateFormat.format(migrationDate);
        }
        migrationDateText.setText("発行日：" + migrationDateStr);
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

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.save_server_button:
                saveServer();
                break;
            case R.id.update_button:
                updateS3Info(true);
                break;
        }
    }

    private void saveServer(){
        List<GameResult> gameResultList = GameResultManager.loadGameResultList(this);
        if(gameResultList.size() == 0){
            Toast.makeText(this, "試合結果がありません。", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = ConfigManager.getMigrationCount();
        Date date = ConfigManager.getLastMigrationDate();
        if(Utility.isToday(date) && count >= 5){
            Toast.makeText(this, "１日にできるバックアップは５回までです。", Toast.LENGTH_SHORT).show();
            return;
        }

        // 最新バージョンでないデータがあれば保存し直し
        for(GameResult gameResult : gameResultList){
            if(gameResult.isLatestVersion() == false) {
                GameResultManager.saveGameResult(this, gameResult);
            }
        }

        Tracker tracker = ((BaseballRecorderApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button").setAction("Push").setLabel("データバックアップ画面―データバックアップ").build());

        SaveServerTask task = new SaveServerTask();
        task.execute();
    }

    private void updateS3Info(boolean showDialog){
        LoadS3InfoTask task = new LoadS3InfoTask(showDialog);
        task.execute();
    }

    class SaveServerTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SaveServerActivity.this);
            dialog.setMessage("通信中");
            dialog.show();
            return;
        }

        @Override
        protected Boolean doInBackground(Void... temp) {
            boolean uploadResult = true;

            String migrationCd = GameResultManager.getMigrationCd();
            String migrationPassword = GameResultManager.getMigrationPassword(migrationCd);

            // migrationCdやmigrationPasswordがnullの場合は何らかのエラーなので処理失敗にする
            if(migrationCd == null || migrationPassword == null){
                return false;
            }

            List<String> selectedFileList = new ArrayList<String>();

            String[] fileList = fileList();
            for(String fileName : fileList) {
                if(fileName != null && fileName.contains("gameresult")){
                    selectedFileList.add(fileName);
                }
            }

            for (String fileName : selectedFileList) {
                File file = getFileStreamPath(fileName);
                boolean result = S3Manager.S3Upload(migrationCd + "/", fileName, file);
                if (result == false) {
                    uploadResult = false;
                    break;
                }
            }

            // 全件アップロードに成功したらID・パスワードをConfigに書き込む
            if(uploadResult == true) {
                Date date = ConfigManager.getLastMigrationDate();
                int count = ConfigManager.getMigrationCount();
                if (Utility.isToday(date)) {
                    count++;
                } else {
                    count = 1;
                }

                ConfigManager.setLastMigrationCd(migrationCd);
                ConfigManager.setLastMigrationPassword(migrationPassword);
                ConfigManager.setLastMigrationDate(new Date());
                ConfigManager.setMigrationCount(count);
            }

            return uploadResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog != null && this.dialog.isShowing()) {
                dialog.dismiss();
            }

            if(result == true) {
                updateLastMigrationInfo();
                Toast.makeText(SaveServerActivity.this, "データをバックアップしました。\nIDとパスワードは画面を確認してください。", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SaveServerActivity.this, "データのバックアップに失敗しました。ネットワーク接続を確認して再度お試しください。", Toast.LENGTH_LONG).show();
            }
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
                dialog = new ProgressDialog(SaveServerActivity.this);
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
