package com.demo.mouse.remote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.demo.mouse.ConstantGloble;
import com.demo.mouse.remote.utils.DoubleGestureDetector;
import com.demo.mouse.remote.utils.GestureDetector;
import com.demo.mouse.remote.utils.GlobalSettings;

import java.util.Stack;

public class SessionView extends View {
    /**
     * 波形的List
     */
//    private List<Wave> waveList;

    /**
     * 最大的不透明度，完全不透明
     */
    private static final int MAX_ALPHA = 255;

    protected static final int FLUSH_ALL = -1;

    private boolean isStart = true;
    boolean doubletouch = false;
    boolean doubletouchscrool = false;
    boolean scrolling = false;
    //滑动距离临界值
    private static final float SCROLL_DELTA = 10.0f;

    public interface SessionViewListener {
        abstract void onSessionViewBeginTouch();

        abstract void onSessionViewEndTouch();

        abstract void onSessionViewLeftTouch(int x, int y, boolean down);

        abstract void onSessionViewRightTouch(int x, int y, boolean down);

        abstract void onSessionViewRight(int x, int y, boolean down);

        abstract void onSessionViewMove(int x, int y);

        abstract void onViewMove(MotionEvent en1, MotionEvent en2);

        abstract void onSessionViewScroll(boolean down);

        //        abstract void onLeftClick(int x, int y, boolean down);
        abstract void onRightClick(int x, int y, boolean down);

        abstract void onPointerScroll(boolean down);

    }

    private int mouseX;
    private int mouseY;
    private int width;
    private int height;
    private BitmapDrawable surface;
    private Stack<Rect> invalidRegions;

    private int touchPointerPaddingWidth = 0;
    private int touchPointerPaddingHeight = 0;

    private SessionViewListener sessionViewListener = null;

    public static final float MAX_SCALE_FACTOR = 3.0f;
    public static final float MIN_SCALE_FACTOR = 1.0f;
    private static final float SCALE_FACTOR_DELTA = 0.0001f;

    private static final float TOUCH_SCROLL_DELTA = 10.0f;

    // helpers for scaling gesture handling
    private float scaleFactor = 1.0f;
    private Matrix scaleMatrix;
    private Matrix invScaleMatrix;
    private RectF invalidRegionF;

    //private static final String TAG = "FreeRDP.SessionView";
    TouchPointerView pointerView;
    private GestureDetector gestureDetector;
    private DoubleGestureDetector doubleGestureDetector;
    private float DownY, DownX;
    private MotionEvent prevEvent = null;
    private MotionEvent prevEvent1 = null;
    private String TAG = "Gesture";

    public void setview(TouchPointerView v) {
        pointerView = v;
    }

    private class SessionGestureListener extends GestureDetector.SimpleOnGestureListener {
        boolean longPressInProgress = false;

        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown");
            prevEvent = MotionEvent.obtain(e);
            return true;
        }

        public boolean onUp(MotionEvent event) {
//            Log.d(TAG, "up");
            sessionViewListener.onSessionViewEndTouch();
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub
            super.onShowPress(e);

        }

        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress");
            MotionEvent mappedEvent = mapTouchEvent(e);
            sessionViewListener.onSessionViewBeginTouch();

