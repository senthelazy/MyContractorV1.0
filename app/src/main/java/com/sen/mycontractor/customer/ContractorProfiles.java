package com.sen.mycontractor.customer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sen.mycontractor.R;
import com.sen.mycontractor.customer.adapter.ProfilesRecyclerViewAdapter;
import com.sen.mycontractor.customer.widget.FilterImageView;
import com.sen.mycontractor.data.Estimate;
import com.sen.mycontractor.data.Technician;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractorProfiles extends AppCompatActivity {
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.backToAccountIb)
    FilterImageView back;
    private ProfilesRecyclerViewAdapter adapter;
    private DatabaseReference dataRefer;
    private List<Technician> technicians;
    private Estimate mEstimate;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_profiles);
        ButterKnife.bind(this);
        setupWindowAnimations();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        dataRefer = FirebaseDatabase.getInstance().getReference();
        status = getIntent().getStringExtra("status");
        mEstimate = getIntent().getParcelableExtra("Estimate");
        technicians = new ArrayList<>();
        showProgressDialog();
        if (status.equals("EstimatesCustomer")) {
            singleProfile();
        } else {
            allProfiles();
        }
    }

    private void singleProfile() {
        technicians.clear();
        dataRefer.child("Users").child("Technicians").child(mEstimate.getTechId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                                Technician technician = dataSnapshot.getValue(Technician.class);
                                technicians.add(technician);
                            adapter = new ProfilesRecyclerViewAdapter(ContractorProfiles.this, technicians);
                            fadeOutProgressDialog();
                            recyclerView.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void allProfiles() {
        technicians.clear();
        dataRefer.child("Users").child("Technicians").orderByChild("status").equalTo("technician")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Technician technician = ds.getValue(Technician.class);
                                technicians.add(technician);
                            }
                            adapter = new ProfilesRecyclerViewAdapter(ContractorProfiles.this, technicians);
                            fadeOutProgressDialog();
                            recyclerView.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @OnClick(R.id.backToAccountIb)
    public void back() {
        super.onBackPressed();
    }


    private void setupWindowAnimations() {
        Slide enterTransition = new Slide();
        enterTransition.setSlideEdge(Gravity.RIGHT);
        enterTransition.setDuration(500);
        getWindow().setEnterTransition(enterTransition);
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(500);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
        getWindow().setAllowReturnTransitionOverlap(false);
        getWindow().setAllowEnterTransitionOverlap(true);
    }

    private void showProgressDialog() {
        progressBar.setAlpha(1f);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void fadeOutProgressDialog() {
        progressBar.animate().alpha(0f).setDuration(10).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        }).start();
    }

}
