package com.sen.mycontractor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sen.mycontractor.custom.GridAdapter;

public class CustomerAccount extends Activity {
    private GridView mGridView;

    private String letterList[] = {"", "", "", "", "", ""};

    private int dashBoardIcons[] = {R.drawable.post_new_request_click, R.drawable.my_job_request_click, R.drawable.job_estimates_click, R.drawable.messages_click,
            R.drawable.reviews_click, R.drawable.profile_click};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_account);

        mGridView = (GridView) findViewById(R.id.dashboardGv);
        GridAdapter mGridAdapter = new GridAdapter(CustomerAccount.this, dashBoardIcons, letterList);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }

        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View view1, int i, long l) {
                switch (i) {
                    case 0:
                        Intent intent1 = new Intent(CustomerAccount.this, LocationDetermine.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case 1:
                        Intent intent2 = new Intent(CustomerAccount.this, MyRequests.class);
                        startActivity(intent2);
                        break;

                }

            }
        });
    }
}
