package com.sen.mycontractor.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sen.mycontractor.R;

/**
 * Created by Sen on 2017/8/27.
 */

public class GridAdapter extends BaseAdapter {

    private int icons[];

    private Context mContext;

    private String mLetters[];

    private LayoutInflater mLayoutInflater;

    public GridAdapter(Context context, int icons[],String letters[]) {
        this.mContext = context;
        this.icons = icons;
       this.mLetters = letters;
    }

    @Override
    public int getCount() {
       return mLetters.length;

    }

    @Override
    public Object getItem(int i) {
        //return mLetters[i];
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup group) {
        View gridView = view;
        if (view == null) {
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = mLayoutInflater.inflate(R.layout.custom_dashboard, null);

        }
        ImageView iconIv = (ImageView) gridView.findViewById(R.id.iconsIv);
        iconIv.setImageResource(icons[i]);
       TextView letter = (TextView) gridView.findViewById(R.id.lettersTv);
        letter.setText(mLetters[i]);
       // ImageView backgroundIv = (ImageView) gridView.findViewById(R.id.backgroundIv);
       // backgroundIv.setImageResource(R.drawable.rectangle);


        return gridView;
    }
}
