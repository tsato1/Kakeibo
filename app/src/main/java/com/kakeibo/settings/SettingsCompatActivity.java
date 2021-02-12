//package com.kakeibo.settings;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Display;
//import android.widget.FrameLayout;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.preference.Preference;
//import androidx.preference.PreferenceFragmentCompat;
//
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.initialization.InitializationStatus;
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
//import com.kakeibo.BuildConfig;
//import com.kakeibo.R;
//import com.kakeibo.util.UtilAds;
//
//public class SettingsCompatActivity extends AppCompatActivity
//        implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
//
//    public static final String TAG = SettingsCompatActivity.class.getSimpleName();
//
//    private Activity _activity;
//
//    private FrameLayout _adContainerView;
//    private AdView _adView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.frl_settings_container, new SettingsFragment())
//                .commit();
//
//        /*** ads ***/
////        if (UtilAds.isBannerAdsDisplayAgreed()) {
////            initAd();
////            loadBanner();
////        }
//
//        _activity = this;
//    }
//
//    @Override
//    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
//        Log.d(TAG, "onPreferenceStartFragment: " + pref.getFragment());
//
//        if (pref.getKey() == null) {
//            return false;
//        }
//
//        int id = _activity.getResources().getIdentifier(pref.getKey(), "xml", getPackageName());
//        caller.addPreferencesFromResource(id);
//
//        String fragmentName = pref.getFragment();
//
//        Log.d(TAG, "fragmentName=" + fragmentName + ", id=" +id);
//        return true;
//    }
//
//    /*** ads ***/
//    private void initAd() {
//        //Call the function to initialize AdMob SDK
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        //get the reference to your FrameLayout
//        _adContainerView = findViewById(R.id.ad_container);
//
//        //Create an AdView and put it into your FrameLayout
//        _adView = new AdView(this);
//        if (BuildConfig.DEBUG) {
//            _adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");/*** in debug mode ***/
//        } else {
//            _adView.setAdUnitId(getString(R.string.settings_banner_ad));
//        }
//        _adContainerView.addView(_adView);
//    }
//
//    private AdSize getAdSize() {
//        //Determine the screen width to use for the ad width.
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        display.getMetrics(outMetrics);
//
//        float widthPixels = outMetrics.widthPixels;
//        float density = outMetrics.density;
//
//        //you can also pass your selected width here in dp
//        int adWidth = (int) (widthPixels / density);
//
//        //return the optimal size depends on your orientation (landscape or portrait)
//        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
//    }
//
//    private void loadBanner() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
//
//        AdSize adSize = getAdSize();
//        // Set the adaptive ad size to the ad view.
//        _adView.setAdSize(adSize);
//
//        // Start loading the ad in the background.
//        _adView.loadAd(adRequest);
//    }
//    /*** ads end ***/
//}
