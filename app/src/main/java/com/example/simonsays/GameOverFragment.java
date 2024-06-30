package com.example.simonsays;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

        TextView scoreLabel = view.findViewById(R.id.game_over_score_label);
        scoreLabel.setText(getString(R.string.game_over_score_label, score));

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
}