package com.example.simonsays.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.simonsays.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.UserMessagingPlatform;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdHelper {

    private static final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    private final Context context;
    private final RelativeLayout adContainerView;

    private AdView adView;

    public AdHelper(Context context, RelativeLayout adContainerView) {
        this.context = context;
        this.adContainerView = adContainerView;

        SharedPreferences sharedPreferences = context.getSharedPreferences("age_restriction", Context.MODE_PRIVATE);
        String contentRating = sharedPreferences.getString("content_rating", RequestConfiguration.MAX_AD_CONTENT_RATING_G);
        int underAgeOfConsent = sharedPreferences.getInt("under_age_of_consent", RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE);

        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(context);
        if (consentInformation.canRequestAds()) {
            loadBanner(contentRating, underAgeOfConsent);
            adListener();
        }
    }

    private void loadBanner(String contentRating, int underAgeOfConsent) {
        adView = new AdView(context);
        adView.setAdSize(getAdSize());
        adView.setAdUnitId(context.getString(R.string.ad_unit_id));

        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        initializeMobileAdsSdk(contentRating, underAgeOfConsent);
    }

    private AdSize getAdSize() {
        Display display = getDefaultDisplay(context);
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);

        float density = outMetrics.density;
        float adWidthPixels = adContainerView.getWidth();
        if (adWidthPixels == 0) adWidthPixels = outMetrics.widthPixels;

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public static Display getDefaultDisplay(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        return displayManager.getDisplay(Display.DEFAULT_DISPLAY);
    }

    private void adListener() {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                adContainerView.setVisibility(View.GONE);
            }
        });
    }

    private void initializeMobileAdsSdk(String contentRating, int underAgeOfConsent) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) return;

        MobileAds.initialize(context, initializationStatus -> {});

        RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTagForUnderAgeOfConsent(underAgeOfConsent)
                .setMaxAdContentRating(contentRating)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
