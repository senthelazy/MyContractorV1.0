package com.sen.mycontractor.customer;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import com.sen.mycontractor.common.AppContext;
import com.sen.mycontractor.R;
import com.sen.mycontractor.customer.images.ImageUtils;
import com.sen.mycontractor.customer.images.LocalAlbum;
import com.sen.mycontractor.customer.images.LocalImageHelper;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.customer.widget.AlbumViewPager;
import com.sen.mycontractor.customer.widget.FilterImageView;
import com.sen.mycontractor.customer.widget.MatrixImageView;

import java.util.ArrayList;
import java.util.List;

public class UploadImages extends AppCompatActivity implements View.OnClickListener, MatrixImageView.OnSingleTapListener {
    private final int MY_PERMISSION_REQUEST_CODE = 7011;
    private Project mProject;
    private TextView picRemain;
    private ImageView add;
    private LinearLayout picContainer;
    private List<LocalImageHelper.LocalFile> pictures = new ArrayList<>();
    HorizontalScrollView scrollView;
    View editContainer;
    View pagerContainer;
    AlbumViewPager viewpager;
    ImageView mBackView;
    private TextView mCountView, upLoadingView;
    View mHeaderBar;
    ImageView delete;
    int size;
    int padding;
    DisplayImageOptions options;
    Button useImagesBtn;
    private FirebaseStorage mStorage;
    ArrayList<String> originalLastPathSegment = new ArrayList<>();
    ArrayList<String> thumbnailLastPathSegment = new ArrayList<>();
    ArrayList<String> originalUriArray = new ArrayList<>();
    ArrayList<String> thumbnailUriArray = new ArrayList<>();
    private final AppContext context = AppContext.getInstance();
    private ProgressBar useImagesProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        setupWindowAnimations();

