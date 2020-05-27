package com.sen.mycontractor.custom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sen.mycontractor.interfaces.OnMovingListener;
import com.sen.mycontractor.interfaces.OnSingleTapListener;


/**
 * Created by Sen on 2017/9/2.
 */

public class MatrixTouchListener extends AppCompatImageView implements View.OnTouchListener {
    private static final int MODE_DRAG = 111, MODE_ZOOM = 112, MODE_UNABLE = 113;
    private float mMaxScale = 6, mDoubleClickScale = 2, mStartFingersDistance;
    private int mMode = 110;
    private Matrix currentMatrix = new Matrix();
    private PointF mStartPoint = new PointF();
    private boolean leftDraggable, rightDraggable, firstMove = false;
    private OnMovingListener moveListener;
    private OnSingleTapListener singleTapListener;
    private float imageWidth, imageHeight, mScale;
    private Matrix mMatrix = new Matrix();
    private GestureDetector mGestureDetector;

    public MatrixTouchListener(Context context) {
        super(context);
    }




    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mMode = MODE_DRAG;
                mStartPoint.set(event.getX(), event.getY());
                isMatrixEnable();
                startDrag();
                checkDraggable();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetMatrix();
                stopDrag();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE_ZOOM) {
                    setZoomMatrix(event);
                } else if (mMode == MODE_DRAG) {
                    setDragMatrix(event);
                } else {
                    stopDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mMode == MODE_UNABLE) return true;
                mMode = MODE_ZOOM;
                mStartFingersDistance = getDistance(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            default:
                break;
        }
        return mGestureDetector.onTouchEvent(event);


    }

    private void startDrag() {
        if (moveListener != null) {
            moveListener.startDrag();
        }
    }

    private void stopDrag() {
        if (moveListener != null) {
            moveListener.stopDrag();
        }
    }

    private void isMatrixEnable() {
        if (getScaleType() != ScaleType.CENTER) {
            setScaleType(ScaleType.MATRIX);
        } else {
            mMode = MODE_UNABLE;
        }
    }

    private void checkDraggable() {
        leftDraggable = true;
        rightDraggable = true;
        firstMove = true;
        float[] values = new float[9];
        getImageMatrix().getValues(values);
        if (values[Matrix.MTRANS_X] >= 0) {
            rightDraggable = false;
        }
        if (imageWidth * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X] <= getWidth()) {
            leftDraggable = false;
        }
    }

    private boolean checkRest() {
        float[] values = new float[9];
        getImageMatrix().getValues(values);
        float scale = values[Matrix.MSCALE_X];
        return scale < mScale;
    }

    private void resetMatrix() {
        if (checkRest()) {
            currentMatrix.set(mMatrix);
            setImageMatrix(currentMatrix);
        } else {
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            float height = imageHeight * values[Matrix.MSCALE_Y];
            if (height < getHeight()) {
                float topMargin = (getHeight() - height) / 2;
                if (topMargin != values[Matrix.MTRANS_Y]) {
                    currentMatrix.set(getImageMatrix());
                    currentMatrix.postTranslate(0, topMargin - values[Matrix.MTRANS_Y]);
                    setImageMatrix(currentMatrix);
                }
            }
        }
    }

    public void setZoomMatrix(MotionEvent zoomMatrix) {
        if (zoomMatrix.getPointerCount() < 2) {
            return;
        }
        float endDis = getDistance(zoomMatrix);
        if (endDis > 10f) {
            float scale = endDis / mStartFingersDistance;
            mStartFingersDistance = endDis;
            currentMatrix.set(getImageMatrix());
            float[] values = new float[9];
            currentMatrix.getValues(values);
            scale = checkMaxScale(scale, values);
            PointF centerF = getCenter(scale, values);
            currentMatrix.postScale(scale, scale, centerF.x, centerF.y);
            setImageMatrix(currentMatrix);
        }
    }

    private float getDistance(MotionEvent zoomMatrix) {
        float dx = zoomMatrix.getX(1) - zoomMatrix.getX(0);
        float dy = zoomMatrix.getY(1) - zoomMatrix.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float checkMaxScale(float scale, float[] values) {
        if (scale * values[Matrix.MSCALE_X] > mMaxScale)
            scale = mMaxScale / values[Matrix.MSCALE_X];
        return scale;
    }

    private PointF getCenter(float scale, float[] values) {
        if (scale * values[Matrix.MSCALE_X] < mScale || scale >= 1) {
            return new PointF(getWidth() / 2, getHeight() / 2);
        }
        float cx = getWidth() / 2;
        float cy = getHeight() / 2;

        if ((getWidth() / 2 - values[Matrix.MTRANS_X]) * scale < getWidth() / 2)
            cx = 0;
        if ((imageWidth * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X]) * scale < getWidth())
            cx = getWidth();
        return new PointF(cx, cy);
    }

    private boolean isZoomChanged() {
        float[] values = new float[9];
        getImageMatrix().getValues(values);
        float scale = values[Matrix.MSCALE_X];
        return scale != mScale;
    }

    private float checkDyBound(float[] values, float dy) {
        float height = getHeight();
        if (imageHeight * values[Matrix.MSCALE_Y] < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(imageHeight * values[Matrix.MSCALE_Y] - height))
            dy = -(imageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }

    private float checkDxBound(float[] values, float dx, float dy) {
        float width = getWidth();
        if (!leftDraggable && dx < 0) {
            if (Math.abs(dx) * 0.4f > Math.abs(dy) && firstMove) {
                stopDrag();
            }
            return 0;
        }
        if (!rightDraggable && dx > 0) {
            if (Math.abs(dx) * 0.4f > Math.abs(dy) && firstMove) {
                stopDrag();
            }
            return 0;
        }
        leftDraggable = true;
        rightDraggable = true;
        if (firstMove) firstMove = false;
        if (imageWidth * values[Matrix.MSCALE_X] < width) {
            return 0;

        }
        if (values[Matrix.MTRANS_X] + dx > 0) {
            dx = -values[Matrix.MTRANS_X];
        } else if (values[Matrix.MTRANS_X] + dx < -(imageWidth * values[Matrix.MSCALE_X] - width)) {
            dx = -(imageWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        }
        return dx;
    }

    public void setDragMatrix(MotionEvent event) {
        if (isZoomChanged()) {
            float dx = event.getX() - mStartPoint.x;
            float dy = event.getY() - mStartPoint.y;

            if (Math.sqrt(dx * dx + dy * dy) > 10f) {
                mStartPoint.set(event.getX(), event.getY());
                currentMatrix.set(getImageMatrix());
                float[] values = new float[9];
                currentMatrix.getValues(values);
                dy = checkDyBound(values, dy);
                dx = checkDxBound(values, dx, dy);
                currentMatrix.postTranslate(dx, dy);
                setImageMatrix(currentMatrix);
            }
        } else {
            stopDrag();
        }
    }

    public void onDoubleClick(){
        float scale=isZoomChanged()?1:mDoubleClickScale;
        currentMatrix.set(mMatrix);//初始化Matrix
        currentMatrix.postScale(scale, scale,getWidth()/2,getHeight()/2);
        setImageMatrix(currentMatrix);
    }
}
