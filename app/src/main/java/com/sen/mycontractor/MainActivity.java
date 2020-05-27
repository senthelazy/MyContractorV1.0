package com.sen.mycontractor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {
    private ImageView logoIv;
    private static int SPLASH_TIME_OUT = 1000;
    private final int MY_PERMISSION_REQUEST_CODE = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoIv = (ImageView) findViewById(R.id.logoIv);
        Animation welcomeAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.welcome);
        logoIv.startAnimation(welcomeAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, SelectionOfStatus.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }
    }
}
