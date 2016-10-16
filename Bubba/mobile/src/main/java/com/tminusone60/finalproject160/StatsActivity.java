package com.tminusone60.finalproject160;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {
    private final String DEBUG_TAG = "StatsActivity";

    private static final int NUM_GRAPH_POINTS = 10;

    private LineChart mChart;

    private BroadcastReceiver receiver;

    private TextView noDataTextView;
    private TextView levelTextView;
    private ProgressBar levelProgressBar;

//    private int feedCount = 0;
//    private int dayCount = 0;
//    private int lastDayFed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Request SMS permissions for API 23 and newer
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(StatsActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

//        final TextView dayText = (TextView) findViewById(R.id.testDayText);
//        final TextView countText = (TextView) findViewById(R.id.testCountText);

        loadLayout();

        setupChart();

        setupBroadcastReceiver();
    }

    private void setupBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(DEBUG_TAG, "Broadcast received");
                updateLevel();
                setupChart();
            }
        };
        regReceiver();
    }

    private void updateLevel() {
        levelTextView = (TextView) findViewById(R.id.level_text_dark);
        levelProgressBar = (ProgressBar) findViewById(R.id.stats_level_progress);
        SharedPreferences storage = getSharedPreferences(Globals.PREFS_NAME, 0);
        int level = storage.getInt(Globals.USER_LEVEL, Globals.DEFAULT_USER_LEVEL);
        int oldProgress = levelProgressBar.getProgress();
        int progress = storage.getInt(Globals.USER_EXPERIENCE, Globals.DEFAULT_USER_EXPERIENCE);
        levelTextView.setText(level + "");
        if (oldProgress <= progress) {
            ProgressBarAnimation anim = new ProgressBarAnimation(levelProgressBar, oldProgress, progress);
            anim.setDuration(Math.abs((progress - oldProgress)*10));
            levelProgressBar.startAnimation(anim);
        } else {
            levelProgressBar.setProgress(progress);
        }

    }

    private void loadLayout() {
        TextView recordText = (TextView) findViewById(R.id.stats_test_records);
        String recordString = getSharedPreferences(Globals.PREFS_NAME, 0).getString(Globals.FEELINGS_DATA, "NONE");
        recordText.setText(recordString);

        levelTextView = (TextView) findViewById(R.id.level_text_dark);
        updateLevel();

        ImageButton backButton = (ImageButton) findViewById(R.id.stats_button_x);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatsActivity.this, MainActivity.class));
                finish();
            }
        });

        noDataTextView = (TextView) findViewById(R.id.stats_no_data_text);
    }

    private void setupChart() {
        mChart = (LineChart) findViewById(R.id.chart1);

        mChart.setDescription("");
        mChart.setNoDataText("No data has been recorded.");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(false);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(110f);
        leftAxis.setAxisMinValue(-10f);
        leftAxis.setShowOnlyMinMax(true);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false); // Unsure
        leftAxis.setDrawLabels(false);


        leftAxis.setDrawGridLines(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData(7, 100);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setEnabled(false);

        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(false);
        // // dont forget to refresh the drawing
        mChart.invalidate();
    }

    private void setData(int count, float range) {

        SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME, 0);
        String feelingsData = settings.getString(Globals.FEELINGS_DATA, null);
        if (feelingsData != null) {
            noDataTextView.setVisibility(View.VISIBLE);
            ArrayList<String> yValues = new ArrayList<String>();
            Log.d(DEBUG_TAG, "FeelingsData: " + feelingsData);
            String[] pairs = feelingsData.split("\\|");
            for (int i = 1; i < pairs.length; i++) {
                Log.i(DEBUG_TAG, "Pair: " + pairs[i]);
                String[] time_feeling = pairs[i].split(";");
                Log.i(DEBUG_TAG, "Parsed Pair: " + time_feeling.toString());
                yValues.add(time_feeling[1]);
            }

            ArrayList<Entry> yEntries = new ArrayList<Entry>();
            ArrayList<String> xVals = new ArrayList<String>();
            int numValues = yValues.size();
            int limitNumValues = Math.min(numValues, NUM_GRAPH_POINTS);
            int chartIndex = 0;
            for (int i = numValues-limitNumValues; i < numValues; i++) {
                yEntries.add(new Entry(Integer.parseInt(yValues.get(i)), chartIndex));
                xVals.add((chartIndex) + "");
                chartIndex++;
            }

            toggleNoDataText(xVals.size());

            LineDataSet set1;
            set1 = new LineDataSet(yEntries, "DataSet 1");

            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"

//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);

            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(3f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(false);

            set1.setFillColor(Color.BLACK);

            set1.setDrawCubic(true); // Testing how slow this is and how it looks

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);
            data.setDrawValues(false);

            // set data
            mChart.setData(data);
        }
    }

    private void toggleNoDataText(int size) {
        if (size > 0) {
            noDataTextView.setVisibility(View.GONE);
        } else {
            noDataTextView.setVisibility(View.VISIBLE);
        }
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Globals.UPDATE_CHART_INTENT);
//        filter.addAction(Globals.LEVEL_UP_INTENT);

        this.registerReceiver(this.receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLevel();
        setupChart();
        regReceiver();

    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(this.receiver);
    }

    class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }
    }
}
