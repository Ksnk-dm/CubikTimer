package com.ukrkosenko.cubikrubicktime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private ListView listView;
    private RecyclerView recordRecyclerView;
    private ListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private AdView mAdView;
    private TextView minTextView;
    private List<Integer> resultList = new ArrayList<>();
    private List<Records> listRecords = new ArrayList();
    private ImageButton infoImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.timeText);
        listView = findViewById(R.id.listRecord);
        infoImageButton = findViewById(R.id.info_image_button);
        infoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(infoActivity);
            }
        });
        minTextView = findViewById(R.id.min_text_view);
        recordRecyclerView = findViewById(R.id.record_recycler_view);
        layoutManager = new GridLayoutManager(this, 1);
        recordRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ListAdapter();
        recordRecyclerView.setAdapter(mAdapter);


//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        mAdView.loadAd(adRequest);

        StopWatc stopwatch = new StopWatc(textView);
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    stopwatch.pause();
                }
                return true;
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText().equals(Contains.TIME_NULL_SIX)) {
                    textView.setText(Contains.TIME_NULL);
                }


                if (stopwatch.getStatus()) {
                    stopwatch.pause();
                    textView.setTextColor(Color.WHITE);

                    if (!textView.getText().equals(Contains.TIME_NULL)) {
                        convertToInt(stopwatch.textView.getText().toString());
                        listRecords.add(new Records(stopwatch.textView.getText().toString()));
                        mAdapter.addItems(new Records(stopwatch.textView.getText().toString()));
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    textView.setTextColor(Color.WHITE);
                }
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!stopwatch.getStatus()) {
                    textView.setText(Contains.TIME_NULL);
                    textView.setTextColor(Color.BLUE);
                }

                return true;
            }
        });


        textView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (textView.getText().equals(Contains.TIME_NULL)) {
                            textView.setTextColor(Color.WHITE);
                            if (!stopwatch.getStatus()) {
                                stopwatch.start();
                            }
                        } else {
                            if (!stopwatch.getStatus()) {
                                textView.setTextColor(Color.RED);
                            }
                        }
                        break;
                }

                return false;
            }


//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    //do stuff here
//                    Log.d("touch", "torch");
//                    textView.setTextColor(Color.BLUE);
//                   // stopwatch.pause();
//                }
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    //do stuff here
//                    Log.d("touch", "otjal");
//                    textView.setTextColor(Color.WHITE);
//                    stopwatch.start();

//                    if (arraList.size() == 0) {
//                        arraList.add(new Records(""));
//
//                    } else {
//                        arraList.add(new Records(stopwatch.textView.getText().toString()));
//                        ArrayAdapter<Records> arrayAdapter
//                                = new ArrayAdapter<Records>(MainActivity.this, android.R.layout.simple_list_item_1, arraList);
//
//                        listView.setAdapter(arrayAdapter);
//
//                    }
//                    if (arraList.size() == 8) {
//                        arraList.clear();
//                    }
////
//
//                }
//                return true;
//            }
//        });

        });
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
                                minTextView.setText("error");
                            }
                        }
                    }
                }
            }
        }
    }
}