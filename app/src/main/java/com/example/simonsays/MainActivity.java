package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        Button startGame = findViewById(R.id.btn_start);
        startGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }
}