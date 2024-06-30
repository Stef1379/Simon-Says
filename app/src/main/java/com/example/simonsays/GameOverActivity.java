package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.media.MediaPlayer;
import android.os.Bundle;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.game_over_fragment_container, new GameOverFragment()).commit();

        MediaPlayer.create(this, R.raw.fail).start();
    }
}