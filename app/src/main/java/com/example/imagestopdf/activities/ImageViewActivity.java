package com.example.imagestopdf.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.imagestopdf.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ImageViewActivity extends AppCompatActivity {

    private String image;

    private ImageView imageTv;
    private MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        

        imageTv = findViewById(R.id.imageTv);


        image = getIntent().getStringExtra("imageUri");

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.baseline_image_24)
                .into(imageTv);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}