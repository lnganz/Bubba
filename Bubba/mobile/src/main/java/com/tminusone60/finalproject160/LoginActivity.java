package com.tminusone60.finalproject160;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText mPhoneNumberView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private EditText mNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
        }


        // Set up the login form.
        mPhoneNumberView = (EditText) findViewById(R.id.login_phone_number);
        mPhoneNumberView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mNameView = (EditText) findViewById(R.id.login_user_name);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordConfirmView = (EditText) findViewById(R.id.login_password_confirm);

        Button createButton = (Button) findViewById(R.id.login_create_button);
        createButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mNameView.setError(null);
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);
        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String phone = mPhoneNumberView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check whether passwords match
        if (!TextUtils.isEmpty(passwordConfirm) && !passwordConfirm.equals(password)) {
            mPasswordConfirmView.setError("Passwords do not match");
            focusView = mPasswordConfirmView;
            cancel = true;
        }

        // Check for a valid phone number.
        if (TextUtils.isEmpty(phone)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }


        if (cancel && (name == null || !name.equalsIgnoreCase("a"))) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            performLogin(name, phone, password);
        }
    }

    private void performLogin(String name, String phone, String password) {
        SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(Globals.USER_NAME, name);
        editor.putString(Globals.PHONE_NUMBER, phone);
        editor.putString(Globals.PASSWORD, password);
        editor.putBoolean(Globals.FIRST_LAUNCH, false);
        editor.commit();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    @Override
    public void onBackPressed() {

    }
}

