package com.sen.mycontractor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sen.mycontractor.data.LocalImage;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.image.ImageHelper;
import com.sen.mycontractor.widget.FilterImageView;

import java.util.List;

public class UploadImages extends Activity {
    private final int GET_CORP_IMAGE = 6557;
    private Project mProject;
    private FilterImageView addImagesFiv;
    private boolean result_OK;
    private int thumbnailSize, thumbnailPadding;
    private DisplayImageOptions mDisplayImageOptions;
    private LinearLayout imagesContainerLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);

        imagesContainerLl=(LinearLayout)findViewById(R.id.imagesContainerLl);

        addImagesFiv = (FilterImageView) findViewById(R.id.addImagesFiv);
        addImagesFiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadImages.this, CustomImagesPicker.class);
                startActivityForResult(intent, GET_CORP_IMAGE);
            }
        });

        mProject = getIntent().getParcelableExtra("Project");
        Button useImagesBtn = (Button) findViewById(R.id.useImagesBtn);
        useImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadImages.this, UploadVideo.class);
                intent.putExtra("Project", mProject);
                startActivity(intent);
            }
        });
        //Toast.makeText(UploadImages.this, "" + mProject.getLocation(), Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_CORP_IMAGE) {
            if (result_OK) {
                result_OK = false;
                List<LocalImage> imagesArrayList = ImageHelper.getImageHelper().getImagesArrayList();
                for (int i = 0; i < imagesArrayList.size(); i++) {
                    FilterImageView filterImageView = new FilterImageView(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
                    layoutParams.rightMargin = thumbnailPadding;
                    filterImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    filterImageView.setLayoutParams(layoutParams);
                    ImageLoader.getInstance().displayImage(imagesArrayList.get(i).getThumbnailUri(), new ImageViewAware(filterImageView)
                            , mDisplayImageOptions, null, null, imagesArrayList.get(i).getOrientation());
                    filterImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (view instanceof FilterImageView) {
                                for (int i = 0; i < imagesContainerLl.getChildCount(); i++) {
                                    if (view == imagesContainerLl.getChildAt(i)) {
                                        showViewPager(i);
                                    }
                                }
                            }
                        }
                    });
                }

            }

        }


    }

    private void showViewPager(int i){


    }
}
