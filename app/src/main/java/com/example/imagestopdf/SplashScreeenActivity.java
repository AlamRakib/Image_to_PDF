package com.example.imagestopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imagestopdf.activities.MainActivity;

public class SplashScreeenActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;

    Animation up,down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splash_screeen);


        //for full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screeen);
        getSupportActionBar();

        textView = findViewById(R.id.apppname);
        up = AnimationUtils.loadAnimation(SplashScreeenActivity.this,R.anim.up_from_bottom_slow);
        textView.startAnimation(up);

        imageView = findViewById(R.id.logo);
        down = AnimationUtils.loadAnimation(SplashScreeenActivity.this,R.anim.down);
        imageView.startAnimation(down);



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        },3500);
    }

}