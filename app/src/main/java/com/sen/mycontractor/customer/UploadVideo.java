package com.sen.mycontractor.customer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sen.mycontractor.R;

import com.sen.mycontractor.common.ToastHelper;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.customer.widget.FilterImageView;

public class UploadVideo extends AppCompatActivity  {
    private static final int VIDEO_REQUEST_CODE = 3;
    private FilterImageView addVideoBtn;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private Uri downloadUrl;
    private Project mProject;
    private VideoView mVideoView;
    private ImageButton playBtn;
    private Button useVideoBtn;
    MediaController mMediaController;
    private boolean isDeleted;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        setupWindowAnimations();

        progressBar=(ProgressBar)findViewById(R.id.progressBarForVideo) ;
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        mMediaController = new MediaController(this);
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        useVideoBtn = (Button) findViewById(R.id.useVideoBtn);
        mProject = getIntent().getParcelableExtra("Project");
        addVideoBtn = (FilterImageView) findViewById(R.id.addVideoBtn);
        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureVideo();
                useVideoBtn.setVisibility(View.GONE);
                showProgressDialog();
            }
        });

        useVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mProject.setVideoUrl(downloadUrl.toString());
                } catch (Exception e) {
                }
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UploadVideo.this);
                Intent intent = new Intent(UploadVideo.this, UploadDescription.class);
                intent.putExtra("Project", mProject);
                startActivity(intent, options.toBundle());
                finishAfterTransition();
            }
        });

        playBtn = (ImageButton) findViewById(R.id.playBtn);
        playBtn.setVisibility(View.INVISIBLE);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });

        mVideoView.setVisibility(View.INVISIBLE);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                playVideo();
                return true;
            }
        });
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


    public void captureVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, VIDEO_REQUEST_CODE);
        }
    }

    public void playVideo() {
        mVideoView.setVideoURI(downloadUrl);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setAnchorView(mVideoView);
        mVideoView.start();
        playBtn.setVisibility(View.INVISIBLE);
    }

    private void showProgressDialog() {
        progressBar.setAlpha(1f);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void fadeOutProgressDialog() {
        progressBar.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        }).start();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                mProject.setVideoLastPathSegment(videoUri.getLastPathSegment());
                StorageReference filePath = mStorageReference.child("Videos").child(videoUri.getLastPathSegment());
                filePath.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                        Toast.makeText(UploadVideo.this, "Video recorded", Toast.LENGTH_LONG).show();
                        downloadUrl = snapshot.getDownloadUrl();
                        playBtn.setVisibility(View.VISIBLE);
                        mVideoView.setVisibility(View.VISIBLE);
                        addVideoBtn.setVisibility(View.GONE);
                        useVideoBtn.setText(getResources().getText(R.string.use_video));
                        useVideoBtn.setVisibility(View.VISIBLE);
                        fadeOutProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadVideo.this, "Fail to upload video....", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(UploadVideo.this, "Video record failed...", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }





}
