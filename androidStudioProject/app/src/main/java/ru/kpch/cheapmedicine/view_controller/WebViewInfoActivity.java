package ru.kpch.cheapmedicine.view_controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.model.Drug;

public class WebViewInfoActivity extends AppActivity {

    InterstitialAd mInterstitialAd;

    WebView htmlContainer;
    boolean isAdmobisClosed=false;
    String parentActivity;

    private static String classNameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            classNameFrom=getIntent().getExtras().getString(IntentKeys.PARENT);
        } catch (NullPointerException e) {

        }

        setActivityActionBar(this, R.raw.doc_back);

        setContentView(R.layout.activity_drug_info);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id3));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                isAdmobisClosed =true;
            }
        });

        requestNewInterstitial();

        htmlContainer =(WebView)findViewById(R.id.wv_container);
        htmlContainer.getSettings().setBuiltInZoomControls(true);
        htmlContainer.getSettings().setJavaScriptEnabled(true);

        parentActivity=getIntent().getExtras().getString(IntentKeys.PARENT);
        String htmlContainerText=null;

        if(parentActivity.equals(AnalogsActivity.class.getName())){
            Drug selectedDrug=(Drug)getIntent().getExtras().getSerializable(IntentKeys.SELECTED_DRUG);
            htmlContainerText=selectedDrug.getNotice();
        }
        if(parentActivity.equals(MainActivity.class.getName())){
            htmlContainerText=appLogic.getAboutInfo();
        }
        htmlContainer.loadDataWithBaseURL(null, htmlContainerText, "text/html", "en_US", null);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (isAppWillBeClosed) {
            appLogic.clearAppCache();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isAdmobisClosed){
            isAdmobisClosed =false;
            onBackPressed();
        }
    }

    public void onClickHomeButton(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        else{
            onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            else{
                onBackPressed();
            }

            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName(classNameFrom));
        } catch (ClassNotFoundException e) {
            closeWithFatalError(this);
            return;
        }
        startActivity(intent);
        finish();
    }
}
