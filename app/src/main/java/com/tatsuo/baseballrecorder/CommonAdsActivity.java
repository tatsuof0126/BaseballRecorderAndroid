package com.tatsuo.baseballrecorder;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;
import com.tatsuo.baseballrecorder.domain.ConfigManager;

/**
 * Created by tatsuo on 2015/08/26.
 */
public class CommonAdsActivity extends AppCompatActivity {

    protected static ADG adg;
    protected static boolean isAds = false;

    protected void makeAdsView(){
        if(ConfigManager.showAds == false){
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
                    adg.start();
                    break;
            }
        }
        @Override
        public void onOpenUrl() {

        }
    }

    protected void adjustViewHeight() {
        // 必要時Override
    }

}
