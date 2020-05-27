package com.sen.mycontractor.customer.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Estimate;

import java.util.List;

/**
 * Created by Jessie on 10/21/2017.
 */

public class EstimatesAdapter extends BaseQuickAdapter<Estimate, BaseViewHolder> {

    public EstimatesAdapter(List<Estimate> estimates) {
        super(R.layout.cardview_estimate, estimates);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Estimate estimate) {
        baseViewHolder.setText(R.id.tech_name, estimate.getTechName())
                .setText(R.id.opinion, estimate.getOpinion())
                .setText(R.id.amount, estimate.getAmount())
                .setText(R.id.time, estimate.getTime());



    }
}
