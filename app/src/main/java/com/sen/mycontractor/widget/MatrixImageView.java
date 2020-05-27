package com.sen.mycontractor.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.sen.mycontractor.custom.GestureListener;
import com.sen.mycontractor.custom.MatrixTouchListener;
import com.sen.mycontractor.interfaces.OnMovingListener;
import com.sen.mycontractor.interfaces.OnSingleTapListener;

/**
 * Created by Sen on 2017/9/2.
 */

public class MatrixImageView extends AppCompatImageView {
    public final static String TAG="MatrixImageView";
    private OnSingleTapListener singleTapListener;
    private GestureDetector mGestureDetector;
    private OnMovingListener mMovingListener;

    public MatrixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MatrixTouchListener mMatrixTouchListener=new MatrixTouchListener(context);
        setOnTouchListener(mMatrixTouchListener);
        mGestureDetector=new GestureDetector(getContext(), new GestureListener(mMatrixTouchListener));
        setBackgroundColor(Color.BLACK);
        setScaleType(ScaleType.FIT_CENTER);
    }
    public MatrixImageView(Context context) {
        super(context, null);
        MatrixTouchListener mMatrixTouchListener=new MatrixTouchListener(context);
        setOnTouchListener(mMatrixTouchListener);
        mGestureDetector=new GestureDetector(getContext(), new GestureListener(mMatrixTouchListener));
        setBackgroundColor(Color.BLACK);
        setScaleType(ScaleType.FIT_CENTER);
    }

    public void setOnMovingListener(OnMovingListener listener){
        this.mMovingListener=listener;
    }
    public void setOnSingleTapListener(OnSingleTapListener onSingleTapListener) {
        this.singleTapListener = onSingleTapListener;
    }
}
