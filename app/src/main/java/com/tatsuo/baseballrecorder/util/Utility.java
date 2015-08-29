package com.tatsuo.baseballrecorder.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

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
        if(Float.isNaN(floatValue)){
            return ".---";
        }

        String retString = String.format("%.3f",floatValue);

        // 0.xxxの場合は先頭の0を削る
        if(floatValue < 1.0f){
            retString = retString.substring(1);
        }

        return retString;
    }

}
