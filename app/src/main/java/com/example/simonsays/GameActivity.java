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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final Random random = new Random();
    private static final long AUTONOMOUS_DELAY = 600;
    private static final int MIN_DURATION = 200;
    private static final int MAX_DURATION = 800;
    private static final int DELAY = 200;

    private static int DURATION = MAX_DURATION;
    private static boolean AUTONOMOUS = false;

    private List<Button> buttons;
    private List<Button> lightedButtons;
    private List<Button> clickedButtons;
    private int buttonClickCounter;
    private int score;

    private TextView scoreText;
    private Button autonomousButton;
    private Menu menu;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scoreText = findViewById(R.id.lbl_scoreText);
        adContainerView = findViewById(R.id.game_ad_container);
        loadBanner();
        adListener();

        lightedButtons = new ArrayList<>();
        score = 0;

        setupButtons();
        setupButtonsClickListener();
        setupSounds();
        addLightButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_speed) {
            toggleGameSpeed();
            toggleGameSpeedLabel(item);
            return true;
        }
        if (item.getItemId() == R.id.action_toggle_mode) {
            toggleAutonomous(item);
            if (buttons.stream().allMatch(View::hasOnClickListeners)) playGameAutonomous();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleGameSpeed() {
        DURATION = DURATION == MIN_DURATION ? MAX_DURATION : MIN_DURATION;
    }

    private void toggleGameSpeedLabel(MenuItem item) {
        if (item.getTitle() == getString(R.string.speed_1_label)) item.setTitle(R.string.speed_2_label);
        else item.setTitle(R.string.speed_1_label);
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
        adView = new AdView(this);
        adView.setAdSize(getAdSize());
        adView.setAdUnitId(getString(R.string.ad_unit_id));

        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void adListener() {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                adContainerView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAdClicked() {
                pauseGame();
            }

            @Override
            public void onAdClosed() {
                resumeGame();
            }

            @Override
            public void onAdOpened() {
                pauseGame();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(soundRedButton);
        releaseMediaPlayer(soundBlueButton);
        releaseMediaPlayer(soundGreenButton);
        releaseMediaPlayer(soundYellowButton);
    }

    private void resumeGame() {
        if (menu != null && AUTONOMOUS) toggleAutonomous(menu.findItem(R.id.action_toggle_mode));
        if (buttons.stream().noneMatch(View::hasOnClickListeners)) setupButtonsClickListener();
        startCycle(score);
    }

    private void pauseGame() {
        mainHandler.removeCallbacksAndMessages(null);
        buttons.forEach(button -> button.setAlpha(0.3f));
        stopMediaPlayer(soundRedButton);
        stopMediaPlayer(soundBlueButton);
        stopMediaPlayer(soundGreenButton);
        stopMediaPlayer(soundYellowButton);
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

        int totalDelay = 0;
        for (int i = 0; i < lightedButtons.size(); i++) {
            final Button lightedButton = lightedButtons.get(i);
            totalDelay += DURATION + DELAY;
            mainHandler.postDelayed(() -> lightButton(lightedButton, DURATION), totalDelay);
        }

        mainHandler.postDelayed(() -> {
            setupButtonsClickListener();
            if (AUTONOMOUS) playGameAutonomous();
        }, totalDelay + DURATION);
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

        if(clickedButtons.size() == lightedButtons.size()) {
            addLightButton();
            startCycle(score + 1);
        }
    }

    private void addLightButton() {
        int number = random.nextInt(buttons.size());
        Button selectedButton = buttons.get(number);
        lightedButtons.add(selectedButton);
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

    private void stopMediaPlayer(MediaPlayer player) {
        if (player != null && player.isPlaying()) player.stop();
    }

    private void releaseMediaPlayer(MediaPlayer player){
        if (player != null) player.release();
    }

    private void playGameAutonomous() {
        lightedButtons.forEach(button -> mainHandler.postDelayed(button::performClick, AUTONOMOUS_DELAY));
    }

    private void toggleAutonomous(MenuItem item) {
        AUTONOMOUS = !AUTONOMOUS;
        item.setTitle(AUTONOMOUS ? R.string.autonomous_mode : R.string.manual_mode);
    }
}