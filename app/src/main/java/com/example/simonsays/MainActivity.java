package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import android.net.LinkAddress;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        Button startGame = findViewById(R.id.btn_start);
        startGame.setOnClickListener(v -> {
            LinearLayout startGameLayout = findViewById(R.id.start_game_layout);
            startGameLayout.setVisibility(View.GONE);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.game_area_fragment, new FourButtons(), "four_buttons").commit();
        });
    }
}