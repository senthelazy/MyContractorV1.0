package com.sen.mycontractor.customer;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.sen.mycontractor.common.ToastHelper;
import com.sen.mycontractor.data.Customer;
import com.sen.mycontractor.data.Project;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerRegistration extends AppCompatActivity {

    private String mVerificationId, mPhone, mEmail, mPassword, mConfirmPassword, mFullName, mPhotoString;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private PhoneAuthCredential mPhoneAuthCredential;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    private Project mProject;
    private boolean phoneIsExist;
    private Customer customer;
    @BindView(R.id.phoneInput)
    EditText phoneInput;
    @BindView(R.id.getVerificationBtn)
    Button getVerificationBtn;
    @BindView(R.id.codeInput)
    EditText codeInput;
    @BindView(R.id.verifyCodeBtn)
    Button verifyCodeBtn;
    @BindView(R.id.emailInput)
    EditText emailInput;
    @BindView(R.id.passwordInput)
    EditText passwordInput;
    @BindView(R.id.confirmPasswordInput)
    EditText confirmPasswordInput;
    @BindView(R.id.fullNameInput)
    EditText fullNameInput;
    @BindView(R.id.createAccountBtn)
    Button createAccountBtn;
    @BindView(R.id.progress_bar_create_account)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);
        ButterKnife.bind(this);
        setupWindowAnimations();
        mProject = getIntent().getParcelableExtra("Project");
        phoneIsExist = false;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPhone = phoneInput.getText().toString().trim();
                if (mPhone.length() == 10) {
                    databaseRef.child("Users").child("Customers").orderByChild("phone").equalTo(mPhone)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        phoneIsExist = true;
                                        new AlertDialog.Builder(CustomerRegistration.this)
                                                .setTitle("Reminder")
                                                .setMessage("This phone number has been registered as Customer,go to login page?")
                                                .setNegativeButton("Use other number", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        phoneInput.setError("Use other number");
                                                    }
                                                }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CustomerRegistration.this);
                                                Intent intent = new Intent(CustomerRegistration.this, CustomerLogin.class);
                                                mProject = getIntent().getParcelableExtra("Project");
                                                intent.putExtra("Project", mProject);
                                                startActivity(intent, options.toBundle());
                                                finish();
                                            }
                                        }).show();
                                    } else {
                                        databaseRef.child("Users").child("Technicians").orderByChild("phone").equalTo(mPhone)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            phoneIsExist = true;
                                                            new AlertDialog.Builder(CustomerRegistration.this)
                                                                    .setTitle("Reminder")
                                                                    .setMessage("This phone number has been registered " +
                                                                            "as Technician,need to use another phone number?")
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
                createAccountBtn.setVisibility(View.VISIBLE);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser != null) {
                    if (mUser.getEmail() != null && mUser.getPhotoUrl() != null) {
                        createNewRequest();
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CustomerRegistration.this);
                        Intent intent = new Intent(CustomerRegistration.this, CustomerAccount.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent, options.toBundle());

                    }
                }
            }
        };

    }

    @OnClick(R.id.createAccountBtn)
    public void createAccountBtn() {
        animateButtonWidth();
        showProgressDialog();
        mPhone = phoneInput.getText().toString().trim();
        mEmail = emailInput.getText().toString().trim();
        mPassword = passwordInput.getText().toString().trim();
        mConfirmPassword = confirmPasswordInput.getText().toString().trim();
        mFullName = fullNameInput.getText().toString().trim();
        mPhotoString = "https://firebasestorage.googleapis.com/v0/b/mycontractor-89912.appspot.com/o/Administrator%2Fdefault_head.png?alt=media&token=ca1764de-0929-401f-bbca-d99b50e5a576";

        if ((mFullName.length() != 0 || mEmail.length() != 0 || mPhone.length() != 0) && mPassword.length() != 0) {
            if (mPassword.equals(mConfirmPassword)) {
                mAuth.signInWithEmailAndPassword(mEmail, mPassword);
                mUser = mAuth.getCurrentUser();
                UserProfileChangeRequest mProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mFullName)
                        .setPhotoUri(Uri.parse(mPhotoString))
                        .build();
                mUser.updateProfile(mProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            try {
                                mUser.updateEmail(mEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(CustomerRegistration.class.getSimpleName(), "Email updated.");
                                    }
                                });
                                mUser.updatePassword(mPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(CustomerRegistration.class.getSimpleName(), "Password updated.");
                                    }
                                });
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                Toast.makeText(CustomerRegistration.this, "profile:" + mEmail + "  " +
                                        mPassword + "  " + mFullName + " " + mPhone, Toast.LENGTH_LONG).show();
                            }
                            customer = new Customer(mFullName, mEmail, mPhone, mUser.getUid(), mPassword,
                                    mPhotoString, "customer", mProject.getLocation());
                            databaseRef.child("Users").child("Customers").child(mUser.getUid()).setValue(customer);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CustomerRegistration.this);
                            Intent intent = new Intent(CustomerRegistration.this, CustomerAccount.class);
                            intent.putExtra("Project", mProject);
                            intent.putExtra("Customer", customer);
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

    @OnClick(R.id.verifyCodeBtn)
    public void verifyCodeBtn() {
        String code = codeInput.getText().toString();
        if (TextUtils.isEmpty(code))
            return;
        if (mVerificationId != null || !mVerificationId.equals("")) {
            phoneCredential(PhoneAuthProvider.getCredential(mVerificationId, code));
            emailInput.requestFocus();
        } else {
            codeInput.setError("Please enter a valid code");
        }
    }


    @OnClick(R.id.getVerificationBtn)
    public void getVerificationBtn() {
        if (mPhone.length() == 10 && (!phoneIsExist)) {
            requestCode();
            codeInput.setVisibility(View.VISIBLE);
            getVerificationBtn.setText("Wait 15 sec to resend..");
            getVerificationBtn.setEnabled(false);
            codeInput.requestFocus();
        } else {
            Toast.makeText(CustomerRegistration.this, "Please enter a valid Canadian cell phone number.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        ToastHelper.showToast(this, "You need to finish this form to finish the request.", false);
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

    private void showProgressDialog() {
        progressBar.setAlpha(1f);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    private int getButtonSize() {
        return (int) getResources().getDimension(R.dimen.button_size);
    }

    private void setupWindowAnimations() {
        Explode enterTransition = new Explode();
        enterTransition.setDuration(500);
        getWindow().setEnterTransition(enterTransition);
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(1000);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
        getWindow().setAllowReturnTransitionOverlap(false);
        getWindow().setAllowEnterTransitionOverlap(true);
    }

    private void createNewRequest() {
        Random randomID = new Random();
        mProject.setID(randomID.nextInt());
        mProject.setCustomerUid(mUser.getUid());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm a", Locale.getDefault());
        String createdAt = sdf.format(new Date());
        mProject.setCreatedAt(createdAt);
        databaseRef.child("Projects").child(Integer.toString(mProject.getID())).setValue(mProject);
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

                    }
                }

        );
    }

    private void phoneCredential(PhoneAuthCredential phoneAuthCredential) {

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

                }
            }
        });
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

