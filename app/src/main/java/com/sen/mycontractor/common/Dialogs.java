package com.sen.mycontractor.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sen.mycontractor.R;
import com.sen.mycontractor.customer.CustomerAccount;
import com.sen.mycontractor.data.Dialog;
import com.sen.mycontractor.data.Message;
import com.sen.mycontractor.data.Technician;
import com.sen.mycontractor.data.User;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;

public class Dialogs extends CommonDialogsActivity {

    private DatabaseReference databaseRef;
    private ArrayList<Dialog> dialogs = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private FirebaseHelper mHelper;
    private FirebaseUser mUser;
    private Dialog existDialog;
    private DialogsList dialogsList;
    private TextView noContact;
    private Dialog passDialog, fromDataDialog;
    private ProgressBar progressBar;

    public static void open(Activity activity, Dialog dialog) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity);
        Intent intent = new Intent(activity, Dialogs.class);
        intent.putExtra("Dialog", dialog);
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        setupWindowAnimations();
        showProgressDialog();
        passDialog = getIntent().getParcelableExtra("Dialog");
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mHelper = new FirebaseHelper(databaseRef);
        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        noContact = (TextView) findViewById(R.id.no_contact);
        createDialogsList();

    }

    @Override
    public void onBackPressed() {
        /*
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        Intent intent = new Intent(this, CustomerAccount.class);
        startActivity(intent, options.toBundle());*/
        super.onBackPressed();
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        databaseRef.child("Dialogs").child(mUser.getUid()).child(dialog.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            fromDataDialog = dataSnapshot.getValue(Dialog.class);
                            ChatPage.open(Dialogs.this, fromDataDialog);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initAdapter(ArrayList<Dialog> thisDialogs) {
        super.dialogsListAdapter = new DialogsListAdapter<>(super.imageLoader);
        //dialogsListAdapter.updateDialogWithMessage(dialogId, message);
        super.dialogsListAdapter.setItems(thisDialogs);
        super.dialogsListAdapter.setOnDialogClickListener(this);
        super.dialogsListAdapter.setOnDialogLongClickListener(this);
        dialogsList.setAdapter(super.dialogsListAdapter,false);
        fadeOutProgressDialog();
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
    }

    public void createDialogsList() {
        dialogs.clear();
        users.clear();
        databaseRef.child("Dialogs").child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Dialog dialog = ds.getValue(Dialog.class);
                                for (int i = 0; i < dialog.getUsersUIds().size(); i++) {
                                    databaseRef.child("Users").child("Technicians").child(dialog.getUsersUIds().get(i))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Technician technician = dataSnapshot.getValue(Technician.class);
                                                        User user = new User(technician.getUid(), technician.getFullName()
                                                                , technician.getPersonalPhotoUrl(), true);
                                                        users.add(user);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                                Message lastMessage = new Message(dialog.getId(), dialog.getMessageId(), dialog.getText(), dialog.getDate(),
                                        dialog.getUserId(), dialog.getUserName(), dialog.getUserPhotoUrl(), dialog.isOnline(),
                                        dialog.getImageUrl(), dialog.getVoiceUrl(), dialog.getDuration());
                                existDialog = new Dialog(dialog.getId(), dialog.getDialogName(),
                                        dialog.getDialogPhoto(), users, lastMessage, dialog.getUnreadCount());
                            }
                            dialogs.add(existDialog);
                            initAdapter(dialogs);
                        } else {
                            noContact.setVisibility(View.VISIBLE);
                            dialogsList.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    private void onNewMessage(String dialogId, Message message) {
        dialogsListAdapter.updateDialogWithMessage(dialogId, message);
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsListAdapter.addItem(dialog);
    }


}
