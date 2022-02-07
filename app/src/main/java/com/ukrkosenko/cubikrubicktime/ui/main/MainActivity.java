package com.ukrkosenko.cubikrubicktime.ui.main;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ukrkosenko.cubikrubicktime.Contains;
import com.ukrkosenko.cubikrubicktime.ui.main.adapter.ListAdapter;
import com.ukrkosenko.cubikrubicktime.R;
import com.ukrkosenko.cubikrubicktime.empty.Records;
import com.ukrkosenko.cubikrubicktime.TimerRun;
import com.ukrkosenko.cubikrubicktime.ui.info.InfoActivity;

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
    private InterstitialAd mInterstitialAd;
    private ConstraintLayout constraintLayout;
    private ImageButton changeThemeImageButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenAndScreenOn();
        setContentView(R.layout.activity_main);
        init();
        initRecycler();
        setVariables();
        setListeners();
        initSharedPrefs();
        setTheme();

        //   initBanner();
        //  initPageBanner(initAdRequest());
    }

    private void initSharedPrefs() {
        sharedPreferences = getSharedPreferences(Contains.PREFS_NAME, MODE_PRIVATE);
        sharedPrefsEditor = sharedPreferences.edit();
    }
    
    private void setFullScreenAndScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreenAndScreenOn();
    }

    private void initBanner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        mAdView.loadAd(initAdRequest());
    }

    private void initPageBanner(AdRequest adRequest) {
        InterstitialAd.load(this, "ca-app-pub-2981423664535117/4013186062", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    private AdRequest initAdRequest() {
        return new AdRequest.Builder().build();
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
        mAdView = findViewById(R.id.adView);
        constraintLayout = findViewById(R.id.home_linear_layout);
        changeThemeImageButton = findViewById(R.id.theme_imageView);
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
            timeTextView.setTextColor(getColor(R.color.colorTimer));
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
        mAdapter = new ListAdapter(timeTextOnClickListener);
        recordRecyclerView.setAdapter(mAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        infoImageButton.setOnClickListener(imageButtonOnClickListener);
        timeTextView.setOnClickListener(timeTextOnClickListener);
        constraintLayout.setOnClickListener(timeTextOnClickListener);
        timeTextView.setOnLongClickListener(timeTextOnLongClickListener);
        timeTextView.setOnTouchListener(timeTextViewOnTouchListener);
        changeThemeImageButton.setOnClickListener(changeThemeOnClickListener);
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
        try {
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
        } catch (ArrayIndexOutOfBoundsException e) {
            minTextView.setText(R.string.error);
        }
    }

    private final View.OnClickListener timeTextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkFirstStart();
            if (timerRun.getStatus()) {
                timerRun.pause();
                timeTextView.setTextColor(getColor(R.color.colorTimer));
                setNewItem();
                recordRecyclerView.scrollToPosition(resultList.size() - 1);
            } else {
                timeTextView.setTextColor(getColor(R.color.colorTimer));
            }
        }
    };

    private final View.OnClickListener imageButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            } else {
                Log.d(Contains.LOG, "null");
            }
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

    private final View.OnClickListener changeThemeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPrefsEditor.putInt(Contains.THEME_ID, currentNightMode).apply();
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPrefsEditor.putInt(Contains.THEME_ID, currentNightMode).apply();
                    break;
            }
        }
    };

    private void setTheme() {
        int currentNightMode = sharedPreferences.getInt(Contains.THEME_ID, 0);
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPrefsEditor.putInt(Contains.THEME_ID, currentNightMode).apply();
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPrefsEditor.putInt(Contains.THEME_ID, currentNightMode).apply();
                break;
        }
    }

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