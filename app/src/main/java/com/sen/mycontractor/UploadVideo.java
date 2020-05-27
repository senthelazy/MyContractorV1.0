package com.sen.mycontractor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.widget.FilterImageView;

public class UploadVideo extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        mMediaController = new MediaController(this);
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        useVideoBtn=(Button)findViewById(R.id.useVideoBtn);
        mProject=getIntent().getParcelableExtra("Project");

        addVideoBtn = (FilterImageView) findViewById(R.id.addVideoBtn);
        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureVideo();
                useVideoBtn.setEnabled(false);
            }
        });

        useVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mProject.setVideoUrl(downloadUrl.toString());
                }catch (Exception e){

                }
                Intent intent=new Intent(UploadVideo.this, UploadDescription.class);
                intent.putExtra("Project",mProject);
                startActivity(intent);
                finish();
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

    public void captureVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                //mStorageReference = FirebaseStorage.getInstance().getReference();
                StorageReference filePath = mStorageReference.child("Videos").child(videoUri.getLastPathSegment());
                filePath.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                        Toast.makeText(UploadVideo.this, "Video recorded", Toast.LENGTH_LONG).show();
                        downloadUrl = snapshot.getDownloadUrl();
                        playBtn.setVisibility(View.VISIBLE);
                        mVideoView.setVisibility(View.VISIBLE);
                        useVideoBtn.setEnabled(true);
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
}
