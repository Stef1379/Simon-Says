package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.simonsays.Helpers.AudioHelper;

public class GameOverActivity extends AppCompatActivity {

    private AudioHelper audioHelper;
    private MediaPlayer audioGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.game_over_fragment_container, new GameOverFragment()).commit();

        audioHelper = new AudioHelper(this);
        audioGameOver = audioHelper.createGameOverAudio();
        audioHelper.startMediaPlayer(audioGameOver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioHelper.releaseMediaPlayer(audioGameOver);
    }

    @Override
    public void onPause() {
        super.onPause();
        audioHelper.stopMediaPlayer(audioGameOver);
    }
}