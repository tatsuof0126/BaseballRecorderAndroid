package com.tatsuo.baseballrecorder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        TextView migrationCdText = (TextView) findViewById(R.id.migration_cd);
        String migrationCd = ConfigManager.getLastMigrationCd();
        migrationCdText.setText("機種変更コード：" + migrationCd);

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
            Toast.makeText(this, "１日に発行できる機種変更コードは５個までです。", Toast.LENGTH_SHORT).show();
            return;
        }

        // 一度全件を再保存（バージョンを最新化するため）
        for(GameResult gameResult : gameResultList){
            GameResultManager.saveGameResult(this, gameResult);
        }

        SaveServerTask task = new SaveServerTask();
        task.execute();
    }

    private String getMigrationCd(){
        int migrationCdInt = 0;
        while(true){
            migrationCdInt = (int) (Math.random() * 90000000) + 10000000;

            List<String> filelist = S3Manager.S3GetFileList(migrationCdInt+"/");

            if(filelist != null && filelist.size() == 0){
                break;
            }
        }

        return ""+migrationCdInt;
    }

    private void updateS3Info(boolean showDialog){
        LoadS3InfoTask task = new LoadS3InfoTask(showDialog);
        task.execute();
    }

    class SaveServerTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SaveServerActivity.this);
            dialog.setMessage("通信中");
            dialog.show();
            return;
        }

        @Override
        protected Void doInBackground(Void... temp) {
            String migrationCd = getMigrationCd();

            List<String> selectedFileList = new ArrayList<String>();

            String[] fileList = fileList();
            for(String fileName : fileList) {
                if(fileName != null && fileName.contains("gameresult")){
                    selectedFileList.add(fileName);
                }
            }

            for(String fileName : selectedFileList) {
                File file = getFileStreamPath(fileName);
                S3Manager.S3Upload(migrationCd+"/", fileName, file);
            }

            Date date = ConfigManager.getLastMigrationDate();
            int count = ConfigManager.getMigrationCount();
            if(Utility.isToday(date)){
                count++;
            } else {
                count = 1;
            }

            ConfigManager.setLastMigrationCd(migrationCd);
            ConfigManager.setLastMigrationDate(new Date());
            ConfigManager.setMigrationCount(count);

            return null;
        }

        @Override
        protected void onPostExecute(Void temp) {
            updateLastMigrationInfo();

            if (dialog != null && this.dialog.isShowing()) {
                dialog.dismiss();
            }

            Toast.makeText(SaveServerActivity.this, "機種変更コードを発行しました。", Toast.LENGTH_LONG).show();
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
