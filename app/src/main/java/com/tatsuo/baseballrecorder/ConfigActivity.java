package com.tatsuo.baseballrecorder;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tatsuo.baseballrecorder.billing.IabHelper;
import com.tatsuo.baseballrecorder.billing.IabResult;
import com.tatsuo.baseballrecorder.billing.Inventory;
import com.tatsuo.baseballrecorder.billing.Purchase;
import com.tatsuo.baseballrecorder.domain.ConfigManager;
import com.tatsuo.baseballrecorder.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tatsuo on 2016/05/08.
 */
public class ConfigActivity extends CommonAdsActivity implements View.OnClickListener {

    private IabHelper iabHelper;
    private boolean purchasedRemoveAds = false;

    private static final String PRODUCT_ID_REMOVE_ADS = "remove_ads";
    private static final String BASE64_ENCODED_PUBLIC_KEY
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApNlSgcpuQwmm6kLppz6n+lc1bcmsQHhq3A9/hywZ630F+oXuKOOkT3kj7xs8tGg9F77GUJhCTCtgPnfZj9JCTiqFSS8mg/OyPvkgQKr21Bs9C5VZmk67zhb3zVJbWqCUNS5NkNK6aD92vTGh/gBiBy9Z6HMAwrCqLPxg/hFISO1rWavnHijb5hLsz4au6DA/Y42SylMht0EzB6Zs3IpcGp6XGQmu+NN6pYNghCX5BxzVBPxk0e933qQBqB9rLwI0NuthxFd1bQK0Pn0lttQ2GTvXBiBiotQheu1aJwW9ds8JJM0g8nL3ty1hpdYHtiCj5rlKlGOPihJSE/wCKhpOxwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Button removeAdsButton = (Button)findViewById(R.id.remove_ads_button);
        removeAdsButton.setOnClickListener(this);

        TextView removeAdsText = (TextView)findViewById(R.id.removed_ads);

        if(ConfigManager.isShowAds() == true) {
            iabHelper = new IabHelper(this, BASE64_ENCODED_PUBLIC_KEY);
            iabHelper.enableDebugLogging(true);
            iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // 失敗した時の処理
                        // Log.e("IAB", "セットアップ失敗");
                        return;
                    }

