package com.ose4g.pomodoroclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    Handler mHandler;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHandler = new Handler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,Pomodoro.class);
                startActivity(intent);
            }
        }, 750);

    }
}