package com.sen.mycontractor.technician.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Project;

import java.util.List;



public class TechRequestsListAdapter extends BaseQuickAdapter<Project, BaseViewHolder> {


    public TechRequestsListAdapter(List<Project> projects) {
        super(R.layout.cardview_request_tech, projects);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Project project) {
        baseViewHolder.setText(R.id.categoryForListTv, project.getCategory())
                .setText(R.id.descriptionTv, project.getJobDescription())
                .setText(R.id.locationTv, project.getLocation())
                .setText(R.id.created_at, project.getCreatedAt());
    }


}
