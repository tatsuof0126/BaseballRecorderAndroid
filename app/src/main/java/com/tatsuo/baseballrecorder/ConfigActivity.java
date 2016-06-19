package com.tatsuo.baseballrecorder;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tatsuo.baseballrecorder.domain.ConfigManager;

/**
 * Created by tatsuo on 2016/05/08.
 */
public class ConfigActivity extends CommonAdsActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

       //  ((Button)findViewById(R.id.image_share_button)).setOnClickListener(this);

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

        int ads = isAds ? 50 : 0; //dp
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
        versionText.setText("草野球日記 ベボレコ ver"+versionName);

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
            case R.id.change_button:

                break;
        }
    }


}
