package com.tatsuo.baseballrecorder;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;
import com.socdm.d.adgeneration.interstitial.ADGInterstitial;
import com.socdm.d.adgeneration.interstitial.ADGInterstitialListener;
import com.tatsuo.baseballrecorder.domain.ConfigManager;

/**
 * Created by tatsuo on 2015/08/26.
 */
public class CommonAdsActivity extends AppCompatActivity {

    protected static ADG adg = null;
    protected static boolean isAds = false;

    protected ADGInterstitial interstitial = null;
    protected static boolean showInterstitial = false;

    protected void makeAdsView(){
        if(ConfigManager.isShowAds() == false){
            return;
        }

        // 広告生成
        LinearLayout adsLayout = (LinearLayout)findViewById(R.id.ads);
        if(adsLayout.getVisibility() == View.INVISIBLE) {
            adsLayout.setVisibility(View.VISIBLE);
            isAds = false;
            adg = new ADG(this);
            adg.setLocationId("27341");
            adg.setAdFrameSize(ADG.AdFrameSize.SP);
            adg.setAdListener(new AdListener());
            adg.setReloadWithVisibilityChanged(false);
            adg.setFillerRetry(false);
            adsLayout.addView(adg);
            adg.start();
        }
    }

    protected void prepareInterstitial() {
        if(ConfigManager.isShowAds() == false){
            return;
        }

        // Log.e("PrepareInterstitial", "PrepareInterstitial : "+getClass().getName());

        interstitial = new ADGInterstitial(this);
        interstitial.setLocationId("38148");
        // interstitial.setLocationId("26803"); // テストID
        // interstitial.setEnableTestMode(true); // テストモード
        // interstitial.setSpan(100, true);
        interstitial.setSpan(25, true);
        interstitial.setAdListener(new InterstitialListener());
        interstitial.preload();
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
        boolean showAd = interstitial.show();
        // Log.e("ShowInterstitial","showAd result " + showAd);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ローテーション再開
        if (adg != null) {
            adg.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // ローテーション停止
        if (adg != null) {
            adg.stop();
        }
    }

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

    protected void adjustViewHeight() {
        // 必要時Override
    }

}
