package com.example.simonsays;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FourButtons extends Fragment {

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

        Handler handler = new Handler();
        int timeDelay = 0;
        for (int i = 1; i <= lightedButtons.size(); i++) {
            Button lightedButton = lightedButtons.get(i - 1);

            handler.postDelayed(() -> {
                lightButton(lightedButton, 1000);
            }, 1000L * i);
            timeDelay = i;
        }

        handler.postDelayed(() -> {
            Random random = new Random();
            int number = random.nextInt(buttons.size());

            Button selectedButton = buttons.get(number);
            lightButton(selectedButton, 1000);
            lightedButtons.add(selectedButton);
        }, 1000L * (timeDelay + 1));

        handler.postDelayed(this::setupButtonsClickListener, 1000L * (timeDelay + 2));
    }

    private void lightButton(Button button, int duration) {
        button.setAlpha(1.0f);

        playSounds((String) button.getTag());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            button.setAlpha(0.3f);
        }, duration);
    }

    private void buttonClick(View view) {
        if(buttonClickCounter >= lightedButtons.size()) {
            return;
        }

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
            if(button.hasOnClickListeners()) {
                button.setOnClickListener(null);
            } else {
                button.setOnClickListener(this::buttonClick);
            }
        }
    }

    private void playSounds(String buttonColor) {
        switch (buttonColor){
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