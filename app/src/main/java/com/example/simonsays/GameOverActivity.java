package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        MediaPlayer soundGameOver = MediaPlayer.create(this, R.raw.fail);
        soundGameOver.start();

        int score = getIntent().getIntExtra("score", 0);

        TextView scoreLabel = findViewById(R.id.game_over_score_label);
        scoreLabel.setText(getString(R.string.game_over_score_label, score));

        Button restartButton = findViewById(R.id.btn_restart);
        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        Button ExitButton = findViewById(R.id.btn_exit);
        ExitButton.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });
    }
}