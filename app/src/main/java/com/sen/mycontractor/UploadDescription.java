package com.sen.mycontractor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sen.mycontractor.data.Project;

public class UploadDescription extends Activity {
    private Spinner categorySpinner,subCategorySpinner;
    private String categorySelected,subCategorySelected;
    private ArrayAdapter<CharSequence> mAdapter;
    private ArrayAdapter<CharSequence> mAdapterSub;
    private Button useDescriptionBtn;
    private Project mProject;
    private EditText jobDescriptionEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_description);

        jobDescriptionEt=(EditText)findViewById(R.id.jobDescriptionEt) ;
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        subCategorySpinner = (Spinner) findViewById(R.id.subCategorySpinner);
        mAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(mAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> view, View view1, int i, long l) {
                categorySelected=categorySpinner.getSelectedItem().toString();
                mProject.setCategory(categorySelected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> view) {

            }
        });

        mAdapterSub = ArrayAdapter.createFromResource(this, R.array.sub_categories, android.R.layout.simple_spinner_item);
        mAdapterSub.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCategorySpinner.setAdapter(mAdapterSub);
        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> view, View view1, int i, long l) {
                subCategorySelected=subCategorySpinner.getSelectedItem().toString();
                mProject.setSubCategory(subCategorySelected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> view) {

            }
        });

        useDescriptionBtn=(Button)findViewById(R.id.useDescriptionBtn);
        mProject=getIntent().getParcelableExtra("Project");
        useDescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProject.setJobDescription(jobDescriptionEt.getText().toString().trim());
                Intent intent=new Intent(UploadDescription.this, CustomerRegistration.class);
                intent.putExtra("Project",mProject);
                startActivity(intent);
            }
        });
    }


}
