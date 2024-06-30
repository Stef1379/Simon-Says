package com.example.simonsays;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FourButtons extends Fragment {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final Random random = new Random();
    private static final long DELAY = 1200;

    private List<Button> buttons;
    private List<Button> lightedButtons;
    private List<Button> clickedButtons;
    private int buttonClickCounter;
    private int score;
    private View view;
    private TextView scoreText;

    private MediaPlayer soundRedButton;
    private MediaPlayer soundBlueButton;
    private MediaPlayer soundGreenButton;
    private MediaPlayer soundYellowButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_four_buttons, container, false);
        scoreText = view.findViewById(R.id.lbl_scoreText);

        lightedButtons = new ArrayList<>();

        setupButtons();
        setupButtonsClickListener();
        startCycle(0);

        soundRedButton = MediaPlayer.create(getContext(), R.raw.sound_red_button);
        soundBlueButton = MediaPlayer.create(getContext(), R.raw.sound_blue_button);
        soundGreenButton = MediaPlayer.create(getContext(), R.raw.sound_green_button);
        soundYellowButton = MediaPlayer.create(getContext(), R.raw.sound_yellow_button);

        return view;
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
        mainHandler.postDelayed(this::setupButtonsClickListener, totalDelay + DELAY);
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
        buttons.add(view.findViewById(R.id.btn_blue));
        buttons.add(view.findViewById(R.id.btn_green));
        buttons.add(view.findViewById(R.id.btn_red));
        buttons.add(view.findViewById(R.id.btn_yellow));
    }

    private void setupButtonsClickListener() {
        for (Button button : buttons) {
            if(button.hasOnClickListeners()) button.setOnClickListener(null);
            else button.setOnClickListener(this::buttonClick);
        }
    }

    private void gameOver() {
        if (getActivity() == null) return;

        Intent intent = new Intent(getContext(), GameOverActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        getActivity().finish();
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
}