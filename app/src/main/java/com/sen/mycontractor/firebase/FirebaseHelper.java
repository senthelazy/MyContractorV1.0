package com.sen.mycontractor.firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sen.mycontractor.CustomerRegistration;
import com.sen.mycontractor.data.Project;

import java.util.ArrayList;

/**
 * Created by Sen on 2017/8/28.
 */

public class FirebaseHelper {
    private DatabaseReference mDatabaseReference;
    private Boolean saved;
    private ArrayList<Project> projects = new ArrayList<>();
    private final FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();

    public FirebaseHelper(DatabaseReference db) {
        this.mDatabaseReference = db;
    }

    public Boolean save(Project project) {
        if (project == null) {
            saved = false;
        } else {
            try {
                mDatabaseReference.child("Projects").push().setValue(project);
                saved = true;
            } catch (DatabaseException e) {
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
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

    public ArrayList<Project> retrieve() {
        mDatabaseReference.child("Projects").child(mUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        fetchData(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

        return projects;
    }
}




