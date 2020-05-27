package com.sen.mycontractor.common;

import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.ValueEventListener;

import com.sen.mycontractor.data.Customer;
import com.sen.mycontractor.data.Dialog;
import com.sen.mycontractor.data.Estimate;
import com.sen.mycontractor.data.Message;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.data.Technician;
import com.sen.mycontractor.data.User;


import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Sen on 2017/8/28.
 */

public class FirebaseHelper {
    private DatabaseReference mDatabaseReference;
    private Boolean savedProject, savedEstimate;
    private ArrayList<Project> projects = new ArrayList<>();
    private ArrayList<Estimate> estimates = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Dialog> dialogs = new ArrayList<>();
    private ArrayList<Message> messages = new ArrayList<>();
    private Message message, lastMessage;
    private Technician technician;
    private Dialog dialog;
    private String dialogId;
    private FirebaseUser mUser;
    private String techLocation = "";
    private boolean isExist;
    private String mCustomerId, mTechnicianId;
    private User user;
    private Customer customer;

    public FirebaseHelper(DatabaseReference db) {
        this.mDatabaseReference = db;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    public void saveEstimate(Estimate estimate, int projectID) {
        savedEstimate = false;
        try {
            mDatabaseReference.child("Estimates").child(Integer.toString(projectID)).child(mUser.getUid()).setValue(estimate);
            savedEstimate = true;
        } catch (DatabaseException e) {
            e.printStackTrace();
            savedEstimate = false;
        }
    }

    public boolean isSavedEstimate(int projectID) {
        savedEstimate = false;
        mDatabaseReference.child("Estimates").child(Integer.toString(projectID)).child(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            savedEstimate = true;
                        } else {
                            savedEstimate = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return savedEstimate;
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        projects.clear();
        if (dataSnapshot.exists()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Project project = ds.getValue(Project.class);
                projects.add(project);
            }
        }

    }

    private void fetchEstimates(DataSnapshot dataSnapshot) {
        estimates.clear();
        if (dataSnapshot.exists()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Estimate estimate = ds.getValue(Estimate.class);
                estimates.add(estimate);
            }
        }
    }




    public ArrayList<Project> retrieveAllForSameLocation() {
        mDatabaseReference.child("Users").child("Technicians")
                .child(mUser.getUid()).child("cityName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                techLocation = dataSnapshot.getValue(String.class);
                mDatabaseReference.child("Projects").orderByChild("location").equalTo(techLocation)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                fetchData(snapshot);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return projects;
    }

    public ArrayList<Estimate> retrieveEstimates(int projectID) {
        mDatabaseReference.child("Estimates").child(Integer.toString(projectID))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        fetchEstimates(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return estimates;
    }


    public ArrayList<Dialog> getDialogsList() {
        dialogs.clear();
        mDatabaseReference.child(mUser.getUid()).orderByChild("duration").equalTo(0)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Dialog dialog = ds.getValue(Dialog.class);
                                dialogs.add(dialog);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return dialogs;
    }


    public void addWelcomeMessage() {
        User user = new User("x5kbkquNujU1QFx7Sx6yCQyUphz2", "Administrator",
                "https://firebasestorage.googleapis.com/v0/b/mycontractor-89912.appspot.com/o/Images%2Ftechnician_head.png?alt=media&token=08352a41-32e8-42be-8737-2da719958697", true);
        Message welcomeMessage = new Message("88888888", "88888888", user, "Hello,how are you!");
        mDatabaseReference.child("Messages").child("88888888").setValue(welcomeMessage);
    }

    public void addWelcomeMessageInDialogs() {
        mDatabaseReference.child("Messages").child("88888888")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            message = dataSnapshot.getValue(Message.class);

                            if (message.getImageUrl() == null) {
                                message.setImageUrl("");
                            }
                            if (message.getVoiceUrl() == null) {
                                message.setVoiceUrl("");
                            }
                            lastMessage = new Message(message.getDialogId(), message.getId(), message.getText(), message.getDate(),
                                    message.getUserId(), message.getUserName(), message.getUserPhotoUrl(), message.isOnline(), ""
                                    , "", message.getDuration());
                            lastMessage.setDate(message.getDate());

                        } else {
                            Log.i("Not successful", message.toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        if (lastMessage == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createDialogsBranch(lastMessage);
                }
            }, 500);
        } else {
            createDialogsBranch(lastMessage);
        }
    }


    public void createDialogsBranch(final Message lastMessage) {
        mDatabaseReference.child("Users").child("Technicians").orderByChild("status").equalTo("technician")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Technician technician = ds.getValue(Technician.class);
                                User user = new User(
                                        mUser.getUid(),
                                        mUser.getDisplayName(),
                                        mUser.getPhotoUrl().toString(),
                                        false);
                                users.add(user);
                                dialogId = Long.toString(UUID.randomUUID().getLeastSignificantBits());
                                Dialog dialog = new Dialog(dialogId, technician.getFullName(),
                                        technician.getPersonalPhotoUrl(), users, lastMessage, 1);
                                mDatabaseReference.child("Dialogs").child(technician.getUid()).child(dialog.getId()).setValue(dialog);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void setDialog(Dialog dialog, String DialogId) {
        mDatabaseReference.child("Dialogs").child(DialogId).setValue(dialog)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    public Dialog getDialog(String dialogId) {
        mDatabaseReference.child("Dialogs").child(dialogId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dialog = dataSnapshot.getValue(Dialog.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        return dialog;
    }

    public ArrayList<Message> getMessages(String dialogId) {
        messages.clear();
        mDatabaseReference.child("Messages").orderByChild("dialogId").equalTo(dialogId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message = ds.getValue(Message.class);
                                messages.add(message);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return messages;
    }


    public ArrayList<User> getUsers(ArrayList<String> usersUIds) {
        users.clear();
        for (int i = 0; i < usersUIds.size(); i++) {
            mDatabaseReference.child("Users").child("Technicians").child(usersUIds.get(i))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Technician technician = dataSnapshot.getValue(Technician.class);
                            User user = new User(
                                    technician.getUid(),
                                    technician.getFullName(),
                                    technician.getPersonalPhotoUrl(),
                                    false);
                            users.add(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        return users;
    }

    //get from technician
    private User getUser(String userUid) {
        mDatabaseReference.child("Users").child("Technicians").child(userUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        technician = dataSnapshot.getValue(Technician.class);
                        if (technician == null) {
                        } else {
                            user = new User(technician.getUid(), technician.getFullName(), technician.getPersonalPhotoUrl(), false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return user;

    }



}




