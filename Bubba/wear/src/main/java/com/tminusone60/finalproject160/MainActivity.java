package com.tminusone60.finalproject160;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private SensorManager mSensorManager;
    private Sensor mSigMotion;
    private TriggerEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSigMotion = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

        setupListener();

        initializeButtons();

        initializeAnimations();
//        final ImageView bubba = (ImageView) findViewById(R.id.main_bubba);
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                bubba.setBackgroundResource(R.drawable.bubba_idle_animation);
//                AnimationDrawable bubbaAnimation = (AnimationDrawable) bubba.getBackground();
//                if (bubbaAnimation.isRunning()) {
//                    bubbaAnimation.stop();
//                }
//                bubbaAnimation.start();
//            }
//        }, 0, 5000);

    }

    private void setupListener() {
        mListener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                shakeHandler();
//                setupListener();
            }
        };
    }

    private void shakeHandler() {
        ImageView bubbaView = (ImageView) findViewById(R.id.main_bubba);
        bubbaView.setBackgroundResource(R.drawable.bubba_eyespin_animation);
        AnimationDrawable bubbaAnimation = (AnimationDrawable) bubbaView.getBackground();
        if (bubbaAnimation.isRunning()) {
            bubbaAnimation.stop();
        }
        bubbaAnimation.setOneShot(true);
        bubbaAnimation.start();
        mSensorManager.requestTriggerSensor(mListener, mSigMotion);
    }

    private void initializeAnimations() {
        final ImageView bubba = (ImageView) findViewById(R.id.main_bubba);
        bubba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubba.setBackgroundResource(R.drawable.bubba_idle_animation);
                AnimationDrawable bubbaAnimation = (AnimationDrawable) bubba.getBackground();
                bubbaAnimation.setOneShot(true);
                if (bubbaAnimation.isRunning()) {
                    bubbaAnimation.stop();
                }
                bubbaAnimation.start();
            }
        });
        bubba.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bubba.setBackgroundResource(R.drawable.bubba_tongue_animation);
                AnimationDrawable bubbaAnimation = (AnimationDrawable) bubba.getBackground();
                if (bubbaAnimation.isRunning()) {
                    bubbaAnimation.stop();
                }
                bubbaAnimation.start();
                bubbaAnimation.setOneShot(true);
                return true;
            }
        });

    }

    private void initializeButtons() {
        ImageButton foodButton = (ImageButton) findViewById(R.id.main_food_button);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FeelingActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mSensorManager.requestTriggerSensor(mListener, mSigMotion);

        ImageView bubba = (ImageView) findViewById(R.id.main_bubba);
        bubba.setBackgroundResource(R.drawable.bubba_idle_animation);
        AnimationDrawable bubbaAnimation = (AnimationDrawable) bubba.getBackground();
        bubbaAnimation.setOneShot(true);
        if (bubbaAnimation.isRunning()) {
            bubbaAnimation.stop();
        }
        bubbaAnimation.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.cancelTriggerSensor(mListener, mSigMotion);
    }
}
