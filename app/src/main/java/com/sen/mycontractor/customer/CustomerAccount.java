package com.sen.mycontractor.customer;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import com.sen.mycontractor.common.Dialogs;
import com.sen.mycontractor.common.RecyclerList;
import com.sen.mycontractor.customer.widget.FilterImageView;
import com.sen.mycontractor.data.Customer;
import com.sen.mycontractor.data.Project;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomerAccount extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final int MY_PERMISSION_REQUEST_CODE_FOR_CALL_PHONE_EXTERNAL_STORAGE=8888;
    private static final int SELECT_PHOTO_REQUEST_CODE = 9999;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference storageRef;
    private Customer mCustomer;
    private TextView navName, navEmail;
    private Project mProject;
    private UserProfileChangeRequest mProfileChangeRequest;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Uri imageUri;
    private FilterImageView backToAccountBtn;
    private ImageView navPhoto;
    @BindView(R.id.fullNameTv)
    TextView fullNameTv;
    @BindView(R.id.locationTv)
    TextView locationTv;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.request_number)
    TextView requestNumber;
    @BindView(R.id.request_done)
    TextView requestDone;
    @BindView(R.id.chatMessages)
    TextView chatMessages;
    @BindView(R.id.chatMessagesIcon)
    FilterImageView chatMessagesIcon;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.nav_button)
    FilterImageView navBtn;
    @BindView(R.id.headPhotoIv)
    FilterImageView customerPhoto;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_account);
        ButterKnife.bind(this);
        checkPermission();
        setupWindowAnimations();
        initInstances();
        initData();
        initView();
        setListeners();
        createNewRequest();
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
        customerPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            android.Manifest.permission.CALL_PHONE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }, MY_PERMISSION_REQUEST_CODE_FOR_CALL_PHONE_EXTERNAL_STORAGE);
        } else {

        }
    }



    public void initView() {
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navPhoto = headerLayout.findViewById(R.id.nav_photo);
        navName = headerLayout.findViewById(R.id.nav_name);
        navEmail = headerLayout.findViewById(R.id.nav_email);
        backToAccountBtn = headerLayout.findViewById(R.id.back);
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

    private void createNewRequest() {
        if (mProject != null) {
            Random randomID = new Random();
            mProject.setID(randomID.nextInt());
            mProject.setCustomerUid(mUser.getUid());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm a", Locale.getDefault());
            String createdAt = sdf.format(new Date());
            mProject.setCreatedAt(createdAt);
            databaseRef.child("Projects").child(Integer.toString(mProject.getID())).setValue(mProject);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    private void setupWindowAnimations() {
        Explode enterTransition = new Explode();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                customerPhoto.setVisibility(View.INVISIBLE);
                showProgressDialog();
                imageUri = data.getData();
                StorageReference filePath = storageRef.child("PersonalPhotos").child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        databaseRef.child("Users").child("Customers").child(mUser.getUid()).child("personalPhotoUrl")
                                .setValue(downloadUrl.toString());
                        mProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUrl)
                                .build();
                        mUser.updateProfile(mProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    customerPhoto.setVisibility(View.VISIBLE);
                                    fadeOutProgressDialog();
                                }
                            }
                        });
                        imageLoader.displayImage(downloadUrl.toString(), new ImageViewAware(customerPhoto), options);
                    }
                });

            }
        }
    }

    public void getCurrentCustomerData() {
        databaseRef.child("Users").child("Customers").child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mCustomer = dataSnapshot.getValue(Customer.class);
                        locationTv.setText(mCustomer.getLocation());
                        email.setText(mCustomer.getEmail());
                        phone.setText(mCustomer.getPhone());
                        imageLoader.displayImage(mCustomer.getPersonalPhotoUrl(), new ImageViewAware(customerPhoto), options);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getCurrentRequestsNumber() {
        databaseRef.child("Projects").orderByChild("customerUid").equalTo(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        requestNumber.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void initData() {
        getCurrentCustomerData();
        getCurrentRequestsNumber();
        mProject = getIntent().getParcelableExtra("Project");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentRequestsNumber();
    }


    public void initInstances() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        mCustomer = new Customer();

    }

    public void setListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        backToAccountBtn.setOnClickListener(this);
    }

    @OnClick(R.id.my_request_text)
    public void myRequestText() {
        requestNumber();
    }

    @OnClick(R.id.nav_button)
    public void navBtn() {
        drawer.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.headPhotoIv)
    public void headPhotoIv() {
        Intent intent0 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent0, "Select Picture"), SELECT_PHOTO_REQUEST_CODE);
    }

    @OnClick(R.id.chatMessages)
    public void chatMessages() {
        ActivityOptions option = ActivityOptions.makeSceneTransitionAnimation(CustomerAccount.this);
        Intent intent1 = new Intent(CustomerAccount.this, Dialogs.class);
        startActivity(intent1, option.toBundle());
    }

    @OnClick(R.id.chatMessagesIcon)
    public void chatMessagesIcon() {
        ActivityOptions option2 = ActivityOptions.makeSceneTransitionAnimation(CustomerAccount.this);
        Intent intent2 = new Intent(CustomerAccount.this, Dialogs.class);
        startActivity(intent2, option2.toBundle());
    }

    @OnClick(R.id.request_number)
    public void requestNumber() {
        ActivityOptions option3 = ActivityOptions.makeSceneTransitionAnimation(CustomerAccount.this);
        Intent intent3 = new Intent(CustomerAccount.this, RecyclerList.class);
        intent3.putExtra("status", "customer");
        startActivity(intent3, option3.toBundle());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_post_new_request:
                Project newProject = new Project();
                ActivityOptions option0 = ActivityOptions.makeSceneTransitionAnimation(CustomerAccount.this);
                Intent intent0 = new Intent(CustomerAccount.this, LocationDetermine.class);
                intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent0.putExtra("Project", newProject);
                startActivity(intent0, option0.toBundle());
                break;
            case R.id.nav_my_request:
                ActivityOptions option1 = ActivityOptions.makeSceneTransitionAnimation(CustomerAccount.this);
                Intent intent1 = new Intent(CustomerAccount.this, RecyclerList.class);
                intent1.putExtra("status", "customer");
                startActivity(intent1, option1.toBundle());
                break;
            case R.id.nav_messages:
                ActivityOptions option2 = ActivityOptions.makeSceneTransitionAnimation(CustomerAccount.this);
                Intent intent2 = new Intent(CustomerAccount.this, Dialogs.class);
                startActivity(intent2, option2.toBundle());
                break;
            case R.id.nav_profiles:
                ActivityOptions option3 = ActivityOptions.makeSceneTransitionAnimation(CustomerAccount.this);
                Intent intent3 = new Intent(CustomerAccount.this, ContractorProfiles.class);
                intent3.putExtra("status", "ContractorsProfiles");
                startActivity(intent3, option3.toBundle());
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_log_out:
                new AlertDialog.Builder(CustomerAccount.this)
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
                        Intent intent = new Intent(CustomerAccount.this, SelectionOfStatus.class);
                        startActivity(intent);
                        finish();
                    }
                }).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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

