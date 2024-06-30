package com.example.simonsays;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final Random random = new Random();
    private static final long DELAY = 1200;
    private static final long AUTONOMOUS_DELAY = 600;
    private static boolean AUTONOMOUS = false;

    private List<Button> buttons;
    private List<Button> lightedButtons;
    private List<Button> clickedButtons;
    private int buttonClickCounter;
    private int score;
    private TextView scoreText;

    private RelativeLayout adContainerView;
    private AdView adView;

    private MediaPlayer soundRedButton;
    private MediaPlayer soundBlueButton;
    private MediaPlayer soundGreenButton;
    private MediaPlayer soundYellowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        scoreText = findViewById(R.id.lbl_scoreText);
        adContainerView = findViewById(R.id.game_ad_container);
        loadBanner();

        lightedButtons = new ArrayList<>();

        setupButtons();
        setupButtonsClickListener();
        setupSounds();
        startCycle(0);

        Button autonomousButton = findViewById(R.id.btn_autonomous);
        autonomousButton.setOnClickListener(_ -> {
            AUTONOMOUS = !AUTONOMOUS;
            autonomousButton.setText(AUTONOMOUS ? R.string.disable_automatic_mode : R.string.enable_automatic_mode);
            if (buttons.stream().allMatch(View::hasOnClickListeners)) playGameAutonomous();
        });
    }

    public static Display getDefaultDisplay(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        return displayManager.getDisplay(Display.DEFAULT_DISPLAY);
    }

    private AdSize getAdSize() {
        Display display = getDefaultDisplay(this);
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);

        float density = outMetrics.density;
        float adWidthPixels = adContainerView.getWidth();
        if (adWidthPixels == 0) adWidthPixels = outMetrics.widthPixels;

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void loadBanner() {
        AdView adView = new AdView(this);
        adView.setAdSize(getAdSize());
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");

        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        mainHandler.removeCallbacksAndMessages(null);
        stopAndReleaseMediaPlayer(soundRedButton);
        stopAndReleaseMediaPlayer(soundBlueButton);
        stopAndReleaseMediaPlayer(soundGreenButton);
        stopAndReleaseMediaPlayer(soundYellowButton);
    }

    private void startCycle(int score) {
        clickedButtons = new ArrayList<>();
        buttonClickCounter = 0;
        lightUpButtons();
        this.score = score;
        setScore();
    }

    private void lightUpButtons() {
        setupButtonsClickListener();

        long totalDelay = 0;
        for (int i = 0; i < lightedButtons.size(); i++) {
            final Button lightedButton = lightedButtons.get(i);
            totalDelay += DELAY;
            mainHandler.postDelayed(() -> lightButton(lightedButton, 1000), totalDelay);
        }

        int number = random.nextInt(buttons.size());
        Button selectedButton = buttons.get(number);
        lightedButtons.add(selectedButton);

        totalDelay += DELAY;
        mainHandler.postDelayed(() -> lightButton(selectedButton, 1000), totalDelay);
        mainHandler.postDelayed(() -> {
            setupButtonsClickListener();
            if (AUTONOMOUS) playGameAutonomous();
        }, totalDelay + DELAY);
    }

    private void lightButton(Button button, int duration) {
        button.setAlpha(1.0f);
        playSounds((String) button.getTag());
        mainHandler.postDelayed(() -> button.setAlpha(0.3f), duration);
    }

    private void buttonClick(View view) {
        if(buttonClickCounter >= lightedButtons.size() || view != lightedButtons.get(buttonClickCounter)) {
            gameOver();
            return;
        }

        clickedButtons.add((Button) view);
        buttonClickCounter++;
        lightButton((Button) view, 200);
        playSounds((String) view.getTag());

        if(clickedButtons.size() == lightedButtons.size()) startCycle(score + 1);
    }

    private void setScore() {
        scoreText.setText(getString(R.string.score_label, score));
    }

    private void setupButtons() {
        buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.btn_blue));
        buttons.add(findViewById(R.id.btn_green));
        buttons.add(findViewById(R.id.btn_red));
        buttons.add(findViewById(R.id.btn_yellow));
    }

    private void setupButtonsClickListener() {
        for (Button button : buttons) {
            if(button.hasOnClickListeners()) button.setOnClickListener(null);
            else button.setOnClickListener(this::buttonClick);
        }
    }

    private void gameOver() {
        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    private void setupSounds() {
        soundRedButton = MediaPlayer.create(getApplicationContext(), R.raw.sound_red_button);
        soundBlueButton = MediaPlayer.create(getApplicationContext(), R.raw.sound_blue_button);
        soundGreenButton = MediaPlayer.create(getApplicationContext(), R.raw.sound_green_button);
        soundYellowButton = MediaPlayer.create(getApplicationContext(), R.raw.sound_yellow_button);

        soundRedButton.setVolume(1, 1);
        soundBlueButton.setVolume(1, 1);
        soundGreenButton.setVolume(1, 1);
        soundYellowButton.setVolume(1, 1);
    }

    private void playSounds(String buttonColor) {
        switch (buttonColor.toLowerCase()) {
            case "red":
                playSound(soundRedButton);
                break;
            case "blue":
                playSound(soundBlueButton);
                break;
            case "green":
                playSound(soundGreenButton);
                break;
            case "yellow":
                playSound(soundYellowButton);
                break;
        }
    }

    private void playSound(MediaPlayer player) {
        player.seekTo(0);
        player.start();
    }

    private void stopAndReleaseMediaPlayer(MediaPlayer player) {
        if (player == null) return;
        if (player.isPlaying()) player.stop();
        player.release();
    }

    private void playGameAutonomous() {
        lightedButtons.forEach(button -> mainHandler.postDelayed(button::performClick, AUTONOMOUS_DELAY));
    }
}