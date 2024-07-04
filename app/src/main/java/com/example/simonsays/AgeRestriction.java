package com.example.simonsays;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.RequestConfiguration;

public class AgeRestriction extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_restriction);

        toast = Toast.makeText(getApplicationContext(), R.string.age_restriction_back_button_toast, Toast.LENGTH_SHORT);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                toast.cancel();
                toast.show();
            }
        });

        Button btnYoungerThan5 = findViewById(R.id.btn_younger_than_5);
        Button btnBetween68 = findViewById(R.id.btn_between_6_8);
        Button btnBetween912 = findViewById(R.id.btn_between_9_12);
        Button btnBetween1315 = findViewById(R.id.btn_between_13_15);
        Button btnBetween1617 = findViewById(R.id.btn_between_16_17);
        Button btnOlderThan18 = findViewById(R.id.btn_older_than_18);

        btnYoungerThan5.setOnClickListener(_ -> setContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G, RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE));
        btnBetween68.setOnClickListener(_ -> setContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG, RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE));
        btnBetween912.setOnClickListener(_ -> setContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG, RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE));
        btnBetween1315.setOnClickListener(_ -> setContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG, RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE));
        btnBetween1617.setOnClickListener(_ -> setContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_T, RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE));
        btnOlderThan18.setOnClickListener(_ -> setContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_MA, RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE));
    }

    private void setContentRating(String contentRating, int underAgeOfConsent) {
        SharedPreferences sharedPreferences = getSharedPreferences("age_restriction", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("content_rating", contentRating);
        editor.putInt("under_age_of_consent", underAgeOfConsent);
        editor.apply();
        toast.cancel();
        finish();
    }
}