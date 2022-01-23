package com.ukrkosenko.cubikrubicktime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView timeTextView;
    private RecyclerView recordRecyclerView;
    private ListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private AdView mAdView;
    private TextView minTextView;
    private List<Integer> resultList;
    private List<Records> listRecords;
    private ImageButton infoImageButton;
    private TimerRun timerRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
        initRecycler();
        setVariables();
        setListeners();

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        mAdView.loadAd(adRequest);


    }

    private void setVariables() {
        timerRun = new TimerRun(timeTextView);
        resultList = new ArrayList<>();
        listRecords = new ArrayList();
    }

    private void init() {
        timeTextView = findViewById(R.id.timeText);
        infoImageButton = findViewById(R.id.info_image_button);
        minTextView = findViewById(R.id.min_text_view);
        recordRecyclerView = findViewById(R.id.record_recycler_view);
    }

    private void checkFirstStart() {
        if (timeTextView.getText().equals(Contains.TIME_NULL_SIX)) {
            timeTextView.setText(Contains.TIME_NULL);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setNewItem() {
        if (!timeTextView.getText().equals(Contains.TIME_NULL)) {
            convertToInt(timerRun.textView.getText().toString());
            mAdapter.addItems(new Records(timerRun.textView.getText().toString()));
            mAdapter.notifyDataSetChanged();
        }
    }

    private void checkClickIfTimerStart() {
        if (timeTextView.getText().equals(Contains.TIME_NULL)) {
            timeTextView.setTextColor(Color.WHITE);
            if (!timerRun.getStatus()) {
                timerRun.start();
            }
        } else {
            if (!timerRun.getStatus()) {
                timeTextView.setTextColor(Color.RED);
            }
        }
    }

    private void initRecycler() {
        layoutManager = new GridLayoutManager(this, 1);
        recordRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ListAdapter();
        recordRecyclerView.setAdapter(mAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        infoImageButton.setOnClickListener(imageButtonOnClickListener);
        timeTextView.setOnClickListener(timeTextOnClickListener);
        timeTextView.setOnLongClickListener(timeTextOnLongClickListener);
        timeTextView.setOnTouchListener(timeTextViewOnTouchListener);
    }


    private void convertToInt(String time) {
        String result = time.replaceAll("\\.", "");
        int i = Integer.parseInt(result);
        resultList.add(i);
        int min = Collections.min(resultList);
        convertToStr(min);
    }

    @SuppressLint("SetTextI18n")
    private void convertToStr(int time) {
        String timeStr = String.valueOf(time);
        char[] strToArray = timeStr.toCharArray();
        for (int i = 0; i < strToArray.length; i++) {
            if (strToArray.length <= 2) {
                minTextView.setText("00.00." + strToArray[0] + strToArray[1]);
            } else {
                if (strToArray.length <= 3) {
                    minTextView.setText("00." + "0" + strToArray[0] + "." + strToArray[1] + strToArray[2]);
                } else {
                    if (strToArray.length <= 4) {
                        minTextView.setText("00." + strToArray[0] + strToArray[1] + "." + strToArray[2]
                                + strToArray[3]);
                    } else {
                        if (strToArray.length <= 5) {
                            minTextView.setText("0" + strToArray[0] + "." + strToArray[1] + strToArray[2]
                                    + "." + strToArray[3] + strToArray[4]);
                        } else {
                            if (strToArray.length <= 6) {
                                minTextView.setText(strToArray[0] + strToArray[1] + "." + strToArray[2]
                                        + strToArray[3] + "." + strToArray[4] + strToArray[5]);
                            } else {
                                minTextView.setText(getResources().getString(R.string.error));
                            }
                        }
                    }
                }
            }
        }
    }


    private final View.OnClickListener timeTextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkFirstStart();
            if (timerRun.getStatus()) {
                timerRun.pause();
                timeTextView.setTextColor(Color.WHITE);
                setNewItem();
            } else {
                timeTextView.setTextColor(Color.WHITE);
            }
        }
    };

    private final View.OnClickListener imageButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
            startActivity(infoActivity);
        }
    };

    private final View.OnLongClickListener timeTextOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (!timerRun.getStatus()) {
                timeTextView.setText(Contains.TIME_NULL);
                timeTextView.setTextColor(Color.BLUE);
            }
            return true;
        }
    };


    private final View.OnTouchListener timeTextViewOnTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    checkClickIfTimerStart();
                    break;
            }
            return false;
        }
    };
}