package com.tminusone60.finalproject160;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by lennon on 4/30/16.
 */
public class LevelUpActivity extends Activity {

    private final String DEBUG_TAG = "LevelUpActivity";

    private String achievementOnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_level_up);

        dimBackground();

        scaleLayout(0.7, 0.5);

        setLevelText();

        TextView closeText = (TextView) findViewById(R.id.level_close_text);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        achievementOnClose = null;
        Intent intent = getIntent();
        if (intent.hasExtra("Achievement")) {
            achievementOnClose = intent.getStringExtra("Achievement");
        }
    }

    private void setLevelText() {
        TextView levelText = (TextView) findViewById(R.id.levelup_text);
        int level = getSharedPreferences(Globals.PREFS_NAME, 0).getInt(Globals.USER_LEVEL, Globals.DEFAULT_USER_LEVEL);
        String text = "You've reached level " + level + ". Congratulations and keep it up!";
        levelText.setText(text);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (achievementOnClose != null) {
            Intent intent = new Intent(LevelUpActivity.this, AchievementActivity.class);
            intent.putExtra("Achievement", achievementOnClose);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
