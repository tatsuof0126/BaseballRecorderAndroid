package com.tatsuo.baseballrecorder;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by tatsuo on 2015/08/30.
 */
public class BaseballRecorderApplication extends Application {

    private Tracker mTracker;

    private static BaseballRecorderApplication instance;

    public BaseballRecorderApplication() {
        super();
        instance = this;
    }

    public synchronized Tracker getTracker() {
        if(mTracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

    public static BaseballRecorderApplication getInstance() {
        return instance;
    }

}
