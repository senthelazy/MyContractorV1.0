package com.sen.mycontractor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sen.mycontractor.custom.ListAdapter;
import com.sen.mycontractor.firebase.FirebaseHelper;

public class MyRequests extends Activity {
    private DatabaseReference mDatabaseReference;

    private FirebaseHelper mHelper;

    private ListAdapter mListAdapter=null;

    private ListView myRequestsLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        myRequestsLv = (ListView) findViewById(R.id.myRequestsLv);
        mHelper = new FirebaseHelper(mDatabaseReference);
        mListAdapter = new ListAdapter(MyRequests.this, mHelper.retrieve());
        myRequestsLv.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();


        //Toast.makeText(MyRequests.this,""+mDatabaseReference.child("Projects").equalTo(mUser.getUid()),Toast.LENGTH_LONG).show();*/
    }


}
