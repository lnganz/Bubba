package com.tminusone60.finalproject160;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "MainActivity";

    private BroadcastReceiver receiver;

    private static String quoteOfTheDay;
    private static String quoteAuthor;
    private static int userLevel;
    private static boolean firstLaunch;

    private TextView quoteTextView;
    private TextView authorTextView;
    private TextView levelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadStorage();

        checkFirstLaunch();

        initializeActionBar();

        initializeLevelButton();

        loadAchievements();

        loadQuoteOfTheDay();

        setupBroadcastReceiver();

//        startNotificationAlarm();
    }

    private void loadStorage() {
        SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
        firstLaunch = settings.getBoolean(Globals.FIRST_LAUNCH, Globals.DEFAULT_FIRST_LAUNCH);
        userLevel = settings.getInt(Globals.USER_LEVEL, Globals.DEFAULT_USER_LEVEL);
    }

    private void checkFirstLaunch() {
        if (firstLaunch) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // May or may not need this.
        }
    }

    private void loadAchievements() {
        HorizontalScrollView achievementScrollview = (HorizontalScrollView) findViewById(R.id.achievement_horizontal_scrollview);
        achievementScrollview.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        achievementScrollview.setHorizontalScrollBarEnabled(false);

        LinearLayout achievementBar = (LinearLayout) findViewById(R.id.achievement_layout);
        achievementBar.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        achievementBar.removeAllViews();

        TextView achievementText = (TextView) findViewById(R.id.achievement_text);


        String achievementString = getSharedPreferences(Globals.PREFS_NAME, 0).getString(Globals.USER_ACHIEVEMENTS, Globals.DEFAULT_USER_ACHIEVEMENTS);
        String [] achievementArray = achievementString.split(";");
        int numAchievements = achievementArray.length - 1;
        achievementText.setText("Achievements (" + numAchievements + ")");
        int numTotal = Math.max(Globals.NUM_ACHIEVEMENT_LOCKS, numAchievements) + 1;
        // Test adding buttons dynamically
        for (int i = 1; i < numTotal; i++) {
            String name;
            if (i <= numAchievements) {
                name = achievementArray[i];
            } else {
                name = Achievement.LOCK.name;
            }
            final String aName = name;
            ImageButton aButton = makeAchievementButton(achievementBar);
            int resourceId = Achievement.getImageResourceId(aName);
            aButton.setImageResource(resourceId);
            aButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AchievementActivity.class);
                    intent.putExtra("Achievement", aName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    private ImageButton makeAchievementButton(LinearLayout achievementBar) {
        ImageButton btn = new ImageButton(this);
        btn.setBackground(getDrawable(R.drawable.button_circle));

        int dpDim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpDim, dpDim);

        dpDim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        params.setMargins(20, 0, 20, 0);
        btn.setLayoutParams(params);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        dpDim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        btn.setPadding(dpDim, dpDim, dpDim, dpDim);
        achievementBar.addView(btn);
        return btn;
    }
    private void initializeLevelButton() {
        ImageButton levelButtton = (ImageButton) findViewById(R.id.level_button);
        levelButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStatsActivity();
            }
        });

        levelTextView = (TextView) findViewById(R.id.level_text);
        updateLevel();
        levelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStatsActivity();
            }
        });
    }

    private void initializeActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        // Remove shadow from underneath ActionBar
        mActionBar.setElevation(0);

        LayoutInflater mInflater = LayoutInflater.from(this);

        // Apply our own layout to the ActionBar
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

        final ImageButton helpButton = (ImageButton) mCustomView.findViewById(R.id.title_help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Launching help", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, OnboardActivity.class));
            }
        });
        final ImageButton bubbaButton = (ImageButton) mCustomView.findViewById(R.id.title_bubba_button);
        bubbaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent watchServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                watchServiceIntent.putExtra("Activity", "MainActivity");
