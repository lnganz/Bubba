package com.tminusone60.finalproject160;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by lennon on 4/29/16.
 */
public class DevSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_settings);

        dimBackground();

        initializeButtons();

        scaleLayout(0.9, 0.7);
    }

    private void initializeButtons() {
        Button clearButton = (Button) findViewById(R.id.dev_master_reset);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPrefs(v, true);
            }
        });

        Button defaultUserButton = (Button) findViewById(R.id.dev_reset_example_user);
        defaultUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPrefs(v, false);
            }
        });

        Button closeButton = (Button) findViewById(R.id.dev_settings_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initializeToggles();
    }

    private void initializeToggles() {
        SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
        boolean notificationsOn = storage.getBoolean(Globals.NOTIFICATIONS_ENABLED, Globals.DEFAULT_NOTIFICATIONS_ENABLED);
        boolean smsOn = storage.getBoolean(Globals.SMS_ENABLED, Globals.DEFAULT_SMS_ENABLED);
        ToggleButton notiToggle = (ToggleButton) findViewById(R.id.dev_toggle_notification);
        notiToggle.setChecked(notificationsOn);
        notiToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
                SharedPreferences.Editor editor = storage.edit();
                editor.putBoolean(Globals.NOTIFICATIONS_ENABLED, isChecked);
                editor.commit();
            }
        });
        ToggleButton smsToggle = (ToggleButton) findViewById(R.id.dev_toggle_sms);
        smsToggle.setChecked(smsOn);
        smsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
                SharedPreferences.Editor editor = storage.edit();
                editor.putBoolean(Globals.SMS_ENABLED, isChecked);
                editor.commit();
            }
        });
    }

    private void resetExampleUser() {
        SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
        SharedPreferences.Editor editor = storage.edit();

        editor.putString(Globals.PHONE_NUMBER, Globals.EXAMPLE_PHONE_NUMBER);
        editor.putInt(Globals.ALERT_DAY_COUNT, Globals.EXAMPLE_ALERT_DAY_COUNT);
        editor.putString(Globals.PASSWORD, Globals.EXAMPLE_PASSWORD);
        editor.putString(Globals.FEELINGS_DATA, Globals.EXAMPLE_FEELINGS_DATA);
        editor.putString(Globals.RECENT_QUOTE_TEXT, Globals.EXAMPLE_RECENT_QUOTE_TEXT);
        editor.putString(Globals.RECENT_QUOTE_AUTHOR, Globals.EXAMPLE_RECENT_QUOTE_AUTHOR);
        editor.putString(Globals.USER_NAME, Globals.EXAMPLE_USER_NAME);
        editor.putInt(Globals.USER_LEVEL, Globals.EXAMPLE_USER_LEVEL);
        editor.putInt(Globals.USER_EXPERIENCE, Globals.EXAMPLE_USER_EXPERIENCE);
        editor.putInt(Globals.USER_FEED_COUNT, Globals.EXAMPLE_USER_FEED_COUNT);
        editor.putString(Globals.USER_ACHIEVEMENTS, Globals.EXAMPLE_USER_ACHIEVEMENTS);
        editor.putBoolean(Globals.SMS_ENABLED, Globals.EXAMPLE_SMS_ENABLED);
        editor.putBoolean(Globals.NOTIFICATIONS_ENABLED, Globals.EXAMPLE_NOTIFICATIONS_ENABLED);
        editor.putBoolean(Globals.FIRST_LAUNCH, Globals.EXAMPLE_FIRST_LAUNCH);
        editor.commit();
    }

    private void clearSharedPrefs(View v, final boolean toDefault) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (toDefault) {
                            SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
                            // DELETES ALL STORED STUFF
                            settings.edit().clear().commit();
                            initializeButtons();
                        } else {
                            resetExampleUser();
                            initializeButtons();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure you want to RESET the SharedStorage?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    private void dimBackground() {
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void scaleLayout(double xScaleFactor, double yScaleFactor) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * xScaleFactor), (int) (height * yScaleFactor));
    }
}
