package com.sen.mycontractor.technician;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sen.mycontractor.R;
import com.sen.mycontractor.common.FirebaseHelper;
import com.sen.mycontractor.customer.CustomerAccount;
import com.sen.mycontractor.customer.CustomerLogin;
import com.sen.mycontractor.customer.CustomerRegistration;
import com.sen.mycontractor.customer.UploadImages;
import com.sen.mycontractor.data.Customer;
import com.sen.mycontractor.data.Technician;


import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TechnicianRegistration extends AppCompatActivity {
    private static final String mStatus = "technician";
    private String mVerificationId, mPhone, mEmail, mPassword, mConfirmPassword, mFullName, mBusinessLicenseUrl, mInsuranceUrl, mCompanyName, mCityName, mPhotoString;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private PhoneAuthCredential mPhoneAuthCredential;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    private boolean phoneIsExist;
    private Technician technician;
    private Customer customer;
    @BindView(R.id.phoneInput2)
    EditText phoneInput;
    @BindView(R.id.getVerificationBtn2)
    Button getVerificationBtn;
    @BindView(R.id.codeEt2)
    EditText codeInput;
    @BindView(R.id.verifyCodeBtn2)
    Button verifyCodeBtn;
    @BindView(R.id.emailAutoTv2)
    EditText emailInput;
    @BindView(R.id.passwordEt2)
    EditText passwordInput;
    @BindView(R.id.confirmPasswordEt2)
    EditText confirmPasswordInput;
    @BindView(R.id.fullNameEt2)
    EditText fullNameInput;
    @BindView(R.id.companyNameEt2)
    EditText companyNameInput;
    @BindView(R.id.cityNameEt2)
    EditText cityNameInput;
    @BindView(R.id.businessLicenseBtn2)
    Button uploadBusinessLicense;
    @BindView(R.id.insuranceBtn2)
    Button upLoadInsurance;
    @BindView(R.id.createAccountBtn2)
    Button createAccountBtn;
    @BindView(R.id.progress_bar_create_account)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_registration);
        ButterKnife.bind(this);
        setupWindowAnimations();
        mAuth = FirebaseAuth.getInstance();
        phoneIsExist = false;
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPhone = phoneInput.getText().toString().trim();
                if (mPhone.length() == 10) {
                    databaseRef.child("Users").child("Technicians").orderByChild("phone").equalTo(mPhone)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        phoneIsExist = true;
                                        new AlertDialog.Builder(TechnicianRegistration.this)
                                                .setTitle("Reminder")
                                                .setMessage("This phone number has been registered," +
                                                        "go to login page?")
                                                .setNegativeButton("Use other number", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        phoneInput.setError("Use other number");
                                                    }
                                                })
                                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TechnicianRegistration.this);
                                                        Intent intent = new Intent(TechnicianRegistration.this, TechnicianLogin.class);
                                                        startActivity(intent, options.toBundle());
                                                        finish();
                                                    }
                                                }).show();
                                    } else {
                                        databaseRef.child("Users").child("Customers").orderByChild("phone").equalTo(mPhone)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            phoneIsExist = true;
                                                            new AlertDialog.Builder(TechnicianRegistration.this)
                                                                    .setTitle("Reminder")
                                                                    .setMessage("This phone number has been registered " +
                                                                            "as Customer,need to use another phone number?")
                                                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            phoneInput.setError("Use another number");
                                                                        }
                                                                    }).show();
                                                        } else {
                                                            phoneIsExist = false;
                                                            getVerificationBtn.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
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
        emailInput.setOnKeyListener(new View.OnKeyListener()

        {
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
                if (passwordInput.length() > 10) {
                    confirmPasswordInput.setVisibility(View.VISIBLE);
                } else {
                    passwordInput.setError("Password is too short!");
                }

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
        mAuthListener = new FirebaseAuth.AuthStateListener()

        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser != null) {
                    if (mUser.getEmail() != null && mUser.getPhotoUrl() != null) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TechnicianRegistration.this);
                        Intent intent = new Intent(TechnicianRegistration.this, TechnicianAccount.class);
                        startActivity(intent, options.toBundle());
                        finish();
                    }
                }
            }
        };

    }

    @OnClick(R.id.getVerificationBtn2)
    public void getVerificationBtn(){
        if (phoneInput.getText().toString().trim().length() == 10) {
            requestCode();
            codeInput.setVisibility(View.VISIBLE);
            codeInput.requestFocus();
            getVerificationBtn.setText("Wait 15 sec to resend..");
            getVerificationBtn.setEnabled(false);
        } else {
            Toast.makeText(TechnicianRegistration.this, "Please enter a valid canadian cell phone number.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.verifyCodeBtn2)
    public void verifyCodeBtn(){
        verifyCode();
    }

    @OnClick(R.id.createAccountBtn2)
    public void createAccountBtn(){
        animateButtonWidth();
        showProgressDialog();
        mPhone = phoneInput.getText().toString().trim();
        mEmail = emailInput.getText().toString().trim();
        mPassword = passwordInput.getText().toString().trim();
        mConfirmPassword = confirmPasswordInput.getText().toString().trim();
        mFullName = fullNameInput.getText().toString().trim();
        mCompanyName = companyNameInput.getText().toString().trim();
        mCityName = cityNameInput.getText().toString().trim();
        mPhotoString = "https://firebasestorage.googleapis.com/v0/b/mycontractor-89912.appspot.com/o/Administrator%2Ftechnician_head.png?alt=media&token=0377d87c-568d-4cf6-bfdc-27641b93c2db";
        if ((mFullName.length() != 0 || mEmail.length() != 0 || mPhone.length() != 0) && mPassword.length() != 0) {
            if (mPassword.equals(mConfirmPassword)) {
                mAuth.signInWithEmailAndPassword(mEmail, mPassword);
                mUser = mAuth.getCurrentUser();
                UserProfileChangeRequest mProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mFullName)
                        .setPhotoUri(Uri.parse(mPhotoString))
                        .build();
                mUser.updateProfile(mProfileChangeRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(CustomerRegistration.class.getSimpleName(), "User profile updated.");
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
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        Toast.makeText(TechnicianRegistration.this, "profile:" + mEmail + "  " +
                                                mPassword + "  " + mFullName + " " + mPhone, Toast.LENGTH_LONG).show();
                                    }
                                    if (mBusinessLicenseUrl == null) {
                                        mBusinessLicenseUrl = "";
                                    }
                                    if (mInsuranceUrl == null) {
                                        mInsuranceUrl = "";
                                    }
                                    Technician technician = new Technician(mFullName, mCompanyName, mCityName, mEmail,
                                            mPhone, mUser.getUid(), mPassword, mPhotoString, mStatus, mBusinessLicenseUrl, mInsuranceUrl);
                                    databaseRef.child("Users").child("Technicians").child(mUser.getUid()).setValue(technician);
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TechnicianRegistration.this);
                                    Intent intent = new Intent(TechnicianRegistration.this, TechnicianAccount.class);
                                    intent.putExtra("Technician", technician);
                                    startActivity(intent, options.toBundle());
                                }
                            }
                        });


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

    private void showProgressDialog() {
        progressBar.setAlpha(1f);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    private int getButtonSize() {
        return (int) getResources().getDimension(R.dimen.button_size);
    }

    private void animateButtonWidth() {
        createAccountBtn.setText("");
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(createAccountBtn.getMeasuredWidth(), getButtonSize());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = createAccountBtn.getLayoutParams();
                layoutParams.width = val;
                createAccountBtn.requestLayout();
            }
        });
        mValueAnimator.setDuration(250);
        mValueAnimator.start();
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
        getWindow().setAllowEnterTransitionOverlap(true);
    }

}
