package com.example.burgernezz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    ImageButton GoFood, GrabFood, ShopeeFood;
    Uri weblink;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle("Mengenai Burgernezz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GoFood = findViewById(R.id.GoFood);
        GrabFood = findViewById(R.id.GrabFood);
        ShopeeFood = findViewById(R.id.shopeeFood);

        //Menjalankan gofood
        GoFood.setOnClickListener(view -> {
            weblink = Uri.parse("https://gofood.link/u/mybgvA");
            intent = new Intent(Intent.ACTION_VIEW, weblink);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Koneksi tidak bisa dijalankan.... Coba lagi", Toast.LENGTH_SHORT).show();
            }
        });

        //Menjalankan grabfood
        GrabFood.setOnClickListener(view -> {
            weblink = Uri.parse("https://food.grab.com/id/id/");
            intent = new Intent(Intent.ACTION_VIEW, weblink);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Koneksi tidak bisa dijalankan.... Coba lagi", Toast.LENGTH_SHORT).show();
            }
        });

        //Menjalankan shopeefood
        ShopeeFood.setOnClickListener(view -> {
            weblink = Uri.parse("https://www.shopeefood.co.id/");
            intent = new Intent(Intent.ACTION_VIEW, weblink);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Koneksi tidak bisa dijalankan.... Coba lagi", Toast.LENGTH_SHORT).show();
            }
        });

    }
}