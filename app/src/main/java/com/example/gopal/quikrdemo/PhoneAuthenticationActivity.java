package com.example.gopal.quikrdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthentication";
    private EditText mUserNameEditText, mPhoneEditText, mOtpEditText;
    private Button mSignInButton;
    private String mPhoneNumber, mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_name), MODE_PRIVATE);
        String userName = sharedPref.getString(getString(R.string.key_user_name), null);
        String phoneNumber = sharedPref.getString(getString(R.string.key_user_phone_number), null);
        if (!TextUtils.isEmpty(userName)) {
            startActivity(new Intent(PhoneAuthenticationActivity.this, MainActivity.class));
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        mUserNameEditText = findViewById(R.id.user_name_edit_text);
        mPhoneEditText = findViewById(R.id.user_phone_number_edit_text);
        mOtpEditText = findViewById(R.id.otp_edit_text);
        mSignInButton = findViewById(R.id.sign_in_button);


        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSignInButton.getText().equals(getString(R.string.sign_in))) {
                    String userName = mUserNameEditText.getText().toString();
                    mPhoneNumber = mPhoneEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(userName)) {
                        mUserNameEditText.setError("Enter your name!");
                        return;
                    }
                    if (TextUtils.isEmpty(mPhoneNumber)) {
                        mPhoneEditText.setError("Enter your mobile number!");
                        return;
                    }
                    mSignInButton.setEnabled(false);
                    requestForOtp();
                    showProgressDialog("Wait for otp...");
                } else {
                    String otp = mOtpEditText.getText().toString();
                    if (TextUtils.isEmpty(otp)) {
                        mUserNameEditText.setError("Enter the otp!");
                        return;
                    }
                    verifyPhoneNumber(otp);
                }

            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                mSignInButton.setEnabled(true);


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(PhoneAuthenticationActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(PhoneAuthenticationActivity.this, "Too Many Request", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(PhoneAuthenticationActivity.this, "Report to developer", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(PhoneAuthenticationActivity.this, "Enter the otp", Toast.LENGTH_SHORT).show();
                mOtpEditText.setVisibility(View.VISIBLE);
                mSignInButton.setText("Verify");
                mSignInButton.setEnabled(true);


            }
        };


    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private void requestForOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }

    private void verifyPhoneNumber(String otp) {
        showProgressDialog("Verifying...");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();

                        if (task.isSuccessful()) {
                            String userName = mUserNameEditText.getText().toString();
                            String userPhoneNumber = mPhoneNumber;
                            Toast.makeText(PhoneAuthenticationActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                            saveUserData(userName, userPhoneNumber);
                            startActivity(new Intent(PhoneAuthenticationActivity.this, MainActivity.class));
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(PhoneAuthenticationActivity.this, "Couldn't login", Toast.LENGTH_SHORT).show();

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneAuthenticationActivity.this, "Enter Correct Otp", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void saveUserData(String userName, String userPhoneNumber) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.key_user_name), userName);
        editor.putString(getString(R.string.key_user_phone_number), userPhoneNumber);
        editor.putInt(getString(R.string.key_count_of_listed_product_by_user), 0);
        editor.apply();
    }


}
