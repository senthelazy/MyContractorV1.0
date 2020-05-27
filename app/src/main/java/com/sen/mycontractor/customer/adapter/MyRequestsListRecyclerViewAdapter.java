package com.sen.mycontractor.customer.adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Project;

import java.util.List;


public class MyRequestsListRecyclerViewAdapter extends BaseItemDraggableAdapter<Project, BaseViewHolder> implements BaseQuickAdapter.OnItemChildClickListener {
    private Context mContext;


    public MyRequestsListRecyclerViewAdapter(Context context, List<Project> projects) {
        super(R.layout.cardview_request_customer, projects);
        this.mContext = context;


    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Project project) {
        baseViewHolder.setText(R.id.categoryForListTv, project.getCategory())
                .setText(R.id.descriptionTv, project.getJobDescription())
                .setText(R.id.locationTv, project.getLocation())
                .setText(R.id.created_at, project.getCreatedAt())
                .addOnClickListener(R.id.details_icon)
                .addOnClickListener(R.id.details)
                .addOnClickListener(R.id.estimates_icon)
                .addOnClickListener(R.id.estimates);


    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
