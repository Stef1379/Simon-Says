package com.example.simonsays;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.ConsentType;
import com.google.firebase.analytics.FirebaseAnalytics.ConsentStatus;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ConsentInformation consentInformation;
    private String purposeConsents;

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
                    else setFirebaseConsent();
                }
        ));

        ConsentRequestParameters consentRequestParameters = new ConsentRequestParameters
                .Builder()
                .setConsentDebugSettings(new ConsentDebugSettings.Builder(this).addTestDeviceHashedId("F7175D9049F7E908B3A017445509B72A").build())
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.reset();
        consentInformation.requestConsentInfoUpdate(
                this,
                consentRequestParameters,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        this,
                        loadAndShowError -> {
                            if (loadAndShowError != null) logError(loadAndShowError);
                            if (isPrivacyOptionsRequired()) {
                                privacySettingsButton.setVisibility(View.VISIBLE);
                                setFirebaseConsent();
                            }

                            startGame.setEnabled(true);
                        }
                ),
                this::logError);
    }

    public boolean isPrivacyOptionsRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    private void setFirebaseConsent() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        purposeConsents = sharedPref.getString("IABTCF_PurposeConsents", "");
        if (purposeConsents.isEmpty()) return;

        boolean hasAnalyticsConsent = hasConsent(6) && hasConsent(7) && hasConsent(8) && hasConsent(9);
        boolean hasAdsConsent = hasConsent(0);
        boolean hasPersonalizationConsent = hasConsent(2) && hasConsent(3);
        boolean hasUserDataConsent = hasAdsConsent && hasConsent(1) && hasPersonalizationConsent;

        Map<ConsentType, ConsentStatus> consentMap = new EnumMap<>(ConsentType.class);
        putConsent(consentMap, ConsentType.ANALYTICS_STORAGE, hasAnalyticsConsent);
        putConsent(consentMap, ConsentType.AD_STORAGE, hasAdsConsent);
        putConsent(consentMap, ConsentType.AD_PERSONALIZATION, hasPersonalizationConsent);
        putConsent(consentMap, ConsentType.AD_USER_DATA, hasUserDataConsent);

        FirebaseAnalytics.getInstance(this).setConsent(consentMap);
    }

    private void putConsent(Map<ConsentType, ConsentStatus> consentMap,
                            ConsentType consentType,
                            boolean granted) {
        consentMap.put(consentType, granted ? ConsentStatus.GRANTED : ConsentStatus.DENIED);
    }

    private boolean hasConsent(int purpose) {
        return !purposeConsents.isEmpty() && purposeConsents.length() > purpose && purposeConsents.charAt(purpose) == '1';
    }

    private void logError(FormError formError) {
        Log.w(TAG, String.format("%s: %s",
                formError.getErrorCode(),
                formError.getMessage()));
    }
}