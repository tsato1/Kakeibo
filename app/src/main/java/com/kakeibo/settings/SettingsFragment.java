package com.kakeibo.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.kakeibo.R;
import com.kakeibo.db.ItemsDBAdapter;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private Activity _activity;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = (Preference preference, Object value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        _activity = getActivity();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String str = preference.getKey();

        if (str.equals(getString(R.string.pref_key_date_format))) {
            return true;
        } else if (str.equals(getString(R.string.pref_key_fraction_digits))) {
            return true;
        } else if (str.equals(getString(R.string.pref_key_delete_all_data))) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(getString(R.string.pref_title_delete_all_items));
            dialog.setMessage(getString(R.string.pref_desc_delete_all_items));
            dialog.setPositiveButton(R.string.ok, (DialogInterface dp, int w) -> {
                AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                confirmation.setIcon(R.drawable.ic_warning_black_24dp);
                confirmation.setTitle(getString(R.string.pref_title_delete_all_items));
                confirmation.setMessage(getString(R.string.pref_warn_delete_all_items));
                confirmation.setPositiveButton(R.string.ok, (DialogInterface dp2, int which) -> {
                    deleteAllItems();
                });
                confirmation.setNegativeButton(R.string.cancel, (DialogInterface dn2, int which) -> {
                });
                confirmation.show();
            });
            dialog.setNegativeButton(R.string.cancel, (DialogInterface dn, int which)-> {
            });
            dialog.show();
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private void deleteAllItems() {
        ItemsDBAdapter itemsDbAdapter = new ItemsDBAdapter();
        itemsDbAdapter.open();

        if(itemsDbAdapter.deleteAllItems()) {
            Toast.makeText(getActivity(), getString(R.string.pref_msg_all_delete_success), Toast.LENGTH_SHORT).show();
        }

        itemsDbAdapter.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                _activity.onBackPressed();
                Log.d(TAG, "Home button pressed");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
