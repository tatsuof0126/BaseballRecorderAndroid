package com.tatsuo.baseballrecorder.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tatsuo on 2015/08/15.
 */
public class Utility {

    public static String replaceComma(String string) {
        return string.replaceAll(",", "、");
    }

    public static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static String getFloatString3(float floatValue){
        if(Float.isNaN(floatValue) || Float.isInfinite(floatValue)){
            return ".---";
        }

        String retString = String.format("%.3f",floatValue);

        // 0.xxxの場合は先頭の0を削る
        if(floatValue < 1.0f){
            retString = retString.substring(1);
        }

        return retString;
    }

    public static String getFloatString2(float floatValue){
        if(Float.isNaN(floatValue) || Float.isInfinite(floatValue)){
            return "-.--";
        }

        String retString = String.format("%.2f",floatValue);

        return retString;
    }


    public static void saveCapture(View view, File file) {
        // キャプチャを撮る
        Bitmap capture = getViewCapture(view);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            // 画像のフォーマットと画質と出力先を指定して保存
            capture.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ie) {
                    fos = null;
                }
            }
        }
    }

    public static Bitmap getViewCapture(View view) {
        view.setDrawingCacheEnabled(true);

        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // Viewのキャプチャを取得
        Bitmap cache = view.getDrawingCache();
        if(cache == null){
            return null;
        }

        Bitmap screenShot = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);

        return screenShot;
    }

    public static void showAlertDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    public static String getStringFromFile(String filePath){
        if(filePath == null || "".equals(filePath)){
            return null;
        }

        File file = new File(filePath);
        if(file.exists() == false){
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));
            String string = null;
            while ((string = reader.readLine()) != null) {
                stringBuilder.append(string);
                stringBuilder.append("\n");
            }
        } catch (IOException ioe){
            return null;
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException ioe){}
        }

        return stringBuilder.toString();
    }

    public static boolean isToday(Date date){
        if(date == null){
            return false;
        }

        Calendar nowCal = Calendar.getInstance();
        Calendar checkCal = Calendar.getInstance();
        checkCal.setTimeInMillis(date.getTime());

        if(nowCal.get(Calendar.YEAR) == checkCal.get(Calendar.YEAR) &&
                nowCal.get(Calendar.MONTH) == checkCal.get(Calendar.MONTH) &&
                nowCal.get(Calendar.DAY_OF_MONTH) == checkCal.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }

        return false;
    }

}
