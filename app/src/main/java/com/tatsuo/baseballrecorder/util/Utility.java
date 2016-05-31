package com.tatsuo.baseballrecorder.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

}
