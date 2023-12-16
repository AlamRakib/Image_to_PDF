package com.example.imagestopdf.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.imagestopdf.ImagesListFragment;
import com.example.imagestopdf.PdfListFragment;
import com.example.imagestopdf.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomnavigationView);

        ImagesListFragment imagesListFragment = new ImagesListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,imagesListFragment,"ImageListFragment");
        fragmentTransaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();


                if(itemId == R.id. bottom_menu_images)
                {
                    loadImagesFragment();

                }
                else if (itemId==R.id.bottom_menu_pdfs) {

                    loadPdfsFragment();
                }

                return true;
            }



            private void loadImagesFragment() {

                setTitle("Images");
                ImagesListFragment imagesListFragment1 = new ImagesListFragment();
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.framelayout,imagesListFragment1,"ImageListFragment");
                fragmentTransaction1.commit();

            }

            private void loadPdfsFragment() {

                setTitle("PDF List");
                PdfListFragment pdfListFragment = new PdfListFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout,pdfListFragment,"PdfListFragment");
                fragmentTransaction.commit();
            }

        });
    }
}