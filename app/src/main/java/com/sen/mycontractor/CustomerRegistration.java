package com.sen.mycontractor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sen.mycontractor.data.Customer;
import com.sen.mycontractor.data.Project;


import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CustomerRegistration extends Activity {
    private static final String mStatus="customer";
    private Button getVerificationBtn, verifyCodeBtn, createAccountBtn;
    private EditText phoneInput, codeInput, emailInput, passwordInput, confirmPasswordInput, fullNameInput;
    private String mVerificationId, mPhone, mEmail, mPassword, mConfirmPassword, mFullName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private PhoneAuthCredential mPhoneAuthCredential;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private UserProfileChangeRequest mProfileChangeRequest;
    private Project mProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);
        phoneInput = (EditText) findViewById(R.id.phoneInput);
        getVerificationBtn = (Button) findViewById(R.id.getVerificationBtn);
        codeInput = (EditText) findViewById(R.id.codeInput);
        verifyCodeBtn = (Button) findViewById(R.id.verifyCodeBtn);
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        confirmPasswordInput = (EditText) findViewById(R.id.confirmPasswordInput);
        fullNameInput = (EditText) findViewById(R.id.fullNameInput);
        createAccountBtn = (Button) findViewById(R.id.createAccountBtn);
        mProject = getIntent().getParcelableExtra("Project");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // Toast.makeText(CustomerRegistration.this, "1 "+ mProject.getLocation()+"2 "+mProject.getImagesUrl()+"3 "+mProject.getVideoUrl()+
        //        "4 "+mProject.getCategory()+"5 "+mProject.getSubCategory()+"6 "+mProject.getJobDescription(), Toast.LENGTH_LONG).show();

        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (phoneInput.getText().toString().trim().length() == 10) {
                    getVerificationBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        getVerificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneInput.getText().toString().trim().length() == 10) {
                    requestCode();
                    codeInput.setVisibility(View.VISIBLE);
                    getVerificationBtn.setText("Wait 15 sec to resend..");
                    getVerificationBtn.setEnabled(false);
                } else {
                    Toast.makeText(CustomerRegistration.this, "Please enter a valid Canadian cell phone number.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                verifyCodeBtn.setVisibility(View.VISIBLE);
            }
        });

        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordInput.setVisibility(View.VISIBLE);
            }
        });

        emailInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    passwordInput.requestFocus();
                }
                return false;
            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                confirmPasswordInput.setVisibility(View.VISIBLE);
            }
        });

        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                fullNameInput.setVisibility(View.VISIBLE);
            }
        });

        fullNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                createAccountBtn.setVisibility(View.VISIBLE);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent=new Intent(CustomerRegistration.this,CustomerAccount.class);
                    createNewRequest();
                    startActivity(intent);
                    finish();
                    //Toast.makeText(CustomerRegistration.this, "Code verified, please finish the rest of the fields. ", Toast.LENGTH_LONG).show();
                   /* emailInput.setVisibility(View.VISIBLE);
                    phoneInput.setEnabled(false);
                    getVerificationBtn.setEnabled(false);
                    codeInput.setEnabled(false);
                    verifyCodeBtn.setEnabled(false);*/
                }
            }
        };

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhone = phoneInput.getText().toString().trim();
                mEmail = emailInput.getText().toString().trim();
                mPassword = passwordInput.getText().toString().trim();
                mConfirmPassword = confirmPasswordInput.getText().toString().trim();
                mFullName = fullNameInput.getText().toString().trim();
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                if ((mFullName.length() != 0 || mEmail.length() != 0 || mPhone.length() != 0) && mPassword.length() != 0) {
                    if (mPassword.equals(mConfirmPassword)) {
                        mProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(mFullName)
                                .build();
                        try {
                            mUser.updateEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(CustomerRegistration.class.getSimpleName(), "Email updated.");
                                }
                            });
                            mUser.updatePassword(mPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(CustomerRegistration.class.getSimpleName(), "Password updated.");
                                }
                            });
                            ;
                            mUser.updateProfile(mProfileChangeRequest)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(CustomerRegistration.class.getSimpleName(), "User profile updated.");
                                            }
                                        }
                                    });

                        } catch (Exception e) {
                            Toast.makeText(CustomerRegistration.this, "profile:" + mEmail + "  " +
                                    mPassword + "  " + mFullName + " " + mPhone, Toast.LENGTH_LONG).show();
                            //Toast.makeText(CustomerRegistration.this, "Create account failed", Toast.LENGTH_LONG).show();
                        }
                        createNewRequest();
                        Customer customer=new Customer(mFullName,mEmail,mPhone,mUser.getUid(),mPassword,"","customer");
                        databaseReference.child("Users").child("Customers").child(mUser.getUid()).setValue(customer);
                        Intent intent = new Intent(CustomerRegistration.this, CustomerAccount.class);
                        intent.putExtra("Project", mProject);
                        intent.putExtra("Customer",customer);
                        startActivity(intent);
                        finish();
                    } else {
                        confirmPasswordInput.setError(new CharSequence() {
                            @Override
                            public int length() {
                                return 0;
                            }

                            @Override
                            public char charAt(int i) {
                                return 0;
                            }

                            @Override
                            public CharSequence subSequence(int i, int i1) {
                                return null;
                            }
                        });
                    }
                }

            }
        });

    }

    private void createNewRequest() {
        Random randomID=new Random();
        mProject.setID(randomID.nextInt());
        mProject.setCustomerUid(mUser.getUid());
        databaseReference.child("Projects").child(mUser.getUid()).child(Integer.toString(mProject.getID())).setValue(mProject);
    }

    public void requestCode() {
        String phoneNumber = phoneInput.getText().toString();
        if (TextUtils.isEmpty(phoneNumber))
            return;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 15, TimeUnit.SECONDS, CustomerRegistration.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        phoneCredential(phoneAuthCredential);
                        mPhoneAuthCredential = phoneAuthCredential;
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(CustomerRegistration.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        mVerificationId = verificationId;
                    }

                    public void onCodeAutoRetrievalTimeOut(String verificationId) {
                        super.onCodeAutoRetrievalTimeOut(verificationId);
                        getVerificationBtn.setText("Get verification code");
                        getVerificationBtn.setEnabled(true);
                        //Toast.makeText(CustomerRegistration.this, "onCodeAutoRetrievalTimeOut :" + verificationId, Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }

    private void phoneCredential(PhoneAuthCredential phoneAuthCredential) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    emailInput.setVisibility(View.VISIBLE);
                    phoneInput.setEnabled(false);
                    getVerificationBtn.setEnabled(false);
                    codeInput.setEnabled(false);
                    verifyCodeBtn.setEnabled(false);
                    Toast.makeText(CustomerRegistration.this, "Code Validated ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CustomerRegistration.this, "Invalid code,if you did not receive the code,please resend after 60sec.", Toast.LENGTH_LONG).show();
                    //Toast.makeText(CustomerRegistration.this, "Invalid code" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void verifyCode() {
        String code = codeInput.getText().toString();
        if (TextUtils.isEmpty(code))
            return;
        phoneCredential(PhoneAuthProvider.getCredential(mVerificationId, code));
    }

    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
