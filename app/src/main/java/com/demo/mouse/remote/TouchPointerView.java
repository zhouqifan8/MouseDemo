package com.demo.mouse.remote;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.demo.mouse.ConstantGloble;
import com.demo.mouse.R;
import com.demo.mouse.UIUtils;
import com.demo.mouse.remote.utils.GestureDetector;

public class TouchPointerView extends ImageView {
    private View view;

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

    private class UIHandler extends Handler {

        UIHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            setPointerImage(R.drawable.mouse);
        }
    }

    public void setview(View v) {
        view = v;
    }

    private static final float SCROLL_DELTA = 10.0f;

    private static final int DEFAULT_TOUCH_POINTER_RESTORE_DELAY = 150;

    private RectF pointerRect;
    private RectF pointerAreaRects[] = new RectF[9];
    private Matrix translationMatrix;
    private boolean pointerMoving = false;
    private boolean pointerScrolling = false;
    private TouchPointerListener listener = null;
    private UIHandler uiHandler = new UIHandler();
    private GestureDetector gestureDetector;

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

    private Context mcontext;

    private int screen_width;

    private int screen_height;

    public TouchPointerView(Context context) {
        super(context);
        this.mcontext = context;
        initTouchPointer(context);
    }

    public TouchPointerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext = context;
        initTouchPointer(context);
    }

    public TouchPointerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mcontext = context;
        initTouchPointer(context);
    }

    public void setRightClick(boolean down) {
        displayPointerImageAction(R.drawable.mouse);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerRightClick((int) rect.centerX(), (int) rect.centerY(), down);
    }

    public void setRightClick() {
        displayPointerImageAction(R.drawable.mouse);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerRightClick((int) rect.centerX(), (int) rect.centerY(), true);
        listener.onTouchPointerRightClick((int) rect.centerX(), (int) rect.centerY(), false);
    }

    public void setRightClick(int x, int y) {
        displayPointerImageAction(R.drawable.mouse);
        listener.onTouchPointerRightClick(x, y, true);
        listener.onTouchPointerRightClick(x, y, false);
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

    public void setmove(float x, float y) {
        movePointer(x, y);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerMove((int) rect.centerX(), (int) rect.centerY());
        listener.onTouchPointerLeftClick((int) rect.centerX(), (int) rect.centerY(), true);
    }

    public void setrootpointermove(float x, float y) {
        movePointer(x, y);
        RectF rect = getCurrentPointerArea();
        listener.onTouchPointerMove((int) rect.centerX(), (int) rect.centerY());
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

    public void setTouchPointerListener(TouchPointerListener listener) {
        this.listener = listener;
    }

    public int getPointerWidth() {
        return getDrawable().getIntrinsicWidth();
    }

    public int getPointerHeight() {
        return getDrawable().getIntrinsicHeight();
    }

    public float[] getPointerPosition() {
        float[] curPos = new float[2];
        translationMatrix.mapPoints(curPos);
        return curPos;
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

    private void ensureVisibility(int screen_width, int screen_height) {
        float[] curPos = new float[2];
        translationMatrix.mapPoints(curPos);

        if (curPos[0] > (screen_width - pointerRect.width()))
            curPos[0] = screen_width - pointerRect.width();
        if (curPos[0] < 0)
            curPos[0] = 0;
        if (curPos[1] > (screen_height - pointerRect.height()))
            curPos[1] = screen_height - pointerRect.height();
        if (curPos[1] < 0)
            curPos[1] = 0;

        translationMatrix.setTranslate(curPos[0], curPos[1]);
        setImageMatrix(translationMatrix);
    }

    private void displayPointerImageAction(int resId) {
        setPointerImage(resId);
        uiHandler.sendEmptyMessageDelayed(0, DEFAULT_TOUCH_POINTER_RESTORE_DELAY);
    }

    private void setPointerImage(int resId) {
        setImageResource(resId);
    }

    private RectF getCurrentPointerArea(int area) {
        RectF transRect = new RectF(pointerAreaRects[area]);
        translationMatrix.mapRect(transRect);
        return transRect;
    }

    private RectF getCurrentPointerArea() {
        RectF transRect = new RectF();
        translationMatrix.mapRect(transRect);
        return transRect;
    }

    private boolean pointerAreaTouched(MotionEvent event, int area) {
        RectF transRect = new RectF(pointerAreaRects[area]);
        translationMatrix.mapRect(transRect);
        if (transRect.contains(event.getX(), event.getY()))
            return true;
        return false;
    }

    private boolean pointerTouched(MotionEvent event) {
        RectF transRect = new RectF(pointerRect);
        translationMatrix.mapRect(transRect);
        if (transRect.contains(event.getX(), event.getY()))
            return true;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!pointerMoving && !pointerScrolling && !pointerTouched(event))
            return false;
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed)
            ensureVisibility(right - left, bottom - top);
    }
}