//                Toast.makeText(getBaseContext(), "Starting Wear's MainActivity", Toast.LENGTH_SHORT).show();
                startService(watchServiceIntent);
            }
        });

        final ImageButton menuButton = (ImageButton) mCustomView.findViewById(R.id.title_settings_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, menuButton);
                popup.getMenuInflater().inflate(R.menu.my_options_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(MainActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        switch (item.getItemId()) {
                            case R.id.setting_1:
                                startActivity(new Intent(MainActivity.this, ChangeSettingsActivity.class));
                                break;
                            case R.id.setting_2:
                                Toast.makeText(MainActivity.this, "SOS", Toast.LENGTH_SHORT).show();
                                sendSOS();
                                break;
                            case R.id.setting_3:
                                startActivity(new Intent(MainActivity.this, DevSettingsActivity.class));
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    private void updateLevel() {
        int level = getSharedPreferences(Globals.PREFS_NAME, 0).getInt(Globals.USER_LEVEL, Globals.DEFAULT_USER_LEVEL);
        levelTextView.setText(level + "");
    }

    // Send message to emergency contact
    private void sendSOS() {
        SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
        String phoneNumber = settings.getString(Globals.PHONE_NUMBER, Globals.DEFAULT_PHONE_NUMBER);
        String name = settings.getString(Globals.USER_NAME, Globals.DEFAULT_USER_NAME);
        try {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, name + " has pressed the SOS button", null, null);
            Log.d(DEBUG_TAG, "SMS sent to " + phoneNumber);
//            Toast.makeText(getBaseContext(), "SMS Sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "Error sending SMS to " + phoneNumber);
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "Error Sending SMS", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadQuoteOfTheDay() {
        quoteTextView = (TextView) findViewById(R.id.quoteText);
        authorTextView = (TextView) findViewById(R.id.main_author_textview);

        if (quoteOfTheDay != null) {
            updateQuoteBoxes(false);
        } else {
            String quoteURL = "http://quotes.rest/qod.json?category=inspire";
            try {
                URL url = new URL(quoteURL);
                new GetBasicRequestTask().execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startStatsActivity() {
        Intent intent = new Intent(getBaseContext(), StatsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateQuoteBoxes(boolean isError) {
        SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
        if (!isError) {
            quoteTextView.setText(quoteOfTheDay);
            authorTextView.setText(quoteAuthor);

            SharedPreferences.Editor editor = storage.edit();
            editor.putString(Globals.RECENT_QUOTE_TEXT, quoteOfTheDay);
            editor.putString(Globals.RECENT_QUOTE_AUTHOR, quoteAuthor);
            editor.commit();

        } else {
            String storedQuote = storage.getString(Globals.RECENT_QUOTE_TEXT, Globals.DEFAULT_RECENT_QUOTE_TEXT);
            String storedAuthor = storage.getString(Globals.RECENT_QUOTE_AUTHOR, Globals.DEFAULT_RECENT_QUOTE_AUTHOR);
            quoteTextView.setText(storedQuote);
            authorTextView.setText(storedAuthor);
        }
    }

    private void parseBasicJSONString(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject contents = obj.getJSONObject("contents");
            JSONArray quotes = contents.getJSONArray("quotes");
            JSONObject quote = quotes.getJSONObject(0);
            quoteOfTheDay = quote.getString("quote");
            quoteAuthor = quote.getString("author");
            updateQuoteBoxes(false);
            Log.d(DEBUG_TAG, "Quote: " + quoteOfTheDay);
            Log.d(DEBUG_TAG, "Author: " + quoteAuthor);

        } catch (Exception e) {
            e.printStackTrace();
            updateQuoteBoxes(true);
        }
    }

    private class GetBasicRequestTask extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... urls) {
            return handleUrls(urls);
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {}
        @Override
        protected void onPostExecute(String response) {
            Log.d(DEBUG_TAG, "Response: " + response);
            parseBasicJSONString(response);
        }
    }

    public String handleUrls(URL[] urls) {
        try {
            URLConnection urlConnection = urls[0].openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            JSONObject response = new JSONObject(responseStrBuilder.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateLevel();
                loadAchievements();
            }
        };
        regReceiver();
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Globals.LEVEL_UP_INTENT);

        this.registerReceiver(this.receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();

        regReceiver();
        Log.d(DEBUG_TAG, "Resumed");
        updateLevel();
        loadAchievements();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.unregisterReceiver(this.receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(this.receiver);
        } catch (Exception e) {
            // Who cares lol
        }
    }
}
