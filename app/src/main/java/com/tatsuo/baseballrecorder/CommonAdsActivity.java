package com.tatsuo.baseballrecorder;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;
import com.tatsuo.baseballrecorder.domain.ConfigManager;

import jp.co.geniee.gnadsdk.video.GNAdVideo;

/**
 * Created by tatsuo on 2015/08/26.
 */
public class CommonAdsActivity extends AppCompatActivity implements GNAdVideo.GNAdVideoListener {

    protected static ADG adg;
    protected static boolean isAds = false;

    protected GNAdVideo videoAd = null;

    protected void makeAdsView(){
        if(ConfigManager.isShowAds(this) == false){
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

    protected void prepareVideoAds(){
        if(ConfigManager.isShowAds(this) == false){
            return;
        }

        // インタースティシャル広告（動画）をロード、表示率は30％にする
        videoAd = new GNAdVideo(this, 1070196);
        videoAd.setAlternativeInterstitialAppID(1070000);
        videoAd.setShowRate(30);
        videoAd.setListener(this);
        videoAd.load(this);
    }

    protected void showVideoAds(){
        if(ConfigManager.isShowAds(this) == false){
            return;
        }

        // インタースティシャル広告（動画）を表示
        if (videoAd != null && videoAd.isReady()) {
            videoAd.show(this);
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

    // インタースティシャル広告（動画）用の実装
    // 広告データの読み込みが完了した時に送られます。
    public void onGNAdVideoReceiveSetting() {
    }
    // ネットワークエラー等の原因で広告の読み込みに失敗した時に送られます。
    public void onGNAdVideoFailedToReceiveSetting() {
    }
    // 動画広告画面が閉じられる直後に送られます。
    public void onGNAdVideoClose() {
    }
    // 管理画面より、代替インタースティシャル広告画面に設置したボタンがタップされ、
    // インタースティシャル広告画面が閉じられる直後に送られます。
    // タップされたボタンの番号は、`nButtonIndex`パラメータで通知されます。
    public void onGNAdVideoButtonClick(int nButtonIndex) {
    }

}
