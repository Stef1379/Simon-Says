package com.example.simonsays;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

public class MainActivity extends AppCompatActivity {

    private ConsentInformation consentInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        Button startGame = findViewById(R.id.btn_start);
        startGame.setEnabled(false);
        startGame.setOnClickListener(_ -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        Button privacySettingsButton = findViewById(R.id.btn_privacy);
        privacySettingsButton.setOnClickListener(_ -> UserMessagingPlatform.showPrivacyOptionsForm(
                this,
                formError -> {
                    if (formError != null) logError(formError);
                }
        ));


        ConsentRequestParameters consentRequestParameters = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(true)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                consentRequestParameters,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        this,
                        loadAndShowError -> {
                            if (loadAndShowError != null) logError(loadAndShowError);

                            if (isPrivacyOptionsRequired()) privacySettingsButton.setVisibility(View.VISIBLE);
                            else privacySettingsButton.setVisibility(View.GONE);

                            startGame.setEnabled(true);
                        }
                ),
                this::logError);
    }

    public boolean isPrivacyOptionsRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    private void logError(FormError formError) {
        Log.w(TAG, String.format("%s: %s",
                formError.getErrorCode(),
                formError.getMessage()));
    }
}