package com.demo.mouse.remote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.demo.mouse.ConstantGloble;
import com.demo.mouse.R;
import com.demo.mouse.remote.utils.GlobalSettings;

public class SessionActivity extends AppCompatActivity implements TouchPointerView.TouchPointerListener {
    private TouchPointerView touchPointerView;
    private View activityRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConstantGloble.ROOTFLAG = false;
        ConstantGloble.isInputflag = false;
        GlobalSettings.setFlag(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_session);
        activityRootView = findViewById(R.id.session_root_view);
        touchPointerView = findViewById(R.id.touchPointerView);
        touchPointerView.setview(activityRootView);
        touchPointerView.setTouchPointerListener(this);
    }

    @Override
    public void onTouchPointerClose() {

    }

    @Override
    public void onTouchPointerLeftClick(int x, int y, boolean down) {
        Log.e("坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onTouchPointerRightClick(int x, int y, boolean down) {
        Log.e("坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onTouchPointerMove(int x, int y) {
        Log.e("坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onTouchPointerScroll(boolean down) {

    }

    @Override
    public void onTouchPointerToggleKeyboard() {

    }

    @Override
    public void onTouchPointerToggleExtKeyboard() {

    }

    @Override
    public void onTouchPointerResetScrollZoom() {

    }

    @Override
    public void onRootViewMove(int y) {

    }
}
