package com.tatsuo.baseballrecorder;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.tatsuo.baseballrecorder.domain.ConfigManager;

/**
 * Created by tatsuo on 2015/08/26.
 */
public class CommonAdsActivity extends AppCompatActivity {

    // protected static ADG adg = null;
    protected AdView gadView = null;
    protected boolean isAds = false;

    public static final String GAD_APP_ID = "ca-app-pub-6719193336347757~3537491855";
    public static final String GAD_UNIT_ID = "ca-app-pub-6719193336347757/5014225053";
    public static final String GAD_INT_UNIT_ID = "ca-app-pub-6719193336347757/6490958256";

    private static final int INTERSTITIAL_FREQ = 25; // インタースティシャル広告の表示割合（％）

    // protected ADGInterstitial interstitial = null;
    protected InterstitialAd interstitial;
    protected static boolean showInterstitial = false;

    protected void makeAdsView(){
        if(ConfigManager.isShowAds() == false){
            return;
        }

        // 広告生成
        LinearLayout adsLayout = (LinearLayout)findViewById(R.id.ads);
        if(adsLayout != null && adsLayout.getVisibility() == View.INVISIBLE) {
            adsLayout.setVisibility(View.VISIBLE);
            isAds = false;
            gadView = new AdView(this);
            gadView.setAdUnitId(GAD_UNIT_ID);
            gadView.setAdSize(AdSize.BANNER);
            gadView.setAdListener(new GadListener());
            adsLayout.addView(gadView);

            AdRequest adRequest = new AdRequest.Builder().build();
            gadView.loadAd(adRequest);
        }
    }

    protected void prepareInterstitial() {
        if(ConfigManager.isShowAds() == false){
            return;
        }

        if(interstitial == null) {
            interstitial = new InterstitialAd(this);
            interstitial.setAdUnitId(GAD_INT_UNIT_ID);
            interstitial.setAdListener(new GadInterstitialListener());
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);

        // Log.e("PrepareInterstitial", "PrepareInterstitial : "+getClass().getName());

        // interstitial = new ADGInterstitial(this);
        // interstitial.setLocationId("38148");
        // interstitial.setLocationId("26803"); // テストID
        // interstitial.setEnableTestMode(true); // テストモード
        // interstitial.setSpan(100, true);
        // interstitial.setSpan(25, true);
        // interstitial.setAdListener(new InterstitialListener());
        // interstitial.preload();
    }

    protected void showInterstitial(){
        if(ConfigManager.isShowAds() == false){
            return;
        }

        // Log.e("ShowInterstitial", "ShowInterstitial : "+ showInterstitial + " " + getClass().getName());
        if(showInterstitial == false || interstitial == null){
            return;
        }

        showInterstitial = false;

        // インタースティシャル広告を表示するか判定
        int rand = (int)(Math.random() * 100);
        if(rand >= INTERSTITIAL_FREQ){
            return;
        }

        if(interstitial.isLoaded()) {
            interstitial.show();
        } else {
            prepareInterstitial();
        }
    }

    @Override
    protected void onResume() {
        // ローテーション再開
        if (gadView != null) {
             gadView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        // ローテーション停止
        if (gadView != null) {
            gadView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(gadView != null){
            gadView.destroy();
        }
        super.onDestroy();
    }

    protected void adjustViewHeight() {
        // 必要時Override
    }

    class GadListener extends com.google.android.gms.ads.AdListener {
        public void onAdLoaded() {
            if(isAds == false) {
                isAds = true;
                adjustViewHeight();
            }
        }

        public void onAdFailedToLoad(int errorCode) {}
        public void onAdOpened() {}
        public void onAdClosed() {}
        public void onAdLeftApplication() {}
    }

    class GadInterstitialListener extends com.google.android.gms.ads.AdListener {
        public void onAdLoaded() {}
        public void onAdFailedToLoad(int errorCode) {}
        public void onAdOpened() {}
        public void onAdClosed() {
            prepareInterstitial();
        }
        public void onAdLeftApplication() {}
    }

/*
    class AdListener extends ADGListener {
        private static final String _TAG = "ADGListener";
        @Override
        public void onReceiveAd() {
            if(isAds == false) {
                isAds = true;
                adjustViewHeight();
            }
        }
        @Override
        public void onFailedToReceiveAd(ADGConsts.ADGErrorCode code) {
            // 不通とエラー過多のとき以外はリトライ
            switch (code) {
                case EXCEED_LIMIT:
                case NEED_CONNECTION:
                    break;
                default:
                    if(adg != null) {
                        adg.start();
                    }
                    break;
            }
        }
        @Override
        public void onOpenUrl() {

        }
    }

    class InterstitialListener extends ADGInterstitialListener {
        @Override
        public void onReceiveAd() {
            // Log.e("ADG", "onReceiveAd");
        }

        @Override
        public void onFailedToReceiveAd(ADGConsts.ADGErrorCode code) {
            // Log.e("ADG", "onFailedToReceiveAd");
            // 不通とエラー過多のとき以外はリトライ
            switch (code) {
                case EXCEED_LIMIT:
                case NEED_CONNECTION:
                    break;
                default:
                    if(interstitial != null) {
                        interstitial.preload();
                    }
                    break;
            }
        }

        @Override
        public void onCloseInterstitial() {
            // Log.e("ADG", "onCloseInterstitial");
        }

        @Override
        public void onOpenUrl() {
            // Log.e("ADG", "onOpenUrl");
            interstitial.dismiss();
        }
    }
*/


}
