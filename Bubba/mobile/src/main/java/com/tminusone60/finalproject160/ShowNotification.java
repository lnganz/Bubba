package com.tminusone60.finalproject160;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by lennon on 4/29/16.
 */
public class ShowNotification extends Service {
    private final static String DEBUG_TAG = "ShowNotification";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getBooleanExtra("shouldFire", false)) {
            Intent mainIntent = new Intent(this, MainActivity.class);

            NotificationManager notificationManager
                    = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification noti = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, mainIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle("Bubba")
                    .setContentText(Globals.NOTIFICATION_MESSAGE)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bubba_icon_blue))
                    .setSmallIcon(R.drawable.logo_bubba)
                    .setTicker("ticker message")
                    .setWhen(System.currentTimeMillis())
                    .build();

            SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
            if (settings.getBoolean(Globals.NOTIFICATIONS_ENABLED, Globals.DEFAULT_NOTIFICATIONS_ENABLED)) {
                notificationManager.notify(0, noti);
            }

            Log.d(DEBUG_TAG, "Notification created");


            if (settings.getBoolean(Globals.SMS_ENABLED, Globals.DEFAULT_SMS_ENABLED)) {
                String phoneNumber = settings.getString(Globals.PHONE_NUMBER, Globals.DEFAULT_PHONE_NUMBER);
                int maxDays = settings.getInt(Globals.ALERT_DAY_COUNT, Globals.DEFAULT_ALERT_DAY_COUNT);
                try {
                    String name = settings.getString(Globals.USER_NAME, Globals.DEFAULT_USER_NAME);
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, "Bubba Alert: " + name + " hasn't fed Bubba in " + maxDays + " days", null, null);
                    Log.d(DEBUG_TAG, "SMS sent to " + phoneNumber);
//            Toast.makeText(getBaseContext(), "SMS Sent!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d(DEBUG_TAG, "Error sending SMS to " + phoneNumber);
                    e.printStackTrace();
//            Toast.makeText(getBaseContext(), "Error Sending SMS", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
