package com.kakeibo.settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.kakeibo.R;

public class SettingsCompatActivity extends AppCompatActivity
        implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    public static final String TAG = SettingsCompatActivity.class.getSimpleName();

    private Activity _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        _activity = this;
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        Log.d(TAG, "onPreferenceStartFragment: " + pref.getFragment());

        if (pref.getKey() == null) {
            return false;
        }

        int id = _activity.getResources().getIdentifier(pref.getKey(), "xml", getPackageName());
        caller.addPreferencesFromResource(id);

        String fragmentName = pref.getFragment();

        Log.d(TAG, "fragmentName=" + fragmentName + ", id=" +id);
        return true;
    }
}
