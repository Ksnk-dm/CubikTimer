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
        Picasso.get().load("https://i.ibb.co/3ctyyB9/12542f53-285d-4197-a478-eb9ab908a189.jpg").into(firstImageView);
        secondImageView = findViewById(R.id.second_image_view);
        Picasso.get().load("https://i.ibb.co/84HVPSW/photo1642949301.jpg").into(secondImageView);
        thirdImageView = findViewById(R.id.third_image_view);
        Picasso.get().load("https://i.ibb.co/K7HHz0j/photo1642949315.jpg").into(thirdImageView);
    }
}