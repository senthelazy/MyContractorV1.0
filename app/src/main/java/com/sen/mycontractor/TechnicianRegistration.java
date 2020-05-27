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
import com.sen.mycontractor.data.Technician;


import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TechnicianRegistration extends Activity {
    private static final String mStatus = "technician";
    private Button getVerificationBtn, verifyCodeBtn, uploadBusinessLicense,upLoadInsurance,createAccountBtn;
    private EditText phoneInput, codeInput, emailInput, passwordInput, confirmPasswordInput, fullNameInput,companyNameInput,cityNameInput;
    private String mVerificationId, mPhone, mEmail, mPassword, mConfirmPassword, mFullName, mBusinessLicenseUrl, mInsuranceUrl, mCompanyName, mCityName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private PhoneAuthCredential mPhoneAuthCredential;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private UserProfileChangeRequest mProfileChangeRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_registration);
        phoneInput = (EditText) findViewById(R.id.phoneInput2);
        getVerificationBtn = (Button) findViewById(R.id.getVerificationBtn2);
        codeInput = (EditText) findViewById(R.id.codeEt2);
        verifyCodeBtn = (Button) findViewById(R.id.verifyCodeBtn2);
        emailInput = (EditText) findViewById(R.id.emailAutoTv2);
        passwordInput = (EditText) findViewById(R.id.passwordEt2);
        confirmPasswordInput = (EditText) findViewById(R.id.confirmPasswordEt2);
        fullNameInput = (EditText) findViewById(R.id.fullNameEt2);
        companyNameInput=(EditText) findViewById(R.id.companyNameEt2);
        cityNameInput=(EditText) findViewById(R.id.cityNameEt2);
        uploadBusinessLicense=(Button)findViewById(R.id.businessLicenseBtn2);
        upLoadInsurance=(Button)findViewById(R.id.insuranceBtn2);
        createAccountBtn = (Button) findViewById(R.id.createAccountBtn2);


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
                    Toast.makeText(TechnicianRegistration.this, "Please enter a valid canadian cell phone number.", Toast.LENGTH_SHORT).show();
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
                companyNameInput.setVisibility(View.VISIBLE);
                cityNameInput.setVisibility(View.VISIBLE);
                uploadBusinessLicense.setVisibility(View.VISIBLE);
                upLoadInsurance.setVisibility(View.VISIBLE);
                createAccountBtn.setVisibility(View.VISIBLE);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser != null) {
                    Toast.makeText(TechnicianRegistration.this, "Code verified, please finish the rest of the fields. ", Toast.LENGTH_LONG).show();
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
                mCompanyName=companyNameInput.getText().toString().trim();
                mCityName=cityNameInput.getText().toString().trim();

                if ((mFullName.length() != 0 || mEmail.length() != 0 || mPhone.length() != 0) && mPassword.length() != 0) {
                    if (mPassword.equals(mConfirmPassword)) {
                        /*
                        mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(CustomerRegistration.class.getSimpleName(), "createUserWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Toast.makeText(CustomerRegistration.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/
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
                            Toast.makeText(TechnicianRegistration.this, "profile:" + mEmail + "  " +
                                    mPassword + "  " + mFullName + " " + mPhone, Toast.LENGTH_LONG).show();
                            //Toast.makeText(CustomerRegistration.this, "Create account failed", Toast.LENGTH_LONG).show();
                        }
                        Technician technician = new Technician(mFullName, mCompanyName, mCityName, mEmail,
                                mPhone, mUser.getUid(), mPassword, "", mStatus, mBusinessLicenseUrl, mInsuranceUrl);
                        databaseReference.child("Users").child("Technicians").child(mUser.getUid()).setValue(technician);
                        Intent intent = new Intent(TechnicianRegistration.this, TechnicianAccount.class);
                        intent.putExtra("Technician", technician);
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

    public void requestCode() {
        String phoneNumber = phoneInput.getText().toString();
        if (TextUtils.isEmpty(phoneNumber))
            return;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 15, TimeUnit.SECONDS, TechnicianRegistration.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        phoneCredential(phoneAuthCredential);
                        mPhoneAuthCredential = phoneAuthCredential;
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(TechnicianRegistration.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    phoneInput.setVisibility(View.GONE);
                    getVerificationBtn.setVisibility(View.GONE);
                    codeInput.setVisibility(View.GONE);
                    verifyCodeBtn.setVisibility(View.GONE);
                    Toast.makeText(TechnicianRegistration.this, "Code Validated ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TechnicianRegistration.this, "Invalid code,if you did not receive the code,please resend after 60sec.", Toast.LENGTH_LONG).show();
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
