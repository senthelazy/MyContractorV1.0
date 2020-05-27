package com.sen.mycontractor.custom;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.sen.mycontractor.interfaces.OnSingleTapListener;

/**
 * Created by Sen on 2017/9/2.
 */

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private final MatrixTouchListener mMatrixTouchListener;
    private OnSingleTapListener singleTapListener;

    public GestureListener(MatrixTouchListener listener){
        this.mMatrixTouchListener=listener;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        //捕获Down事件
        return true;
    }
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //触发双击事件
        mMatrixTouchListener.onDoubleClick();
        return true;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return super.onSingleTapUp(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
        super.onLongPress(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
        super.onShowPress(e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // TODO Auto-generated method stub
        return super.onDoubleTapEvent(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(singleTapListener!=null) singleTapListener.onSingleTap();
        return super.onSingleTapConfirmed(e);
    }
}
