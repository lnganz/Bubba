package com.tminusone60.finalproject160;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
//        Log.v("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        String path = new String(messageEvent.getPath());
        Log.d("T", "Message Path: " + path);
        Log.d("T", "Message Value: " + value);
        Intent mainIntent;
        if (path.equalsIgnoreCase("/MainActivity")) {
            mainIntent = new Intent(this, MainActivity.class);
//            mainIntent.putExtra("Reps", value);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
        }
    }
}
