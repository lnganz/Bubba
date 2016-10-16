package com.tminusone60.finalproject160;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class WatchToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private final String DEBUG_TAG = "WatchToPhoneService";

    private GoogleApiClient mWatchApiClient;
    private List<Node> nodes = new ArrayList<>();
    private String toActivity;
    private String path;
    private String message;
    private String recordTime;
    private String recordFeeling;
    private boolean started = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            toActivity = intent.getStringExtra("Activity");
            recordTime = intent.getStringExtra("RecordTime");
            recordFeeling = intent.getStringExtra("RecordFeeling");
            if (toActivity != null) {
                path = "Activity";
                message = toActivity;
            } else if (recordTime != null) {
                path = "Record";
                message = recordTime + ";" + recordFeeling;
            }

            Log.d(DEBUG_TAG, "Service Started");
            if (started) {
                if (message != null) {
                    Log.d(DEBUG_TAG, "Sending message: " + message);
                    sendMessage("/" + path, message);
                } else {
                    Log.d(DEBUG_TAG, "No Message To Send");
                }
            } else {
                started = true;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mWatchApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .build();
        //and actually connect it
        mWatchApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override //alternate method to connecting: no longer create this in a new thread, but as a callback
    public void onConnected(Bundle bundle) {
        Log.d(DEBUG_TAG, "in onconnected");

        path = "DEFAULT";
        if (bundle != null) {
            toActivity = bundle.getString("Activity");
            recordTime = bundle.getString("RecordTime");
            recordFeeling = bundle.getString("RecordFeeling");
            if (toActivity != null) {
                path = "Activity";
                message = toActivity;
            } else if (recordTime != null) {
                path = "Record";
                message = recordTime + ";" + recordFeeling;
            }
        }
        Wearable.NodeApi.getConnectedNodes(mWatchApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        nodes = getConnectedNodesResult.getNodes();
                        Log.d(DEBUG_TAG, "found nodes");
                        //when we find a connected node, we populate the list declared above
                        //finally, we can send a message
//                        sendMessage("Activity", activity);
                        if (message != null) {
                            Log.d(DEBUG_TAG, "Sending message: " + message);
                            sendMessage("/" + path, message);
                        } else {
                            Log.d(DEBUG_TAG, "No Message To Send");
                        }
//                        sendMessage("/task", task);
                        Log.d(DEBUG_TAG, "sent");
                    }
                });
    }

    @Override //we need this to implement GoogleApiClient.ConnectionsCallback
    public void onConnectionSuspended(int i) {}

    private void sendMessage(final String path, final String text ) {
        for (Node node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mWatchApiClient, node.getId(), path, text.getBytes());
        }
    }
}
