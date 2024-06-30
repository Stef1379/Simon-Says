package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> consentMap = new EnumMap<>(FirebaseAnalytics.ConsentType.class);
        consentMap.put(FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE, FirebaseAnalytics.ConsentStatus.GRANTED);
        consentMap.put(FirebaseAnalytics.ConsentType.AD_STORAGE, FirebaseAnalytics.ConsentStatus.GRANTED);
        consentMap.put(FirebaseAnalytics.ConsentType.AD_USER_DATA, FirebaseAnalytics.ConsentStatus.GRANTED);
        consentMap.put(FirebaseAnalytics.ConsentType.AD_PERSONALIZATION, FirebaseAnalytics.ConsentStatus.GRANTED);

        firebaseAnalytics.setConsent(consentMap);


        Button startGame = findViewById(R.id.btn_start);
        startGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }
}