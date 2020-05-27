package com.sen.mycontractor.technician;


import android.app.Activity;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Estimate;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.common.FirebaseHelper;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendEstimatePopup extends Activity {
    private int width, height;
    private Project mProject;
    private DatabaseReference databaseRef;
    private FirebaseUser mUser;
    @BindView(R.id.amountEt)
    EditText amountEt;
    @BindView(R.id.timeEt)
    EditText timeEt;
    @BindView(R.id.opinionEt)
    EditText opinionEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_estimate_popup);
        ButterKnife.bind(this);
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseRef=FirebaseDatabase.getInstance().getReference();
        mProject = getIntent().getParcelableExtra("Project");
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        width = mDisplayMetrics.widthPixels;
        height = mDisplayMetrics.heightPixels;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));
    }

    @OnClick(R.id.sendBtn)
    public void sendBtn(){
        String estimateId=Long.toString(UUID.randomUUID().getLeastSignificantBits());
        String mAmount = amountEt.getText().toString().trim();
        String mTime = timeEt.getText().toString().trim();
        String mOpinion = opinionEt.getText().toString().trim();
        Estimate estimate = new Estimate(estimateId,mAmount, mTime, mOpinion,mUser.getDisplayName()
                ,mUser.getPhoneNumber(),mUser.getUid(),Integer.toString(mProject.getID()));
        databaseRef.child("Estimates").child(estimateId).setValue(estimate);
        finish();
    }
}
