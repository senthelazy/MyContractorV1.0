package com.sen.mycontractor.technician;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sen.mycontractor.R;
import com.sen.mycontractor.SelectionOfStatus;
import com.sen.mycontractor.common.FirebaseHelper;

import com.sen.mycontractor.common.Dialogs;
import com.sen.mycontractor.common.RecyclerList;
import com.sen.mycontractor.customer.CustomerAccount;
import com.sen.mycontractor.customer.images.ImageUtils;
import com.sen.mycontractor.customer.widget.FilterImageView;
import com.sen.mycontractor.data.Technician;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TechnicianAccount extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final int SELECT_PHOTO_REQUEST_CODE = 9999;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseHelper mHelper;
    private Technician mTechnician;
    private TextView navName, navEmail;
    private FilterImageView backToAccountBtn;
    private ImageView navPhoto;
    @BindView(R.id.estimates_number)
    TextView estimatesNumber;
    @BindView(R.id.headPhotoIv)
    FilterImageView technicianPhoto;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fullNameTv)
    TextView fullNameTv;
    @BindView(R.id.locationTv)
    TextView locationTv;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.nav_button)
    FilterImageView navBtn;
    @BindView(R.id.chatMessages)
    TextView chatMessages;
    @BindView(R.id.chatMessagesIcon)
    FilterImageView chatMessagesIcon;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.email)
    TextView email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_account);
        ButterKnife.bind(this);
        setupWindowAnimations();
        initInstances();
        initData();
        initView();
        setListeners();
        initImageLoader();

        navigationView.bringToFront();
        navName.setText(mUser.getDisplayName());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navEmail.setText(mUser.getEmail());
            }
        }, 2000);
        fullNameTv.setText(mUser.getDisplayName());

    }

    private void initView() {
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navPhoto = headerLayout.findViewById(R.id.nav_photo);
        navName = headerLayout.findViewById(R.id.nav_name);
        navEmail = headerLayout.findViewById(R.id.nav_email);
    }

    @OnClick(R.id.headPhotoIv)
    public void replacePhoto() {
        Intent intent0 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent0, "Select Picture"), SELECT_PHOTO_REQUEST_CODE);
    }

    @OnClick(R.id.nav_button)
    public void navBtn() {
        drawer.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.estimates_number_text)
    public void estimatesNumberText(){
        estimatesNumber();
    }

    @OnClick(R.id.estimates_number)
    public void estimatesNumber(){
        ActivityOptions option1 = ActivityOptions.makeSceneTransitionAnimation(TechnicianAccount.this);
        Intent intent1 = new Intent(TechnicianAccount.this, RecyclerList.class);
        intent1.putExtra("status", "EstimatesTechnician");
        startActivity(intent1, option1.toBundle());
    }

    @OnClick(R.id.chatMessages)
    public void chatMessages() {
        ActivityOptions option = ActivityOptions.makeSceneTransitionAnimation(TechnicianAccount.this);
        Intent intent1 = new Intent(TechnicianAccount.this, Dialogs.class);
        startActivity(intent1, option.toBundle());
    }

    @OnClick(R.id.chatMessagesIcon)
    public void chatMessagesIcon() {
        chatMessages();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                technicianPhoto.setVisibility(View.INVISIBLE);
                showProgressDialog();
                Uri imageUri = data.getData();
                StorageReference filePath = storageRef.child("PersonalPhotos").child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        databaseRef.child("Users").child("Technicians").child(mUser.getUid()).child("personalPhotoUrl")
                                .setValue(downloadUrl.toString());
                        UserProfileChangeRequest mProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUrl)
                                .build();
                        mUser.updateProfile(mProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    technicianPhoto.setVisibility(View.VISIBLE);
                                    fadeOutProgressDialog();
                                }
                            }
                        });
                        imageLoader.displayImage(downloadUrl.toString(), new ImageViewAware(technicianPhoto), options);
                    }
                });

            }
        }
    }

    private void initImageLoader() {
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .build();

    }



    private void setListeners() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initInstances() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        mTechnician = new Technician();
    }


    private void initData() {
        mTechnician = getIntent().getParcelableExtra("Technician");
        getCurrentTechnicianData();
    }

    private void setupWindowAnimations() {
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(500);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
        getWindow().setAllowReturnTransitionOverlap(false);
        Explode enterTransition = new Explode();
        enterTransition.setDuration(500);
        getWindow().setEnterTransition(enterTransition);
        getWindow().setAllowEnterTransitionOverlap(true);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentEstimatesNumber();
    }

    public void getCurrentEstimatesNumber() {
        databaseRef.child("Estimates").orderByChild("techId").equalTo(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            estimatesNumber.setText(dataSnapshot.getChildrenCount() + "");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_requests_list:
                ActivityOptions option0 = ActivityOptions.makeSceneTransitionAnimation(TechnicianAccount.this);
                Intent intent0 = new Intent(TechnicianAccount.this, RecyclerList.class);
                intent0.putExtra("status", "technician");
                startActivity(intent0, option0.toBundle());
                break;
            case R.id.nav_estimates:
                estimatesNumber();
                break;
            case R.id.nav_messages:
                chatMessages();
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_log_out:
                new AlertDialog.Builder(TechnicianAccount.this)
                        .setTitle("Reminder")
                        .setMessage("Are you sure you want to log out?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        Intent intent = new Intent(TechnicianAccount.this, SelectionOfStatus.class);
                        startActivity(intent);
                        finish();
                    }
                }).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void getCurrentTechnicianData() {
        showProgressDialog();
        databaseRef.child("Users").child("Technicians").child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTechnician = dataSnapshot.getValue(Technician.class);
                        fadeOutProgressDialog();
                        locationTv.setText(mTechnician.getCityName());
                        email.setText(mTechnician.getEmail());
                        phone.setText(mTechnician.getPhone());
                        technicianPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageLoader.displayImage(mTechnician.getPersonalPhotoUrl(), new ImageViewAware(technicianPhoto), options);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setAlpha(1f);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);

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
