package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.ConsentType;
import com.google.firebase.analytics.FirebaseAnalytics.ConsentStatus;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Map<ConsentType, ConsentStatus> consentMap = new EnumMap<>(ConsentType.class);
        consentMap.put(ConsentType.ANALYTICS_STORAGE, ConsentStatus.GRANTED);
        consentMap.put(ConsentType.AD_STORAGE, ConsentStatus.GRANTED);
        consentMap.put(ConsentType.AD_USER_DATA, ConsentStatus.GRANTED);
        consentMap.put(ConsentType.AD_PERSONALIZATION, ConsentStatus.GRANTED);

        firebaseAnalytics.setConsent(consentMap);

        Button startGame = findViewById(R.id.btn_start);
        startGame.setOnClickListener(_ -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });
    }
}