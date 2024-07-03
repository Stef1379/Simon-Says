package com.example.simonsays.Helpers;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.simonsays.R;

public class AudioHelper {

    private final Context context;

    private MediaPlayer audioRedButton;
    private MediaPlayer audioBlueButton;
    private MediaPlayer audioGreenButton;
    private MediaPlayer audioYellowButton;

    public AudioHelper(Context context) {
        this.context = context;
        setupButtonsAudio();
    }

    public void setupButtonsAudio() {
        audioRedButton = MediaPlayer.create(context, R.raw.sound_red_button);
        audioBlueButton = MediaPlayer.create(context, R.raw.sound_blue_button);
        audioGreenButton = MediaPlayer.create(context, R.raw.sound_green_button);
        audioYellowButton = MediaPlayer.create(context, R.raw.sound_yellow_button);
    }

    public MediaPlayer createGameOverAudio() {
        return MediaPlayer.create(context, R.raw.fail);
    }

    public void playAudio(String buttonColor) {
        switch (buttonColor.toLowerCase()) {
            case "red":
                startMediaPlayer(audioRedButton);
                break;
            case "blue":
                startMediaPlayer(audioBlueButton);
                break;
            case "green":
                startMediaPlayer(audioGreenButton);
                break;
            case "yellow":
                startMediaPlayer(audioYellowButton);
                break;
        }
    }

    public void startMediaPlayer(MediaPlayer player) {
        player.seekTo(0);
        player.start();
    }

    public void stopMediaPlayers() {
        stopMediaPlayer(audioRedButton);
        stopMediaPlayer(audioBlueButton);
        stopMediaPlayer(audioGreenButton);
        stopMediaPlayer(audioYellowButton);
    }

    public void releaseMediaPlayers() {
        releaseMediaPlayer(audioRedButton);
        releaseMediaPlayer(audioBlueButton);
        releaseMediaPlayer(audioGreenButton);
        releaseMediaPlayer(audioYellowButton);
    }

    public void stopMediaPlayer(MediaPlayer player) {
        if (player != null && player.isPlaying()) player.stop();
    }

    public void releaseMediaPlayer(MediaPlayer player){
        stopMediaPlayer(player);
        if (player != null) player.release();
    }
}
