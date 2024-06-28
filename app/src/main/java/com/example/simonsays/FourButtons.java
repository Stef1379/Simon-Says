package com.example.simonsays;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private MediaPlayer soundRedButton;
    private MediaPlayer soundBlueButton;
    private MediaPlayer soundGreenButton;
    private MediaPlayer soundYellowButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_four_buttons, container, false);

        setupButtons();
        lightedButtons = new ArrayList<>();
        clickedButtons = new ArrayList<>();
        lightUpButton();
        buttonClickCounter = 0;
        score = 0;
        setScore();

        soundRedButton = MediaPlayer.create(getContext(), R.raw.sound_red_button);
        soundBlueButton = MediaPlayer.create(getContext(), R.raw.sound_blue_button);
        soundGreenButton = MediaPlayer.create(getContext(), R.raw.sound_green_button);
        soundYellowButton = MediaPlayer.create(getContext(), R.raw.sound_yellow_button);

        return view;
    }

    private void lightUpButton() {
        setupButtonsClickListener();

        int timeDelay = 0;
        for (int i = 1; i <= lightedButtons.size(); i++) {
            Button lightedButton = lightedButtons.get(i - 1);
            executorService.schedule(() -> mainHandler.post(() -> lightButton(lightedButton, 1000)), DELAY * i, TimeUnit.MILLISECONDS);
            timeDelay = i;
        }

        executorService.schedule(() -> {
            int number = random.nextInt(buttons.size());

            Button selectedButton = buttons.get(number);
            mainHandler.post(() -> {
                lightButton(selectedButton, 1000);
                lightedButtons.add(selectedButton);
            });
        }, DELAY * (timeDelay + 1), TimeUnit.MILLISECONDS);

        executorService.schedule(this::setupButtonsClickListener, DELAY * (timeDelay + 2), TimeUnit.MILLISECONDS);
    }


    private void lightButton(Button button, int duration) {
        button.setAlpha(1.0f);

        playSounds((String) button.getTag());

        mainHandler.postDelayed(() -> button.setAlpha(0.3f), duration);
    }

    private void buttonClick(View view) {
        if(buttonClickCounter >= lightedButtons.size()) return;

        clickedButtons.add((Button) view);
        if(clickedButtons.get(buttonClickCounter) != lightedButtons.get(buttonClickCounter) && getActivity() != null) {
            Intent intent = new Intent(getContext(), GameOverActivity.class);
            intent.putExtra("score", score);
            startActivity(intent);
            getActivity().finish();
            return;
        }

        lightButton((Button) view, 200);
        playSounds((String) view.getTag());
        buttonClickCounter++;

        if(clickedButtons.size() == lightedButtons.size()) {
            clickedButtons.clear();
            buttonClickCounter = 0;
            lightUpButton();
            score++;
            setScore();
        }
    }

    private void setScore() {
        TextView scoreText = view.findViewById(R.id.lbl_scoreText);
        scoreText.setText(getString(R.string.score_label, score));
    }

    private void setupButtons() {
        buttons = new ArrayList<>();
        buttons.add(view.findViewById(R.id.btn_blue));
        buttons.add(view.findViewById(R.id.btn_green));
        buttons.add(view.findViewById(R.id.btn_red));
        buttons.add(view.findViewById(R.id.btn_yellow));

        for (Button button : buttons) {
            button.setOnClickListener(this::buttonClick);
        }
    }

    private void setupButtonsClickListener() {
        for (Button button : buttons) {
            if(button.hasOnClickListeners()) button.setOnClickListener(null);
            else button.setOnClickListener(this::buttonClick);
        }
    }

    private void playSounds(String buttonColor) {
        switch (buttonColor.toLowerCase()) {
            case "red":
                soundRedButton.start();
                break;
            case "blue":
                soundBlueButton.start();
                break;
            case "green":
                soundGreenButton.start();
                break;
            case "yellow":
                soundYellowButton.start();
                break;
        }
    }
}