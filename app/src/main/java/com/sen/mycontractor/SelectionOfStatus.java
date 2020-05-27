package com.sen.mycontractor;

import android.*;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.sen.mycontractor.common.ToastHelper;
import com.sen.mycontractor.customer.CustomerAccount;
import com.sen.mycontractor.customer.LocationDetermine;

import com.sen.mycontractor.technician.TechnicianAccount;
import com.sen.mycontractor.technician.TechnicianLogin;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class SelectionOfStatus extends AppCompatActivity {
    private final int MY_PERMISSION_REQUEST_CODE = 7000;
    private final int MY_PERMISSION_REQUEST_CODE_FOR_MANAGE_DOCUMENTS = 998;
    private String mStatus;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FrameLayout temBtn;
    private TextView temText;
    private ProgressBar temBar;
    private View temReveal;
    @BindView(R.id.hireBtn)
    FrameLayout hireBtn;
    @BindView(R.id.beingHiredBtn)
    FrameLayout beingHiredBtn;
    @BindView(R.id.sText)
    TextView sText;
    @BindView(R.id.sText2)
    TextView sText2;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.progress_bar2)
    ProgressBar progressBar2;
    @BindView(R.id.revealView)
    View revealView;
    @BindView(R.id.revealView2)
    View revealView2;
    @BindView(R.id.repair)
    ImageView repair;
    @BindView(R.id.house)
    ImageView house;
    @BindView(R.id.allViews)
    RelativeLayout allViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_of_status);
        ButterKnife.bind(this);
        setupWindowAnimations();
        checkManageDocumentsPermission();
        checkLocationPermission();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
    }

    @OnClick(R.id.hireBtn)
    public void hireBtn() {
        animateButtonWidth(hireBtn);
        fadeOutTextAndShowProgressDialog(sText, progressBar);
        house.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealButton(hireBtn, sText, revealView);
                fadeOutProgressDialog(progressBar);
                customerActivity();
            }
        }, 500);
    }

    @OnClick(R.id.beingHiredBtn)
    public void beingHiredBtn() {
        animateButtonWidth(beingHiredBtn);
        fadeOutTextAndShowProgressDialog(sText2, progressBar2);
        repair.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealButton(beingHiredBtn, sText2, revealView2);
                fadeOutProgressDialog(progressBar2);
                technicianActivity();
            }
        }, 500);
    }

    //check if user has technician account, or go to login page.
    private void technicianActivity() {
        if (mUser != null) {
            mDatabaseReference.child("Users").child("Technicians").child(mUser.getUid()).child("status")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mStatus = snapshot.getValue(String.class);
                            if (mStatus != null) {
                                if (mStatus.equals("technician")) {
                                    Intent intent1 = new Intent(SelectionOfStatus.this, TechnicianAccount.class);
                                    startActivity(intent1);
                                    finishAfterTransition();
                                } else {
                                    Intent intent2 = new Intent(SelectionOfStatus.this, TechnicianLogin.class);
                                    startActivity(intent2);
                                    finishAfterTransition();
                                }
                            } else {
                                startActivity(new Intent(SelectionOfStatus.this, TechnicianLogin.class));
                                finishAfterTransition();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
        } else {
            startActivity(new Intent(SelectionOfStatus.this, TechnicianLogin.class));
            finishAfterTransition();
        }
    }

    //animation for transition of activities
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


    private void fadeOutTextAndShowProgressDialog(TextView text, ProgressBar progressBar) {
        this.temBar = progressBar;
        text.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showProgressDialog(temBar);
            }
        }).start();
    }

    private void showProgressDialog(ProgressBar progressBar) {
        progressBar.setAlpha(1f);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void animateButtonWidth(FrameLayout btn) {
        this.temBtn = btn;
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(temBtn.getMeasuredWidth(), getButtonSize());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = temBtn.getLayoutParams();
                layoutParams.width = val;
                temBtn.requestLayout();
            }
        });
        mValueAnimator.setDuration(250);
        mValueAnimator.start();
    }

    @Override
    public void onBackPressed() {

    }

    //check if user has customer account, or go to login page.
    private void customerActivity() {
        if (mUser != null) {
            mDatabaseReference.child("Users").child("Customers").child(mUser.getUid()).child("status")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mStatus = snapshot.getValue(String.class);
                            if (mStatus != null) {
                                if (mStatus.equals("customer")) {
                                    Intent intent = new Intent(SelectionOfStatus.this, CustomerAccount.class);
                                    startActivity(intent);
                                    finishAfterTransition();
                                } else {

                                    startActivity(new Intent(SelectionOfStatus.this, LocationDetermine.class));
                                    finishAfterTransition();
                                }
                            } else {

                                startActivity(new Intent(SelectionOfStatus.this, LocationDetermine.class));
                                finishAfterTransition();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
        } else {

            startActivity(new Intent(SelectionOfStatus.this, LocationDetermine.class));
            finishAfterTransition();
        }
    }

    private void fadeOutProgressDialog(ProgressBar progressBar) {
        progressBar.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        }).start();
        //allViews.setVisibility(View.GONE);
    }

    private void revealButton(FrameLayout frameLayout, TextView text, View revealView) {
        this.temBtn = frameLayout;
        this.temText = text;
        this.temReveal = revealView;
        temBtn.setElevation(0f);
        temReveal.setVisibility(View.VISIBLE);
        int cx = temReveal.getWidth();
        int cy = temReveal.getHeight();
        int x = (int) (getButtonSize() / 2 + temBtn.getX());
        int y = (int) (getButtonSize() / 2 + temBtn.getY());
        float finalRadius = Math.max(cx, cy) * 1.2f;
        Animator reveal = ViewAnimationUtils.createCircularReveal(temReveal, x, y, getButtonSize(), finalRadius);
        reveal.setDuration(250);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reset(animation);
            }

            private void reset(Animator animation) {
                super.onAnimationEnd(animation);
                temReveal.setVisibility(INVISIBLE);
                temText.setVisibility(VISIBLE);
                temText.setAlpha(1f);
                temBtn.setElevation(4f);
                ViewGroup.LayoutParams layoutParams = temBtn.getLayoutParams();
                layoutParams.width = (int) (getResources().getDisplayMetrics().density * 300);
                temBtn.requestLayout();
            }
        });
        reveal.start();
    }

    private int getButtonSize() {
        return (int) getResources().getDimension(R.dimen.button_size);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    }, MY_PERMISSION_REQUEST_CODE);
        } else {

        }
    }

    private void checkManageDocumentsPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            android.Manifest.permission.MANAGE_DOCUMENTS
                    }, MY_PERMISSION_REQUEST_CODE_FOR_MANAGE_DOCUMENTS);
        } else {
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //ToastHelper.showToast(this, "Not possible to use this app with out the permission!", true);
                }
                break;
            case MY_PERMISSION_REQUEST_CODE_FOR_MANAGE_DOCUMENTS:
                break;
        }
    }


}
