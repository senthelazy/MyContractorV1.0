package com.sen.mycontractor.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Project;



import java.util.ArrayList;


/**
 * Created by Sen on 2017/8/28.
 */

public class ListAdapter extends BaseAdapter{
    private Context mContext;
    ArrayList<Project> mProjects;

    public ListAdapter(Context context,ArrayList<Project> projects){
        this.mContext=context;
        this.mProjects=projects;

    }
    @Override
    public int getCount() {
        return mProjects.size();
    }

    @Override
    public Object getItem(int i) {
        return mProjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup group) {
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.cardview_request,group,false);
        }
        TextView categoryTv=(TextView)view.findViewById(R.id.categoryForListTv);
        TextView descriptionTv=(TextView)view.findViewById(R.id.descriptionTv);
        TextView subCategoryTv=(TextView)view.findViewById(R.id.subCategoryTv);
        final Project mProject=(Project)this.getItem(i);
        categoryTv.setText(mProject.getCategory());
        descriptionTv.setText(mProject.getJobDescription());
        subCategoryTv.setText(mProject.getSubCategory());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,mProject.getCategory(),Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
