package com.ukrkosenko.cubikrubicktime.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ukrkosenko.cubikrubicktime.utils.Contains;
import com.ukrkosenko.cubikrubicktime.ui.main.adapter.ListAdapter;
import com.ukrkosenko.cubikrubicktime.R;
import com.ukrkosenko.cubikrubicktime.empty.Records;
import com.ukrkosenko.cubikrubicktime.utils.TimerRun;
import com.ukrkosenko.cubikrubicktime.ui.info.InfoActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {
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
    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private final String mSkuId = "ads_2";
    private final String adUnitId = "ca-app-pub-2981423664535117/4013186062";
    private ImageButton payImageButton;
    private ImageButton infoImageButtonNoAds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenAndScreenOn();
        setContentView(R.layout.activity_main);
        initSharedPrefs();
        iniBilling();
        init();
        initRecycler();
        setVariables();
        setListeners();
        setTheme();
        initPageBanner(initAdRequest());
    }

    private void iniBilling() {
        mBillingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(billingClientStateListener);
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        return purchasesResult.getPurchasesList();
    }

    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(mSkuId);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == 0) {
                    assert list != null;
                    for (SkuDetails skuDetails : list) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                    }
                }
            }
        });
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
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreenAndScreenOn();
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId))
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void initBanner() {
        boolean statusVisibility = sharedPreferences.getBoolean(Contains.STATUS_PAY_BUTTON, true);
        if (statusVisibility) {
            mAdView.loadAd(initAdRequest());
        } else {
            mAdView.setVisibility(View.INVISIBLE);
            infoImageButtonNoAds.setVisibility(View.VISIBLE);
            payImageButton.setVisibility(View.INVISIBLE);
            infoImageButton.setVisibility(View.INVISIBLE);
        }

    }

    private void initPageBanner(AdRequest adRequest) {
        InterstitialAd.load(this, adUnitId, adRequest,
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
        infoImageButtonNoAds = findViewById(R.id.info_image_button_no_ads);
        payImageButton = findViewById(R.id.payImageButton);
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
        infoImageButtonNoAds.setOnClickListener(noAdsImageButtonOnClickListener);
        payImageButton.setOnClickListener(payImageButtonOnClickListener);
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
            startInfoActivity();
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
            if (event.getAction() == MotionEvent.ACTION_UP) {
                checkClickIfTimerStart();
            }
            return false;
        }
    };

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            payComplete();
        }
    }

    private void payComplete() {
        sharedPrefsEditor.putBoolean(Contains.STATUS_PAY_BUTTON, false).apply();
        payImageButton.setVisibility(View.INVISIBLE);
        infoImageButton.setVisibility(View.INVISIBLE);
        infoImageButtonNoAds.setVisibility(View.VISIBLE);
        mAdView.setVisibility(View.GONE);
        mAdView.destroy();
    }

    private void startInfoActivity() {
        Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
        startActivity(infoActivity);
    }

    private final View.OnClickListener noAdsImageButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startInfoActivity();
        }
    };

    private final View.OnClickListener payImageButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                launchBilling(mSkuId);
            } catch (IllegalArgumentException e) {
                e.getStackTrace();
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final BillingClientStateListener billingClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingServiceDisconnected() {
            Log.d("TAG_INAPP", "Billing client Disconnected");
        }

        @Override
        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                querySkuDetails();
                List<Purchase> purchasesList = queryPurchases();
                for (int i = 0; i < purchasesList.size(); i++) {
                    ArrayList<String> purchaseId = purchasesList.get(i).getSkus();
                    if (TextUtils.equals(mSkuId, purchaseId.get(i))) {
                        payComplete();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBanner();
                            }
                        });

                    }
                }
            }
        }
    };
}