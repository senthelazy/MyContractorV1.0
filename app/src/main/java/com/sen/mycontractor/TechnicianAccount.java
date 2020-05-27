package com.sen.mycontractor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class TechnicianAccount extends Activity {
    private GridView mGridView;

    private String letterList[] = {"", "", "", "", "", ""};

    private int dashBoardIcons[] = {R.drawable.job_estimates_click,R.drawable.post_new_request_click, R.drawable.my_job_request_click,  R.drawable.messages_click,
            R.drawable.reviews_click, R.drawable.profile_click};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_account);
    }
}
