package com.tminusone60.finalproject160;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by lennon on 4/28/16.
 */
public class ChangeSettingsActivity extends Activity {

    private EditText passwordText;
    private EditText phoneNumberText;
    private EditText dayCountText;
    private ScrollView passwordLayout;
    private ScrollView settingsLayout;
    private ToggleButton notificationToggle;
    private ToggleButton smsToggle;
    private SeekBar daySeekBar;
    private TextView dayText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_settings);

        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        scaleLayout(0.9, 0.9);

        loadLayout();

        initializeSeekBar();

        initializeFields();

        initializeButtons();
    }

    private void loadLayout() {
        passwordText = (EditText) findViewById(R.id.change_settings_password_text);
        phoneNumberText = (EditText) findViewById(R.id.guardian_phone_edittext);
        phoneNumberText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        dayCountText = (EditText) findViewById(R.id.guardian_day_count);
        passwordLayout = (ScrollView) findViewById(R.id.change_settings_auth_form);
        settingsLayout = (ScrollView) findViewById(R.id.change_settings_form);
        notificationToggle = (ToggleButton) findViewById(R.id.settings_toggle_notification);
        smsToggle = (ToggleButton) findViewById(R.id.settings_toggle_sms);
        daySeekBar = (SeekBar) findViewById(R.id.settings_daySeekBar);
        dayText = (TextView) findViewById(R.id.settings_dayText);
    }

    private void initializeFields() {
        passwordText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
        String phoneNumber = settings.getString(Globals.PHONE_NUMBER, Globals.DEFAULT_PHONE_NUMBER);
        int dayCount = settings.getInt(Globals.ALERT_DAY_COUNT, Globals.DEFAULT_ALERT_DAY_COUNT);
        phoneNumberText.setText(phoneNumber);
        daySeekBar.setProgress(dayCount-1);
//        dayCountText.setText(dayCount);

    }

    private void initializeButtons() {
        Button closeButton = (Button) findViewById(R.id.settings_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button passwordButton = (Button) findViewById(R.id.password_check_button);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This goes against everything I learned in 161. SUPER INSECURE.
                String inputText = passwordText.getText().toString();
                String storedPassword = getSharedPreferences(Globals.PREFS_NAME, 0).getString(Globals.PASSWORD, Globals.DEFAULT_PASSWORD);
                if (inputText.equals(storedPassword)) {
                    switchLayouts();
                } else {
                    Toast.makeText(ChangeSettingsActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
        notificationToggle.setChecked(storage.getBoolean(Globals.NOTIFICATIONS_ENABLED, Globals.DEFAULT_NOTIFICATIONS_ENABLED));
        smsToggle.setChecked(storage.getBoolean(Globals.SMS_ENABLED, Globals.DEFAULT_SMS_ENABLED));
        int dayCount = storage.getInt(Globals.ALERT_DAY_COUNT, Globals.DEFAULT_ALERT_DAY_COUNT - 1);
        daySeekBar.setProgress(dayCount-1);
        dayText.setText(getDayText(dayCount));


        Button updateButton = (Button) findViewById(R.id.guardian_save_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hide keyboard
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                // Save settings
                String phoneNumber = phoneNumberText.getText().toString();
//                String dayCount = dayCountText.getText().toString();
                int dayCount = daySeekBar.getProgress() + 1;
                boolean notificationsOn = notificationToggle.isChecked();
                boolean smsOn = smsToggle.isChecked();
                SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Globals.PHONE_NUMBER, phoneNumber);
                editor.putInt(Globals.ALERT_DAY_COUNT, dayCount);
                editor.putBoolean(Globals.NOTIFICATIONS_ENABLED, notificationsOn);
                editor.putBoolean(Globals.SMS_ENABLED, smsOn);
                editor.commit();

                Toast.makeText(ChangeSettingsActivity.this, "Saved Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchLayouts() {
        passwordLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.VISIBLE);
    }

    private void scaleLayout(double xScaleFactor, double yScaleFactor) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * xScaleFactor), (int) (height * yScaleFactor));
    }

    private String getDayText(int day) {
        return "Alert after " + day + " day(s)";
    }

    private void initializeSeekBar() {
//        daySeekBar.setScaleX(2f);
//        daySeekBar.setScaleY(2f);
        daySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dayText.setText(getDayText(progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
////                int curValue = seekBar.getProgress();
////                Toast.makeText(FeelingActivity.this, "Seek Value: " + curValue, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(FeelingActivity.this, WatchToPhoneService.class);
//                long time = System.currentTimeMillis();
//                int feeling = seekBar.getProgress();
//                startService(intent);
//                finish();
            }
        });
    }
}
