package com.tminusone60.finalproject160;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class FeelingActivity extends Activity {

    private boolean updated;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeling);

        updated = false;

        initializeSeekBar();

        ImageView background = (ImageView) findViewById(R.id.feeling_background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updated) {
                    Intent intent = new Intent(FeelingActivity.this, WatchToPhoneService.class);
                    long time = System.currentTimeMillis();
                    int feeling = seekBar.getProgress();
                    intent.putExtra("RecordTime", time + "");
                    intent.putExtra("RecordFeeling", feeling + "");
                    startService(intent);
                }
                finish();
            }
        });
    }

    private void initializeSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.feeling_seekbar);
        seekBar.setScaleX(2f);
        seekBar.setScaleY(2f);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updated = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                int curValue = seekBar.getProgress();
//                Toast.makeText(FeelingActivity.this, "Seek Value: " + curValue, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FeelingActivity.this, WatchToPhoneService.class);
                long time = System.currentTimeMillis();
                int feeling = seekBar.getProgress();
                intent.putExtra("RecordTime", time + "");
                intent.putExtra("RecordFeeling", feeling + "");
                startService(intent);
                finish();
            }
        });
    }
}
