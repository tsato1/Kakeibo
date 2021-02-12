package com.kakeibo.settings

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kakeibo.BuildConfig
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.ui.KkbAppViewModel
import com.kakeibo.ui.SubscriptionStatusViewModel

class SettingsCompatActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private var _adContainerView: FrameLayout? = null
    private var _adView: AdView? = null

    private lateinit var _kkbAppViewModel: KkbAppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frl_settings_container, SettingsFragment())
                .commit()

        _kkbAppViewModel = ViewModelProviders.of(this)[KkbAppViewModel::class.java]

        _kkbAppViewModel.all.observe( this, {
            /*** ads ***/
            if (it?.valInt2==0) { // val2 = -1:original, 0:agreed to show ads
                initAd()
                loadBanner()
            }
        })
    }

    override fun onPreferenceStartFragment(
            caller: PreferenceFragmentCompat,
            pref: Preference
    ): Boolean {

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
            _adView!!.adUnitId = "ca-app-pub-3940256099942544/6300978111"
            /*** in debug mode  */
        } else {
            _adView!!.adUnitId = getString(R.string.settings_banner_ad)
        }

        _adContainerView?.addView(_adView)
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
        _adView?.let{
            // Set the adaptive ad size to the ad view.
            it.adSize = adSize
            // Start loading the ad in the background.
            it.loadAd(AdRequest.Builder().build())
        }
    }

    companion object {
        val TAG = SettingsCompatActivity::class.java.simpleName
    }
}