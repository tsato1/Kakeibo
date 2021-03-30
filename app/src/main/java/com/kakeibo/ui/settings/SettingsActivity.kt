package com.kakeibo.ui.settings

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kakeibo.BuildConfig
import com.kakeibo.R
import com.kakeibo.databinding.ActivitySettingsBinding
import com.kakeibo.ui.viewmodel.KkbAppViewModel
import com.kakeibo.ui.viewmodel.SubscriptionStatusViewModel

class SettingsActivity
    : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    companion object {
        val TAG = SettingsActivity::class.java.simpleName
    }

    private lateinit var _adContainerView: FrameLayout
    private lateinit var _adView: AdView

    private val _kkbAppViewModel: KkbAppViewModel by viewModels()
    private val _subscriptionStatusViewModel: SubscriptionStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        binding.kkbAppViewModel = _kkbAppViewModel
        binding.subscriptionViewModel = _subscriptionStatusViewModel
        setContentView(binding.root)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frl_settings_container, SettingsFragment())
                .commit()

//        _kkbAppViewModel.all.observe( this, {
//            /*** ads ***/
//            if (it?.valInt2==0) { // val2 = -1:original, 0:agreed to show ads
//                Log.d("asdf","coooming")
//                initAd()
//                loadBanner()
//            }
//        })

        /*** ads  */
        _kkbAppViewModel.all.observe(this, {
            val showAds = it?.valInt2 == 0 // val2 = -1:original, 0:agreed to show ads

            if (showAds) {
                Log.d("asdf","oioioife")
                MobileAds.initialize(this) {}
                val adView: AdView = findViewById(R.id.ad_container)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                adView.visibility = View.VISIBLE
            }
        })
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference)
    : Boolean {

        Log.d(TAG, "onPreferenceStartFragment: " + pref.fragment)

        if (pref.key == null) {
            return false
        }

        val id = resources.getIdentifier(pref.key, "xml", packageName)
        caller.addPreferencesFromResource(id)
        val fragmentName = pref.fragment

        Log.d(TAG, "fragmentName=$fragmentName, id=$id")

        return true
    }

    /*** ads  */
    private fun initAd() {
        //Call the function to initialize AdMob SDK
        MobileAds.initialize(this) { }

        //get the reference to your FrameLayout
        _adContainerView = findViewById(R.id.ad_container)

        //Create an AdView and put it into your FrameLayout
        _adView = AdView(this)
        if (BuildConfig.DEBUG) {
            _adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
            /*** in debug mode  */
        } else {
            _adView.adUnitId = getString(R.string.settings_banner_ad)
        }

        _adContainerView.addView(_adView)
        _adContainerView.visibility = View.VISIBLE
    }

    //Determine the screen width to use for the ad width.
    private val adSize: AdSize
        get() {
            //Determine the screen width to use for the ad width.
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density

            //you can also pass your selected width here in dp
            val adWidth = (widthPixels / density).toInt()

            //return the optimal size depends on your orientation (landscape or portrait)
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun loadBanner() {
        _adView.adSize = adSize
        _adView.loadAd(AdRequest.Builder().build())
    }
}