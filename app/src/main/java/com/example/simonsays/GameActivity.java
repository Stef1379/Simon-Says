package com.example.simonsays;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.simonsays.Helpers.AdHelper;
import com.example.simonsays.Helpers.AudioHelper;

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
    private Menu menu;

    private AudioHelper audioHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scoreText = findViewById(R.id.lbl_scoreText);
        RelativeLayout adContainerView = findViewById(R.id.game_ad_container);
        new AdHelper(this, adContainerView);
        audioHelper = new AudioHelper(this);

        lightedButtons = new ArrayList<>();
        score = 0;

        setupButtons();
        setButtonsClickListener();
        addLightButton();
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
        audioHelper.releaseMediaPlayers();
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

    private void resumeGame() {
        if (menu != null && AUTONOMOUS) toggleAutonomous(menu.findItem(R.id.action_toggle_mode));
        if (buttons.stream().noneMatch(View::hasOnClickListeners)) setButtonsClickListener();
        startCycle(score);
    }

    private void pauseGame() {
        mainHandler.removeCallbacksAndMessages(null);
        buttons.forEach(button -> button.setAlpha(0.3f));
        audioHelper.stopMediaPlayers();
    }

    private void startCycle(int score) {
        clickedButtons = new ArrayList<>();
        buttonClickCounter = 0;
        lightUpButtons();
        this.score = score;
        setScore();
    }

    private void lightUpButtons() {
        setButtonsClickListener();

        int totalDelay = 0;
        for (int i = 0; i < lightedButtons.size(); i++) {
            final Button lightedButton = lightedButtons.get(i);
            totalDelay += DURATION + DELAY;
            mainHandler.postDelayed(() -> lightButton(lightedButton, DURATION), totalDelay);
        }

        mainHandler.postDelayed(() -> {
            setButtonsClickListener();
            if (AUTONOMOUS) playGameAutonomous();
        }, totalDelay + DURATION);
    }

    private void lightButtonOnClickListener(View view) {
        if(buttonClickCounter >= lightedButtons.size() || view != lightedButtons.get(buttonClickCounter)) {
            gameOver();
            return;
        }

        clickButton((Button) view);

        if(clickedButtons.size() == lightedButtons.size()) {
            addLightButton();
            startCycle(score + 1);
        }
    }

    private void clickButton(Button button) {
        clickedButtons.add(button);
        buttonClickCounter++;
        lightButton(button, 200);
        audioHelper.playAudio((String) button.getTag());
    }

    private void lightButton(Button button, int duration) {
        button.setAlpha(1.0f);
        audioHelper.playAudio((String) button.getTag());
        mainHandler.postDelayed(() -> button.setAlpha(0.3f), duration);
    }

    private void addLightButton() {
        int number = random.nextInt(buttons.size());
        Button selectedButton = buttons.get(number);
        lightedButtons.add(selectedButton);
    }

    private void playGameAutonomous() {
        lightedButtons.forEach(button -> mainHandler.postDelayed(button::performClick, AUTONOMOUS_DELAY));
    }

    private void toggleAutonomous(MenuItem item) {
        AUTONOMOUS = !AUTONOMOUS;
        item.setTitle(AUTONOMOUS ? R.string.autonomous_mode : R.string.manual_mode);
    }

    private void setScore() {
        scoreText.setText(getString(R.string.score_label, score));
    }

    private void gameOver() {
        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    private void setButtonsClickListener() {
        for (Button button : buttons) {
            if(button.hasOnClickListeners()) button.setOnClickListener(null);
            else button.setOnClickListener(this::lightButtonOnClickListener);
        }
    }

    private void setupButtons() {
        buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.btn_blue));
        buttons.add(findViewById(R.id.btn_green));
        buttons.add(findViewById(R.id.btn_red));
        buttons.add(findViewById(R.id.btn_yellow));
    }
}