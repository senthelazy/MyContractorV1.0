package com.sen.mycontractor.common;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.sen.mycontractor.data.Dialog;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

public class CommonDialogsActivity extends AppCompatActivity implements
        DialogsListAdapter.OnDialogClickListener<Dialog>, DialogsListAdapter.OnDialogLongClickListener<Dialog> {
    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader=new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(CommonDialogsActivity.this).load(url).into(imageView);
            }
        };
    }
    @Override
    public void onDialogClick(Dialog dialog) {
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        ToastHelper.showToast(
                this,
                dialog.getDialogName(),
                false);
    }
}
