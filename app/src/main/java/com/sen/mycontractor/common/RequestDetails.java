package com.sen.mycontractor.common;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sen.mycontractor.R;
import com.sen.mycontractor.customer.widget.FilterImageView;
import com.sen.mycontractor.data.Estimate;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.customer.widget.AlbumViewPager;
import com.sen.mycontractor.customer.widget.MatrixImageView;
import com.sen.mycontractor.technician.SendEstimatePopup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestDetails extends BaseActivity implements MatrixImageView.OnSingleTapListener {
    private Project mProject;
    private VideoView requestVV;
    private ImageButton playBtn;
    private MediaController mMediaController;
    private HorizontalScrollView scrollView;
    private int size;
    private int padding;
    private FrameLayout pagerContainer;
    private RelativeLayout allDetailsRl;
    private AlbumViewPager albumViewPager;
    private LinearLayout picContainer;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private FilterImageView backToAccountIb;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private String status;
    private Estimate mEstimate;
    @BindView(R.id.category)
    TextView category;
    @BindView(R.id.created_at)
    TextView createdAt;
    @BindView(R.id.headerTv)
    TextView headerTv;
    @BindView(R.id.create_estimate)
    Button createEstimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        ButterKnife.bind(this);
        setupWindowAnimations();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mProject = getIntent().getParcelableExtra("Project");
        mEstimate=getIntent().getParcelableExtra("Estimate");
        status = getIntent().getStringExtra("status");
        if (status.equals("technician")) {
            createEstimate.setVisibility(View.VISIBLE);
        }
        TextView requestDescriptionTv = findViewById(R.id.requestDescriptionTv);
        requestVV = findViewById(R.id.requestVV);
        requestVV.setFocusable(false);
        mMediaController = new MediaController(this);
        playBtn = findViewById(R.id.playVideoBtn);
        pagerContainer = findViewById(R.id.pager_view);
        allDetailsRl = findViewById(R.id.allDetailsRl);
        albumViewPager = findViewById(R.id.album_view_pager);
        picContainer = findViewById(R.id.photosLl);
        size = (int) getResources().getDimension(R.dimen.size_100);
        padding = (int) getResources().getDimension(R.dimen.padding_10);
        requestDescriptionTv.setText(mProject.getJobDescription());
        category.setText(mProject.getCategory());
        createdAt.setText(mProject.getCreatedAt());
        if (!mProject.getVideoUrl().equals("")) {
            requestVV.setVideoURI(Uri.parse(mProject.getVideoUrl()));
            playBtn.setVisibility(View.VISIBLE);
            requestVV.setVisibility(View.VISIBLE);
        }
        backToAccountIb = findViewById(R.id.backToAccountIb);
        backToAccountIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });

        requestVV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                playVideo();
                return true;
            }
        });
        scrollView = findViewById(R.id.photosSv);

        for (int i = 0; i < mProject.getImagesThumbnailUri().size(); i++) {
            String image_url = mProject.getImagesThumbnailUri().get(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.rightMargin = padding;
            FilterImageView imageView = new FilterImageView(this);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(false)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
            imageLoader.displayImage(image_url, new ImageViewAware(imageView), options);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < picContainer.getChildCount(); i++) {
                        if (view == picContainer.getChildAt(i)) {
                            showViewPager(i);
                        }
                    }
                }
            });
            picContainer.addView(imageView);
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 50L);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.playVideoBtn:
                        playVideo();
                        break;
                    default:

                        break;
                }
            }
        };
        playBtn.setOnClickListener(listener);
        albumViewPager.setOnPageChangeListener(pageChangeListener);
        albumViewPager.setOnSingleTapListener(this);
    }

    @OnClick(R.id.create_estimate)
    public void createEstimate() {
        Intent popupIntent = new Intent(RequestDetails.this, SendEstimatePopup.class);
        popupIntent.putExtra("Project", mProject);
        startActivity(popupIntent);

    }

    public void playVideo() {
        requestVV.setMediaController(mMediaController);
        mMediaController.setAnchorView(requestVV);
        requestVV.start();
        playBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSingleTap() {
        hideViewPager();
    }

    @Override
    public void onBackPressed() {
        if (pagerContainer.getVisibility() != View.VISIBLE) {
            super.onBackPressed();
        } else {
            hideViewPager();
        }
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
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    private void showViewPager(int index) {
        pagerContainer.setVisibility(View.VISIBLE);
        allDetailsRl.setVisibility(View.GONE);
        if (status.equals("technician")) {
            createEstimate.setVisibility(View.GONE);
        }
        albumViewPager.setAdapter(albumViewPager.new ViewPagerAdapter(mProject.getImagesOriginalUri()));
        albumViewPager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }


    private void hideViewPager() {
        pagerContainer.setVisibility(View.GONE);
        allDetailsRl.setVisibility(View.VISIBLE);
        if (status.equals("technician")) {
            createEstimate.setVisibility(View.VISIBLE);
        }
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }
}
