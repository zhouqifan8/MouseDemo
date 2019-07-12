package com.demo.admin.testdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.demo.admin.testdemo.utils.GestureDetector;
import com.demo.admin.testdemo.utils.UIUtils;

public class TouchMoveView extends ImageView {
    private GestureDetector gestureDetector;
    private String TAG = "TouchMoveView";
    private boolean pointerMoving = false;
    private boolean pointerScrolling = false;
    private RectF pointerRect;
    private RectF pointerAreaRects[] = new RectF[9];
    private Matrix translationMatrix;
    private TouchPointerListener listener = null;
    private static final float SCROLL_DELTA = 10.0f;
    private static final int DEFAULT_TOUCH_POINTER_RESTORE_DELAY = 150;
    private UIHandler uiHandler = new UIHandler();

    private Context mcontext;
    private int screen_width;
    private int screen_height;

    private class UIHandler extends Handler {

        UIHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            setPointerImage(R.drawable.mouse);
        }
    }

    public interface TouchPointerListener {
        abstract void onTouchPointerClose();

        abstract void onTouchPointerLeftClick(int x, int y, boolean down);

        abstract void onTouchPointerRightClick(int x, int y, boolean down);

        abstract void onTouchPointerMove(int x, int y);

        abstract void onTouchPointerScroll(boolean down);

        abstract void onTouchPointerToggleKeyboard();

        abstract void onTouchPointerToggleExtKeyboard();

        abstract void onTouchPointerResetScrollZoom();

        abstract void onRootViewMove(int y);
    }

    public TouchMoveView(Context context) {
        super(context);
        this.mcontext = context;
        initTouchPointer(context);
    }

    public TouchMoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext = context;
        initTouchPointer(context);
    }

    public TouchMoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mcontext = context;
        initTouchPointer(context);
    }

    private void initTouchPointer(Context context) {
        gestureDetector = new GestureDetector(context, new TouchPointerGestureListener(), null, true);
        gestureDetector.setLongPressTimeout(500);
        translationMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(translationMatrix);
        pointerRect = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        screen_width = UIUtils.getScreenWidth(context);
        screen_height = UIUtils.getScreenHeight(context);
    }

    private class TouchPointerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private MotionEvent prevEvent = null;

        public boolean onDown(MotionEvent e) {
            prevEvent = MotionEvent.obtain(e);
            return true;
        }

        public boolean onUp(MotionEvent e) {
            if (prevEvent != null) {
                prevEvent.recycle();
                prevEvent = null;
            }

            if (pointerScrolling)
                setPointerImage(R.drawable.mouse);
            pointerMoving = false;
            pointerScrolling = false;
            return true;
        }

        public void onLongPress(MotionEvent e) {
            setPointerImage(R.drawable.mouse);
            pointerMoving = true;
            RectF rect = getCurrentPointerArea();
            listener.onTouchPointerLeftClick((int) rect.centerX(), (int) rect.centerY(), true);
        }

        public void onLongPressUp(MotionEvent e) {
            setPointerImage(R.drawable.mouse);
            pointerMoving = false;
            RectF rect = getCurrentPointerArea();
            listener.onTouchPointerLeftClick((int) rect.centerX(), (int) rect.centerY(), false);
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (pointerMoving) {
                movePointer((int) (e2.getX() - prevEvent.getX()), (int) (e2.getY() - prevEvent.getY()));
                prevEvent.recycle();
                prevEvent = MotionEvent.obtain(e2);
                RectF rect = getCurrentPointerArea();
                listener.onTouchPointerMove((int) rect.centerX(), (int) rect.centerY());
                return true;
            } else if (pointerScrolling) {
                float deltaY = e2.getY() - prevEvent.getY();
                if (deltaY > SCROLL_DELTA) {
                    listener.onTouchPointerScroll(true);
                    prevEvent.recycle();
                    prevEvent = MotionEvent.obtain(e2);
                } else if (deltaY < -SCROLL_DELTA) {
                    listener.onTouchPointerScroll(false);
                    prevEvent.recycle();
                    prevEvent = MotionEvent.obtain(e2);
                }
                return true;
            }
            return false;
        }

        public boolean onSingleTapUp(MotionEvent e) {

            return true;
        }

        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

    }

    private void displayPointerImageAction(int resId) {
        setPointerImage(resId);
        uiHandler.sendEmptyMessageDelayed(0, DEFAULT_TOUCH_POINTER_RESTORE_DELAY);
    }

    public void setRightClick() {
        displayPointerImageAction(R.drawable.mouse);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerRightClick((int) rect.centerX(), (int) rect.centerY(), true);
        listener.onTouchPointerRightClick((int) rect.centerX(), (int) rect.centerY(), false);
    }

    public void setLeftClick(boolean down) {
        setPointerImage(R.drawable.mouse);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerLeftClick((int) rect.centerX(), (int) rect.centerY(), down);
    }

    public void setLeftClick() {
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerLeftClick((int) rect.centerX(), (int) rect.centerY(), true);
        listener.onTouchPointerLeftClick((int) rect.centerX(), (int) rect.centerY(), false);
    }

    public void setmove(MotionEvent e1, MotionEvent e2, boolean flag) {
        movePointer1((int) (e2.getX() - e1.getX()), (int) (e2.getY() - e1.getY()));
        e1.recycle();
        e1 = MotionEvent.obtain(e2);
        displayPointerImageAction(R.drawable.mouse);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerMove((int) rect.centerX(), (int) rect.centerY());
    }

    public void setmove1(MotionEvent e1, MotionEvent e2, boolean flag) {
        movePointer1((int) (e2.getX() - e1.getX()), (int) (e2.getY() - e1.getY()));
        e1.recycle();
        e1 = MotionEvent.obtain(e2);
        displayPointerImageAction(R.drawable.mouse);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerMove((int) rect.centerX(), (int) rect.centerY());
    }

    private void movePointer1(float deltaX, float deltaY) {
        float[] curPos = getPointerPosition();
        if (curPos[1] <= 0 && deltaY < 0)
            deltaY = 0;

        if (curPos[0] <= 0 && deltaX < 0)
            deltaX = 0;
        if (curPos[1] >= (screen_height - 15) && deltaY > 0)
            deltaY = 0;

        if (curPos[0] >= (screen_width - 15) && deltaX > 0)
            deltaX = 0;
        if (ConstantGloble.ROOTFLAG) {
            listener.onRootViewMove((int) deltaY);
        } else {
        }
        translationMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(translationMatrix);
    }

    public void setTouchPointerListener(TouchPointerListener listener) {
        this.listener = listener;
    }

    private void movePointer(float deltaX, float deltaY) {
        float[] curPos = getPointerPosition();
        if (curPos[1] <= 0 && deltaY < 0)
            deltaY = 0;

        if (curPos[0] <= 0 && deltaX < 0)
            deltaX = 0;
        if (curPos[1] >= (screen_height - 15) && deltaY > 0)
            deltaY = 0;

        if (curPos[0] >= (screen_width - 15) && deltaX > 0)
            deltaX = 0;
        translationMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(translationMatrix);
    }

    public float[] getPointerPosition() {
        float[] curPos = new float[2];
        translationMatrix.mapPoints(curPos);
        return curPos;
    }

    private void setPointerImage(int resId) {
        setImageResource(resId);
    }

    private RectF getCurrentPointerArea() {
        RectF transRect = new RectF();
        translationMatrix.mapRect(transRect);
        return transRect;
    }

    private boolean pointerTouched(MotionEvent event) {
        RectF transRect = new RectF(pointerRect);
        translationMatrix.mapRect(transRect);
        if (transRect.contains(event.getX(), event.getY()))
            return true;
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!pointerMoving && !pointerScrolling && !pointerTouched(event))
            return false;
        return gestureDetector.onTouchEvent(event);
    }

    public void moveView(int x, int y) {
        layout(getLeft() + x, getTop() + y, getRight() + x, getBottom() + y);
    }
}
