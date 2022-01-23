package com.ukrkosenko.cubikrubicktime.ui.info;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ukrkosenko.cubikrubicktime.R;

public class InfoActivity extends AppCompatActivity {
    private ImageView firstImageView;
    private ImageView secondImageView;
    private ImageView thirdImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info);
        init();
    }

    private void init() {
        firstImageView = findViewById(R.id.first_mage_view);
        Picasso.get().load("https://i2.piccy.info/i9/d6f2a22082c55e2c8c6b61929c914dc0/1642949441/14057/1453485/12542f53_285d_4197_a478_eb9ab908a189.jpg").into(firstImageView);
        secondImageView = findViewById(R.id.second_image_view);
        Picasso.get().load("https://i2.piccy.info/i9/3b6d881a67ab62b79714e861fe2a3c17/1642949943/17981/1453485/photo1642949301.jpg").into(secondImageView);
        thirdImageView = findViewById(R.id.third_image_view);
        Picasso.get().load("https://i2.piccy.info/i9/40ba452ff915c9bbabac704ce2aafd92/1642950160/20188/1453485/photo1642949315.jpg").into(thirdImageView);
    }
}