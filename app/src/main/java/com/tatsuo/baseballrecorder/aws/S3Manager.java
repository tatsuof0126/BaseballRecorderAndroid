package com.tatsuo.baseballrecorder.aws;

import android.app.Application;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tatsuo.baseballrecorder.BaseballRecorderApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tatsuo on 2017/02/02.
 */

public class S3Manager {

    public static final String S3_BUCKET = "baseballrecorder";

    public static boolean S3Upload(String prefix, String fileName, File file){
        AWSCredentials credentials = new BasicAWSCredentials(S3Define.AWS_KEY, S3Define.AWS_SECRET);
        AmazonS3 s3 = new AmazonS3Client(credentials);

        TransferUtility transferUtility = new TransferUtility(s3, BaseballRecorderApplication.getInstance());

        final S3Result s3Result = new S3Result();
        s3Result.setStatus(S3Result.STATUS_CONNECTING);

        TransferObserver observer = transferUtility.upload(S3_BUCKET, prefix+fileName, file);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                switch (state) {
                    case COMPLETED:
                        // ダウンロード完了時
                        s3Result.setStatus(S3Result.STATUS_COMPLETE);
                        break;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                s3Result.setStatus(S3Result.STATUS_ERROR);
            }
        });

        while(s3Result.getStatus() == S3Result.STATUS_CONNECTING) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        }

        if(s3Result.getStatus() == S3Result.STATUS_ERROR){
            return false;
        }

        return true;
    }

    public static List<String> S3GetFileList(String prefix){
        List<String> fileList = new ArrayList<String>();

        AWSCredentials credentials = new BasicAWSCredentials(S3Define.AWS_KEY, S3Define.AWS_SECRET);
        AmazonS3 s3 = new AmazonS3Client(credentials);

        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(S3_BUCKET)
                .withPrefix(prefix);
        ObjectListing list = s3.listObjects(request);

        // フォルダ一覧
        // List<String> folders = list.getCommonPrefixes();
        // for(String folder : folders){
        //   Log.e("FOLDERS", folder);
        // }

        List<S3ObjectSummary> objects = list.getObjectSummaries();
        for(S3ObjectSummary object : objects) {
            fileList.add(object.getKey());
            Log.e("OBJECTS", object.getKey());
        }

        return fileList;
    }

    public static String S3Download(String prefix, String key) {
        return S3Download(prefix + key);
    }

    public static String S3Download(String key){
        AWSCredentials credentials = new BasicAWSCredentials(S3Define.AWS_KEY, S3Define.AWS_SECRET);
        AmazonS3 s3 = new AmazonS3Client(credentials);

        Application application = BaseballRecorderApplication.getInstance();
        final S3Result s3Result = new S3Result();
        s3Result.setStatus(S3Result.STATUS_CONNECTING);

        TransferUtility transferUtility = new TransferUtility(s3, application);

        File file = application.getFileStreamPath("tempfile.tmp");
        file.delete();

        TransferObserver observer = transferUtility.download(S3_BUCKET, key, file);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                switch (state) {
                    case COMPLETED:
                        // ダウンロード完了時
                        s3Result.setStatus(S3Result.STATUS_COMPLETE);
                        break;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                s3Result.setStatus(S3Result.STATUS_ERROR);
            }
        });

        while(s3Result.getStatus() == S3Result.STATUS_CONNECTING) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        }

        if(s3Result.getStatus() == S3Result.STATUS_ERROR){
            return null;
        }

        return file.getAbsolutePath();
    }

}