            if (GlobalSettings.isFlag()) {
                sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), true);
            } else {

                pointerView.setLeftClick(true);
            }
            longPressInProgress = true;
        }

        public void onLongPressUp(MotionEvent e) {
            Log.d(TAG, "onLongPressUp");
            MotionEvent mappedEvent = mapTouchEvent(e);
            if (GlobalSettings.isFlag()) {

                sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), false);
            } else {
                pointerView.setLeftClick(false);
            }

            longPressInProgress = false;
            sessionViewListener.onSessionViewEndTouch();
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
            prevEvent1 = e2;
            doubletouch = false;
            MotionEvent mappedEvent = mapTouchEvent(e2);
            if (GlobalSettings.isFlag()) {
//                sessionViewListener.onViewMove(e1, e2);
                float deltaY = e2.getY() - e1.getY();
//                float deltaY1 = e1.getY() - e1.getY();
                if (deltaY > TOUCH_SCROLL_DELTA) {
                    sessionViewListener.onSessionViewScroll(false);
                } else if (deltaY < -TOUCH_SCROLL_DELTA) {
                    sessionViewListener.onSessionViewScroll(true);
                }
            }
            if (longPressInProgress) {
                if (GlobalSettings.isFlag()) {
                    sessionViewListener.onSessionViewMove((int) mappedEvent.getX(), (int) mappedEvent.getY());
                    return true;
                }
            }

            return false;
        }

        public boolean onDoubleTap(MotionEvent e) {
            // send 2nd click for double c
            MotionEvent mappedEvent = mapTouchEvent(e);
            if (GlobalSettings.isFlag()) {
                sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), true);
                sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), false);

            } else {
                pointerView.setLeftClick();

            }

            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent");
            // TODO Auto-generated method stub
            return false;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp");
            // send single click
            MotionEvent mappedEvent = mapTouchEvent(e);
            sessionViewListener.onSessionViewBeginTouch();
            if (mappedEvent == null) {
                return false;
            }
            if (GlobalSettings.isFlag()) {

                sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), true);
                sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), false);
            } else {
                pointerView.setLeftClick();
            }
            sessionViewListener.onSessionViewEndTouch();
            return true;
        }
    }

    private class SessionDoubleGestureListener implements DoubleGestureDetector.OnDoubleGestureListener, GestureDetector.OnDoubleTapListener {
        private MotionEvent prevEvent = null;

        public boolean onDoubleTouchDown(MotionEvent e) {
            Log.d(TAG, "onDoubleTouchDown");
            sessionViewListener.onSessionViewBeginTouch();
            prevEvent = MotionEvent.obtain(e);
            return true;
        }

        public boolean onDoubleTouchUp(MotionEvent e) {
            Log.d(TAG, "onDoubleTouchUp");
            if (prevEvent != null) {
                prevEvent.recycle();
                prevEvent = null;
            }
            sessionViewListener.onSessionViewEndTouch();
            return true;
        }

        public boolean onDoubleTouchScroll(MotionEvent e1, MotionEvent e2) {
            // calc if user scrolled up or down (or if any scrolling happened at all)
            Log.d(TAG, "onDoubleTouchScroll");
//            float deltaY = e2.getY() - prevEvent.getY();
//            float deltaY1 = e1.getY() - prevEvent.getY();
//            if (deltaY > TOUCH_SCROLL_DELTA || deltaY1 > TOUCH_SCROLL_DELTA) {
//                sessionViewListener.onSessionViewScroll(false);
//                prevEvent.recycle();
//                prevEvent = MotionEvent.obtain(e2);
//            } else if (deltaY < -TOUCH_SCROLL_DELTA || deltaY1 < -TOUCH_SCROLL_DELTA) {
//                sessionViewListener.onSessionViewScroll(true);
//                prevEvent.recycle();
//                prevEvent = MotionEvent.obtain(e2);
//            }
            return true;
        }

        public boolean onDoubleTouchSingleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTouchSingleTap");
            // send single click
            MotionEvent mappedEvent = mapDoubleTouchEvent(e);
            if (GlobalSettings.isFlag()) {
            } else {
                pointerView.setRightClick();
            }

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onDoubleTap");
            pointerView.setLeftClick();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onDoubleTapEvent");
            return true;
        }
    }


    private void initSessionView(Context context) {
        invalidRegions = new Stack<Rect>();
        gestureDetector = new GestureDetector(context, new SessionGestureListener(), null, true);
        doubleGestureDetector = new DoubleGestureDetector(context, null, new SessionDoubleGestureListener());
        scaleFactor = 1.0f;
        scaleMatrix = new Matrix();
        invScaleMatrix = new Matrix();
        invalidRegionF = new RectF();
    }

    public SessionView(Context context) {
        super(context);
        initSessionView(context);
    }

    public SessionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSessionView(context);
    }

    public SessionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSessionView(context);
    }

    public void setScaleGestureDetector(ScaleGestureDetector scaleGestureDetector) {
        doubleGestureDetector.setScaleGestureDetector(scaleGestureDetector);
    }

    public void setSessionViewListener(SessionViewListener sessionViewListener) {
        this.sessionViewListener = sessionViewListener;
    }

    public void addInvalidRegion(Rect invalidRegion) {
        invalidRegionF.set(invalidRegion);
        scaleMatrix.mapRect(invalidRegionF);
        invalidRegionF.roundOut(invalidRegion);

        invalidRegions.add(invalidRegion);
    }

    public void invalidateRegion() {
        invalidate(invalidRegions.pop());
    }

    public void setZoom(float factor) {
        scaleFactor = factor;
        scaleMatrix.setScale(scaleFactor, scaleFactor);
        invScaleMatrix.setScale(1.0f / scaleFactor, 1.0f / scaleFactor);
        requestLayout();
    }

    public float getZoom() {
        return scaleFactor;
    }

    public boolean isAtMaxZoom() {
        return (scaleFactor > (MAX_SCALE_FACTOR - SCALE_FACTOR_DELTA));
    }

    public boolean isAtMinZoom() {
        return (scaleFactor < (MIN_SCALE_FACTOR + SCALE_FACTOR_DELTA));
    }

    public boolean zoomIn(float factor) {
        boolean res = true;
        scaleFactor += factor;
        if (scaleFactor > (MAX_SCALE_FACTOR - SCALE_FACTOR_DELTA)) {
            scaleFactor = MAX_SCALE_FACTOR;
            res = false;
        }
        setZoom(scaleFactor);
        return res;
    }

    public boolean zoomOut(float factor) {
        boolean res = true;
        scaleFactor -= factor;
        if (scaleFactor < (MIN_SCALE_FACTOR + SCALE_FACTOR_DELTA)) {
            scaleFactor = MIN_SCALE_FACTOR;
            res = false;
        }
        setZoom(scaleFactor);
        return res;
    }

    public void setTouchPointerPadding(int widht, int height) {
        touchPointerPaddingWidth = widht;
        touchPointerPaddingHeight = height;
        requestLayout();
    }

    public int getTouchPointerPaddingWidth() {
        return touchPointerPaddingWidth;
    }

    public int getTouchPointerPaddingHeight() {
        return touchPointerPaddingHeight;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension((int) (width * scaleFactor) + touchPointerPaddingWidth, (int) (height * scaleFactor) + touchPointerPaddingHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.concat(scaleMatrix);
        surface.draw(canvas);
        canvas.restore();
    }


    private MotionEvent mapTouchEvent(MotionEvent event) {
        MotionEvent mappedEvent = MotionEvent.obtain(event);
        float[] coordinates = {mappedEvent.getX(), mappedEvent.getY()};
        invScaleMatrix.mapPoints(coordinates);
        mappedEvent.setLocation(coordinates[0], coordinates[1]);
        return mappedEvent;
    }

    private MotionEvent mapDoubleTouchEvent(MotionEvent event) {
        MotionEvent mappedEvent = MotionEvent.obtain(event);
        float[] coordinates = {(mappedEvent.getX(0) + mappedEvent.getX(1)) / 2, (mappedEvent.getY(0) + mappedEvent.getY(1)) / 2};
        invScaleMatrix.mapPoints(coordinates);
        mappedEvent.setLocation(coordinates[0], coordinates[1]);
        return mappedEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean res = gestureDetector.onTouchEvent(event);
        res |= doubleGestureDetector.onTouchEvent(event);
        //当所有手指都touch屏幕时触发时
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 2) {
                float deltaY = event.getY() - prevEvent.getY();
                float deltaY1 = event.getY() - prevEvent.getY();
                if (deltaY > TOUCH_SCROLL_DELTA || deltaY1 > TOUCH_SCROLL_DELTA) {
                    sessionViewListener.onSessionViewScroll(false);
                    prevEvent.recycle();
                    prevEvent = MotionEvent.obtain(event);
                } else if (deltaY < -TOUCH_SCROLL_DELTA || deltaY1 < -TOUCH_SCROLL_DELTA) {
                    sessionViewListener.onSessionViewScroll(true);
                    prevEvent.recycle();
                    prevEvent = MotionEvent.obtain(event);
                }
            } else if (event.getPointerCount() == 1) {
                if (!GlobalSettings.isFlag()) {
                    if (!doubletouch) {
                        if (prevEvent != null && prevEvent1 != null) {

                            pointerView.setmove(prevEvent, prevEvent1, false);
                        }
                    }
                } else {
                    if (ConstantGloble.ROOTFLAG) {
                        if (prevEvent != null && prevEvent1 != null) {
                            pointerView.setmove1(prevEvent, prevEvent1, false);
                        }
                    }
                }
            }
        } else {
            //单点触控时，只绘制A点
            if (event.getPointerCount() == 1) {

            }
            //多点触控时，同时绘制A,B点
            else if (event.getPointerCount() == 2) {

            }
        }
        //进行绘制


        return res;
    }


    private boolean flag = false;

    private class Wave {
        int waveX;
        int waveY;
        /**
         * 用来表示圆环的半径
         */
        float radius;
        Paint paint;
        /**
         * 按下的时候x坐标
         */
        int xDown;
        /**
         * 按下的时候y的坐标
         */
        int yDown;
        float width;
        int alpha;
    }

    /**
     * 解决横屏调用软键盘，会出现软键盘没有候选栏的情况，导致无法输入中文
     */
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
//      outAttrs.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
//      outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return null;
    }


}
