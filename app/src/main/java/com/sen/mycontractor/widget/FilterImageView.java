package com.sen.mycontractor.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;



public class FilterImageView extends android.support.v7.widget.AppCompatImageView{
    public FilterImageView(Context context){
        super(context);
    }
    public FilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    public FilterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    private void setFilter() {
        Drawable drawable=getDrawable();
        if (drawable==null) {
            drawable=getBackground();
        }
        if(drawable!=null){
            drawable.setColorFilter(Color.GRAY,PorterDuff.Mode.MULTIPLY);;
        }
    }

    private void removeFilter() {
        Drawable drawable=getDrawable();
        if (drawable==null) {
            drawable=getBackground();
        }
        if(drawable!=null){
            drawable.clearColorFilter();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                setFilter();
                break;
            case MotionEvent.ACTION_UP:
                performClick();
            case MotionEvent.ACTION_CANCEL:

                removeFilter();
                break;
            default:
                break;
        }
        return true;
    }

}