        checkPermission();
        context.init();
        mStorage = FirebaseStorage.getInstance();
        mProject = getIntent().getParcelableExtra("Project");
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.add_images)
                .showImageOnFail(R.drawable.add_images)
                .showImageOnLoading(R.drawable.add_images)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        initViews();
        initData();
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


    private void initViews() {
        useImagesBtn = (Button) findViewById(R.id.useImagesBtn);
        picRemain = (TextView) findViewById(R.id.post_pic_remain);
        add = (ImageView) findViewById(R.id.post_add_pic);
        picContainer = (LinearLayout) findViewById(R.id.post_pic_container);
        scrollView = (HorizontalScrollView) findViewById(R.id.post_scrollview);
        viewpager = (AlbumViewPager) findViewById(R.id.albumviewpager);
        mBackView = (ImageView) findViewById(R.id.header_bar_photo_back);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        mHeaderBar = findViewById(R.id.album_item_header_bar);
        delete = (ImageView) findViewById(R.id.header_bar_photo_delete);
        editContainer = findViewById(R.id.post_edit_container);
        pagerContainer = findViewById(R.id.pagerview);
        upLoadingView = (TextView) findViewById(R.id.upLoadingView);
        useImagesProgressBar = (ProgressBar) findViewById(R.id.progress_bar_use_images);
        delete.setVisibility(View.VISIBLE);
        viewpager.setOnPageChangeListener(pageChangeListener);
        useImagesBtn.setOnClickListener(this);
        viewpager.setOnSingleTapListener(this);
        mBackView.setOnClickListener(this);
        mCountView.setOnClickListener(this);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);
    }


    private void initData() {
        size = (int) getResources().getDimension(R.dimen.size_100);
        padding = (int) getResources().getDimension(R.dimen.padding_10);

    }

    @Override
    public void onBackPressed() {
        if (pagerContainer.getVisibility() != View.VISIBLE) {
        } else {
            hideViewPager();
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }, MY_PERMISSION_REQUEST_CODE);
        } else {

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }
    }

    private void animateButtonWidth() {
        useImagesBtn.setText("");
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(useImagesBtn.getMeasuredWidth(), getButtonSize());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = useImagesBtn.getLayoutParams();
                layoutParams.width = val;
                useImagesBtn.requestLayout();
            }
        });
        mValueAnimator.setDuration(250);
        mValueAnimator.start();
    }

    private void showProgressDialog() {
        useImagesProgressBar.setAlpha(1f);
        useImagesProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        useImagesProgressBar.setVisibility(View.VISIBLE);
    }

    private int getButtonSize() {
        return (int) getResources().getDimension(R.dimen.button_size);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.useImagesBtn:
                if (pictures.size() != 0) {
                    upload();
                    upLoadingView.setVisibility(View.VISIBLE);
                    animateButtonWidth();
                    showProgressDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UploadImages.this);
                            Intent intent = new Intent(UploadImages.this, UploadVideo.class);
                            intent.putExtra("Project", mProject);
                            startActivity(intent, options.toBundle());
                            finishAfterTransition();
                        }
                    }, pictures.size() * 2000);
                } else {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UploadImages.this);
                    Intent toNextActivity = new Intent(UploadImages.this, UploadVideo.class);
                    toNextActivity.putExtra("Project", mProject);
                    startActivity(toNextActivity, options.toBundle());
                    finishAfterTransition();
                }
                break;
            case R.id.header_bar_photo_back:
            case R.id.header_bar_photo_count:
                hideViewPager();
                break;
            case R.id.header_bar_photo_delete:
                final int index = viewpager.getCurrentItem();
                new AlertDialog.Builder(this)
                        .setTitle("Reminder")
                        .setMessage("Want to remove this photo from selection?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                pictures.remove(index);
                                if (pictures.size() == 9) {
                                    add.setVisibility(View.GONE);
                                } else {
                                    add.setVisibility(View.VISIBLE);
                                }
                                if (pictures.size() == 0) {
                                    hideViewPager();
                                }
                                picContainer.removeView(picContainer.getChildAt(index));
                                picRemain.setText(pictures.size() + "/9");
                                mCountView.setText((viewpager.getCurrentItem() + 1) + "/" + pictures.size());
                                viewpager.getAdapter().notifyDataSetChanged();
                                LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                            }
                        }).show();
                break;
            case R.id.post_add_pic:
                Intent selectFromAlbum = new Intent(UploadImages.this, LocalAlbum.class);
                startActivityForResult(selectFromAlbum, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
                break;
            default:
                if (view instanceof FilterImageView) {
                    for (int i = 0; i < picContainer.getChildCount(); i++) {
                        if (view == picContainer.getChildAt(i)) {
                            showViewPager(i);
                        }
                    }
                }
                break;
        }
    }

    private void upload() {
        for (int i = 0; i < pictures.size(); i++) {
            Uri photoUri = Uri.parse(pictures.get(i).getOriginalUri());
            originalLastPathSegment.add(photoUri.getLastPathSegment());
            StorageReference filePath = mStorage.getReference().child("Images").child(photoUri.getLastPathSegment());
            filePath.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    originalUriArray.add(taskSnapshot.getDownloadUrl().toString());


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadImages.this, "UploadImage failed", Toast.LENGTH_SHORT).show();
                }
            });
            Uri photoUri2 = Uri.parse(pictures.get(i).getThumbnailUri());
            thumbnailLastPathSegment.add(photoUri2.getLastPathSegment());
            StorageReference filePath2 = mStorage.getReference().child("ThumbImages").child(photoUri2.getLastPathSegment());
            filePath2.putFile(photoUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    thumbnailUriArray.add(taskSnapshot.getDownloadUrl().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadImages.this, "UploadImage failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        mProject.setImagesOriginalUri(originalUriArray);
        mProject.setImagesThumbnailUri(thumbnailUriArray);
        mProject.setOriginalLastPathSegment(originalLastPathSegment);
        mProject.setThumbnailLastPathSegment(thumbnailLastPathSegment);
    }


    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (viewpager.getAdapter() != null) {
                String text = (position + 1) + "/" + viewpager.getAdapter().getCount();
                mCountView.setText(text);
            } else {
                mCountView.setText("0/0");
            }
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
        editContainer.setVisibility(View.GONE);
        useImagesBtn.setVisibility(View.GONE);
        viewpager.setAdapter(viewpager.new LocalViewPagerAdapter(pictures));
        viewpager.setCurrentItem(index);
        mCountView.setText((index + 1) + "/" + pictures.size());
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
        editContainer.setVisibility(View.VISIBLE);
        useImagesBtn.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    @Override
    public void onSingleTap() {
        hideViewPager();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                if (LocalImageHelper.getInstance().isResultOk()) {
                    LocalImageHelper.getInstance().setResultOk(false);
                    List<LocalImageHelper.LocalFile> files = LocalImageHelper.getInstance().getCheckedItems();
                    for (int i = 0; i < files.size(); i++) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                        params.rightMargin = padding;
                        FilterImageView imageView = new FilterImageView(this);
                        imageView.setLayoutParams(params);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(files.get(i).getThumbnailUri(), new ImageViewAware(imageView), options,
                                null, null, files.get(i).getOrientation());
                        imageView.setOnClickListener(this);
                        pictures.add(files.get(i));
                        if (pictures.size() == 9) {
                            add.setVisibility(View.GONE);
                        } else {
                            add.setVisibility(View.VISIBLE);
                        }
                        picContainer.addView(imageView, picContainer.getChildCount() - 1);
                        picRemain.setText(pictures.size() + "/9");
                        LocalImageHelper.getInstance().setCurrentSize(pictures.size());

                    }
                    files.clear();
                    LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    }, 50L);
                }
                //upload();
                LocalImageHelper.getInstance().getCheckedItems().clear();
                useImagesBtn.setText(getResources().getText(R.string.use_images));
                break;
            default:
                break;
        }
    }


}
