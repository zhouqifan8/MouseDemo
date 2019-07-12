package com.demo.mouse.remote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.demo.mouse.R;

public class MouseActivity extends AppCompatActivity {
    private SessionView sessionView;
    private TouchPointerView touchPointerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mouse);
        sessionView = (SessionView) findViewById(R.id.sessionView);
        touchPointerView = (TouchPointerView) findViewById(R.id.touchPointerView);
        sessionView.setview(touchPointerView);

    }
}