                    // Log.e("IAB", "セットアップ成功。購入済みアイテムを取得する");
                    iabHelper.queryInventoryAsync(inventoryListener);
                }
            });
            removeAdsButton.setVisibility(View.VISIBLE);
            removeAdsText.setVisibility(View.GONE);
        } else {
            removeAdsButton.setVisibility(View.GONE);
            removeAdsText.setVisibility(View.VISIBLE);
        }

        makeConfigView();

        makeAdsView();
    }

    @Override
    protected void adjustViewHeight() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        int height = dm.heightPixels;
        float density = dm.density;

        int header = 81; //dp
        int headerpx = (int) (header * density + 0.5f); // 0.5fは四捨五入用

        int ads = isAds && ConfigManager.isShowAds() ? 50 : 0; //dp
        int adspx = (int) (ads * density + 0.5f);

        int viewHeight = height - (headerpx + adspx);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, viewHeight));
    }

    protected void makeConfigView() {
        boolean calc7Flg = ConfigManager.loadCalc7Flg();
        CheckBox calc7Check = (CheckBox)findViewById(R.id.calc7_check);
        calc7Check.setChecked(calc7Flg);
        calc7Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                boolean checked = checkBox.isChecked();
                ConfigManager.saveCalc7Flg(checked);
            }
        });

        PackageManager pm = getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        TextView versionText = (TextView)findViewById(R.id.version_text);
        versionText.setText("草野球日記 ベボレコ ver" + versionName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.remove_ads_button:
                removeAds();
                break;
        }
    }

    private void removeAds(){
        if(purchasedRemoveAds == true){
            // 広告削除を購入済みだった場合、広告表示を消す
            // Log.e("IAB", "広告削除を購入済みです。");
            ConfigManager.saveShowAds(false);
            Button removeAdsButton = (Button)findViewById(R.id.remove_ads_button);
            TextView removeAdsText = (TextView)findViewById(R.id.removed_ads);
            removeAdsButton.setVisibility(View.GONE);
            removeAdsText.setVisibility(View.VISIBLE);

            adjustViewHeight();

            Utility.showAlertDialog(this, "すでに広告削除を購入済みであることが確認できたため広告を削除します。");
            return;
        }

        // 広告削除の購入手続き
        try {
            List additionalSkuList = new ArrayList();
            additionalSkuList.add(PRODUCT_ID_REMOVE_ADS);
            Inventory inventory = iabHelper.queryInventory(true, additionalSkuList);
            inventory.getSkuDetails(PRODUCT_ID_REMOVE_ADS).getPrice();

            iabHelper.launchPurchaseFlow(this, PRODUCT_ID_REMOVE_ADS, 10001, purchaseFinishedListener, "12345678");
        } catch(Exception e){
            Utility.showAlertDialog(this, "購入手続きを開始できません。端末にGoogleIDが設定されていることを確認の上、しばらくしてから再度お試しください。");
            // Log.e("IAB","Exception",e);
        }

    }

    // 購入済みアイテムの取得結果の受け取り用メソッドを作成
    IabHelper.QueryInventoryFinishedListener inventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // Log.e("IAB", "購入済みアイテムの取得完了");

            if (iabHelper == null) {
                return;
            }

            if (result.isFailure()) {
                // Log.e("IAB", "購入済みアイテムの取得失敗");
                return;
            }

            // Log.e("IAB", "購入済みアイテムの取得成功");

            // 購入済みアイテムの確認
            Purchase purchase = inventory.getPurchase(PRODUCT_ID_REMOVE_ADS);
            if (purchase != null) {
                purchasedRemoveAds = true;
                // Log.e("IAB", "商品を購入済みです。");

                // テストコード・購入済みを解除
                // iabHelper.consumeAsync(purchase, consumeFinishedListener);
            }
        }
    };

    // 購入結果の受け取り用メソッド
    IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // Log.e("IAB", "購入完了 result:" + result + ", purchase: " + purchase);

            if (iabHelper == null){
                return;
            }

            if (result.isFailure()) {
                // Log.e("IAB","購入失敗");
                Utility.showAlertDialog(ConfigActivity.this, "購入手続きに失敗しました。しばらくしてから再度お試しください。");
                return;
            }

            // Log.e("IAB", "購入成功");

            if (purchase.getSku().equals(PRODUCT_ID_REMOVE_ADS)) {
                // Log.e("IAB", "あなたの商品：" + PRODUCT_ID_REMOVE_ADS + "を購入しました。");
                // Log.e("IAB","orderIdは：" + purchase.getOrderId());
                // Log.e("IAB", "INAPP_PURCHASE_DATAのJSONは：" + purchase.getOriginalJson());
                
                // 広告表示を消す
                ConfigManager.saveShowAds(false);
                Button removeAdsButton = (Button)findViewById(R.id.remove_ads_button);
                TextView removeAdsText = (TextView)findViewById(R.id.removed_ads);
                removeAdsButton.setVisibility(View.GONE);
                removeAdsText.setVisibility(View.VISIBLE);

                adjustViewHeight();

                Utility.showAlertDialog(ConfigActivity.this, "広告削除を購入しました。広告を削除します。");
            }
        }
    };

    // テストコード
    /*
    IabHelper.OnConsumeFinishedListener consumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                // provision the in-app purchase to the user
                // Log.e("IAB", "消費成功 result:" + result.toString());
            } else {
                // handle error
                // Log.e("IAB", "消費失敗");
            }
        }
    };
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(iabHelper == null || iabHelper.handleActivityResult(requestCode, resultCode, data) == false){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
