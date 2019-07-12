package com.demo.admin.testdemo;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.demo.admin.testdemo.utils.DoubleGestureDetector;
import com.demo.admin.testdemo.utils.GestureDetector;
import com.demo.admin.testdemo.utils.GlobalSettings;

import java.util.Stack;

public class FillView extends View {
    TouchMoveView pointerView;
    private float scaleFactor = 1.0f;
    private Matrix scaleMatrix;
    private Matrix invScaleMatrix;
    private RectF invalidRegionF;
    private GestureDetector gestureDetector;
    private DoubleGestureDetector doubleGestureDetector;
    private float DownY, DownX;
    private MotionEvent prevEvent = null;
    private MotionEvent prevEvent1 = null;
    private Stack<Rect> invalidRegions;
    private String TAG = "FillView";
    private SessionViewListener sessionViewListener = null;
    private static final float TOUCH_SCROLL_DELTA = 10.0f;
    boolean doubletouch = false;

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

    public void setview(TouchMoveView MoveView) {
        pointerView = MoveView;
    }

    public FillView(Context context) {
        super(context);
        initSessionView(context);
    }

    public FillView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSessionView(context);
    }

    public FillView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSessionView(context);
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

    public void setSessionViewListener(SessionViewListener sessionViewListener) {
        this.sessionViewListener = sessionViewListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean res = gestureDetector.onTouchEvent(event);
        res |= doubleGestureDetector.onTouchEvent(event);
        //当所有手指都touch屏幕时触发时
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
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
}
