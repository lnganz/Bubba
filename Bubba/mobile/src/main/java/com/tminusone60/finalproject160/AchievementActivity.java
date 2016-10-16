package com.tminusone60.finalproject160;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lennon on 4/28/16.
 */
public class AchievementActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_achievement);

        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        scaleLayout(0.75, 0.5);

        TextView closeText = (TextView) findViewById(R.id.achievement_close);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String name = null;
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("Achievement");
            setAchievementIconImageAndDescription(name);
        }

    }

    private void setAchievementText(String description) {
         TextView aText = (TextView) findViewById(R.id.achievement_text);
         aText.setText(description);
    }

    private void setAchievementIconImageAndDescription(String name) {
        ImageView achievementIcon = (ImageView) findViewById(R.id.achievement_icon);
        if (name == null) {
            achievementIcon.setImageResource(R.drawable.button_help);

        } else {
            switch (name) {
                case "Star":
                    achievementIcon.setImageResource(Achievement.STAR.imageResourceId);
                    setAchievementText(Achievement.STAR.flavorText);
                    break;
                case "Check":
                    achievementIcon.setImageResource(Achievement.CHECK.imageResourceId);
                    setAchievementText(Achievement.CHECK.flavorText);
                    break;
                case "Gift":
                    achievementIcon.setImageResource(Achievement.GIFT.imageResourceId);
                    setAchievementText(Achievement.GIFT.flavorText);
                    break;
                case "Plus1":
                    achievementIcon.setImageResource(Achievement.PLUS1.imageResourceId);
                    setAchievementText(Achievement.PLUS1.flavorText);
                    break;
                case "Trophy":
                    achievementIcon.setImageResource(Achievement.TROPHY.imageResourceId);
                    setAchievementText(Achievement.TROPHY.flavorText);
                    break;
                case "Lock":
                    achievementIcon.setImageResource(Achievement.LOCK.imageResourceId);
                    setAchievementText(Achievement.LOCK.flavorText);
                    ((TextView) findViewById(R.id.achievement_header)).setText("Locked");
                    break;
                default:
                    achievementIcon.setImageResource(R.drawable.button_help);
            }
        }
    }

    private void scaleLayout(double xScaleFactor, double yScaleFactor) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * xScaleFactor), (int)(height * yScaleFactor));
    }
}
