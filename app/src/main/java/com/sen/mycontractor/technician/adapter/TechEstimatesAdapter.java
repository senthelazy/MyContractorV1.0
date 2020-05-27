package com.sen.mycontractor.technician.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Estimate;


import java.util.List;

/**
 * Created by Jessie on 10/21/2017.
 */

public class TechEstimatesAdapter extends BaseItemDraggableAdapter<Estimate, BaseViewHolder> {

    public TechEstimatesAdapter(List<Estimate> estimates) {
        super(R.layout.cardview_estimate, estimates);
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, Estimate estimate) {
        baseViewHolder.setText(R.id.tech_name, estimate.getProjectId())
                .setText(R.id.opinion, estimate.getOpinion())
                .setText(R.id.amount, estimate.getAmount())
                .setText(R.id.time, estimate.getTime())
                .setVisible(R.id.tech_name_text, true);
    }
}
