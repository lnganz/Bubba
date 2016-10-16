package com.tminusone60.finalproject160;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class PhoneListenerService extends WearableListenerService {

    private final String DEBUG_TAG = "PhoneListenerService";
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(DEBUG_TAG, "Received: " + messageEvent.getPath());

        if (messageEvent.getPath() != null) {
            final String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            final String fullPath = messageEvent.getPath();
            final String path = fullPath.replace("/", "");
            Log.d(DEBUG_TAG, "Path Received: " + path);
            Log.d(DEBUG_TAG, "Message Received: " + value);

            if (path.equalsIgnoreCase("Record")) {
                String[] timeAndFeeling = value.split(";");
                String time = timeAndFeeling[0];

                String feeling = timeAndFeeling[1];
                String toastMessage = "Time: " + time + " Feeling: " + feeling;

//                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show(); // Still doesn't work

                // Update data storage
                SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
                String oldData = settings.getString(Globals.FEELINGS_DATA, Globals.DEFAULT_FEELINGS_DATA);
                String newData = oldData + "|" + time + ";" + feeling;
                int oldLevel = settings.getInt(Globals.USER_LEVEL, Globals.DEFAULT_USER_LEVEL);
                int newLevel = oldLevel;
                int oldExperience = settings.getInt(Globals.USER_EXPERIENCE, Globals.DEFAULT_USER_EXPERIENCE);
                int newExperience = oldExperience + Globals.EXP_PER_FEED;
                int totalCount = settings.getInt(Globals.USER_FEED_COUNT, Globals.DEFAULT_USER_FEED_COUNT) + 1;
                String achievements = settings.getString(Globals.USER_ACHIEVEMENTS, Globals.DEFAULT_USER_ACHIEVEMENTS);
                String[] achievementList = achievements.split(";");
                ArrayList<String> updatedAchievements = new ArrayList<String>(Arrays.asList(achievementList));

                boolean levelUp = false;
                // LEVEL LOGIC
                if (newExperience >= Globals.EXP_PER_LEVEL) {
                    newLevel = oldLevel + 1;
                    newExperience = newExperience % Globals.EXP_PER_LEVEL;
                    levelUp = true;
                } else {
                    // Not sure why I had this else here
                }

                String recentAchievement = null;
                if (totalCount == 2) {
                    recentAchievement = Achievement.GIFT.name;
                    updatedAchievements.add(recentAchievement);
                }
                if (totalCount == 5) {
                    recentAchievement = Achievement.PLUS1.name;
                    updatedAchievements.add(recentAchievement);
                }
                if (totalCount == 8) {
                    recentAchievement = Achievement.STAR.name;
                    updatedAchievements.add(recentAchievement);
                }

                // Must be done in this order (1)
                if (levelUp) {
                    if (newLevel == 3) {
                        recentAchievement = Achievement.CHECK.name;
                        updatedAchievements.add(recentAchievement);
                    }
                }

                String updatedAchievementString = "";
                for (int i = 1; i < updatedAchievements.size(); i++) {
                    updatedAchievementString = updatedAchievementString + ";" + updatedAchievements.get(i);
                }

                // Must be done in this order (2)
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Globals.FEELINGS_DATA, newData);
                editor.putInt(Globals.USER_LEVEL, newLevel);
                editor.putInt(Globals.USER_EXPERIENCE, newExperience);
                editor.putInt(Globals.USER_FEED_COUNT, totalCount);
                editor.putString(Globals.USER_ACHIEVEMENTS, updatedAchievementString);
                editor.commit();

                // Must be done in this order (3)
                Intent activityIntent;
                Log.d(DEBUG_TAG, "Recent achievement: " + recentAchievement);
                if (levelUp) {
                    activityIntent = new Intent(PhoneListenerService.this, LevelUpActivity.class);
                    Log.d(DEBUG_TAG, "Starting LevelUpActivity");
                } else {
                    activityIntent = new Intent(PhoneListenerService.this, AchievementActivity.class);
                    Log.d(DEBUG_TAG, "Starting AchievementActivity");
                }
                if (levelUp || recentAchievement != null) {
                    activityIntent.putExtra("Achievement", recentAchievement);
                    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(activityIntent);
                }

                Intent levelIntent = new Intent();
                levelIntent.setAction(Globals.LEVEL_UP_INTENT);
                sendBroadcast(levelIntent);

                // Tell StatsActivity to update chart
                Intent chartIntent = new Intent();
                chartIntent.setAction(Globals.UPDATE_CHART_INTENT);
                sendBroadcast(chartIntent);


                // Set alarm for Notification/SMS
//                int days;
//                try {
//                    days = Integer.parseInt(daysToAlert);
//                } catch (Exception e) {
//                    days = 3; // Hardcoded alert day count
//                }
                int daysToAlert = settings.getInt(Globals.ALERT_DAY_COUNT, Globals.DEFAULT_ALERT_DAY_COUNT);
                Intent notificationIntent = new Intent(PhoneListenerService.this, ShowNotification.class);
                notificationIntent.putExtra("shouldFire", true);
//                notificationIntent.setAction(Globals.SEND_ALERTS_INTENT);
                PendingIntent contentIntent = PendingIntent.getService(PhoneListenerService.this, 0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.cancel(contentIntent);
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + ((AlarmManager.INTERVAL_FIFTEEN_MINUTES / 30) * daysToAlert), contentIntent); // FOR DEMO PURPOSES, 1 Day = 30 Seconds
//                        + AlarmManager.INTERVAL_DAY * SMS_ALERT_SETTING, contentIntent);
            }
//            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

        }
    }
}
