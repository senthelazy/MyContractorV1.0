package com.sen.mycontractor.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;


public class BaseActivity extends Activity {
    protected boolean isDestroy;

    private boolean clickable=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDestroy=false;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy=true;
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        clickable=true;
    }


    protected boolean isClickable(){
        return  clickable;
    }


    protected void lockClick(){
        clickable=false;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if(isClickable()) {
            lockClick();
            super.startActivityForResult(intent, requestCode,options);
        }
    }
}
