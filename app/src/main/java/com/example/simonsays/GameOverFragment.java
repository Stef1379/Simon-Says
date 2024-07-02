package com.example.simonsays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GameOverFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_over, container, false);

        if (getActivity() == null) return view;

        int score = getActivity().getIntent().getIntExtra("score", 0);
        int highScore = getHighScore();
        if (score > highScore) {
            setHighScore(score);
            highScore = score;
        }

        TextView scoreLabel = view.findViewById(R.id.game_over_score_label);
        scoreLabel.setText(getString(R.string.game_over_score_label, score));

        TextView highScoreLabel = view.findViewById(R.id.game_over_high_score_label);
        highScoreLabel.setText(getString(R.string.game_over_high_score_label, highScore));

        Button restartButton = view.findViewById(R.id.btn_restart);
        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), GameActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        Button ExitButton = view.findViewById(R.id.btn_exit);
        ExitButton.setOnClickListener(v -> {
            getActivity().finish();
            System.exit(0);
        });

        return view;
    }

    private int getHighScore() {
        if (getActivity() == null) return 0;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SimonSaysScore", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("score", 0);
    }

    private void setHighScore(int score) {
        if (getActivity() == null) return;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SimonSaysScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("score", score);
        editor.apply();
    }
}