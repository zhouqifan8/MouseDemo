package com.demo.admin.testdemo;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TouchMoveView.TouchPointerListener, FillView.SessionViewListener {

    private TouchMoveView TouchMoveView;
    private FillView fillView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        TouchMoveView = findViewById(R.id.TouchMoveView);
        fillView = findViewById(R.id.fillView);
        fillView.setview(TouchMoveView);
        TouchMoveView.setTouchPointerListener(this);
        fillView.setSessionViewListener(this);
//        TouchMoveView.setVisibility(View.GONE);
    }

    @Override
    public void onSessionViewBeginTouch() {

    }

    @Override
    public void onSessionViewEndTouch() {

    }

    @Override
    public void onSessionViewLeftTouch(int x, int y, boolean down) {
        Log.e("8坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onSessionViewRightTouch(int x, int y, boolean down) {
        Log.e("7坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onSessionViewRight(int x, int y, boolean down) {
        Log.e("6坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onSessionViewMove(int x, int y) {
        Log.e("5坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onViewMove(MotionEvent en1, MotionEvent en2) {

    }

    @Override
    public void onSessionViewScroll(boolean down) {

    }

    @Override
    public void onRightClick(int x, int y, boolean down) {
        Log.e("4坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onPointerScroll(boolean down) {

    }

    @Override
    public void onTouchPointerClose() {

    }

    @Override
    public void onTouchPointerLeftClick(int x, int y, boolean down) {

        Log.e("1坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onTouchPointerRightClick(int x, int y, boolean down) {
        Log.e("2坐标", "X：" + x + "Y:" + y);
    }

    @Override
    public void onTouchPointerMove(int x, int y) {
        Log.e("3坐标", "X：" + x + "Y:" + y);
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
