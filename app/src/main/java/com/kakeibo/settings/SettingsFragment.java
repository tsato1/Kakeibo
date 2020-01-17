package com.kakeibo.settings;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.kakeibo.R;
import com.kakeibo.db.ItemDBAdapter;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private Activity _activity;
    private FragmentActivity _context;

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
        _context = getActivity();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String str = preference.getKey();

        if (str.equals(getString(R.string.pref_key_date_format))) {
            return true;
        } else if (str.equals(getString(R.string.pref_key_fraction_digits))) {
            return true;
        } else if (str.equals(getString(R.string.pref_key_start_date_of_month))) {
            return true;
        } else if (str.equals(getString(R.string.pref_key_category_add_remove_reorder))) {
            startActivity(new Intent(_activity, CategoryPlacementActivity.class));
            return true;
        } else if (str.equals(getString(R.string.pref_key_category_reorder))) {
            startActivity(new Intent(_activity, CategoryReorderActivity.class));
            return true;
        } else if (str.equals(getString(R.string.pref_key_category_creation))) {
            startActivity(new Intent(_activity, CategoryCreationActivity.class));
            return true;
        } else if (str.equals(getString(R.string.pref_key_delete_all_data))) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(getString(R.string.delete_all_items));
            dialog.setMessage(getString(R.string.desc_delete_all_items));
            dialog.setPositiveButton(R.string.ok, (DialogInterface dp, int w) -> {
                AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                confirmation.setIcon(R.drawable.ic_warning_black_24dp);
                confirmation.setTitle(getString(R.string.delete_all_items));
                confirmation.setMessage(getString(R.string.warn_delete_all_items));
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
        } else if (str.equals(getString(R.string.pref_key_about))) {
            startActivity(new Intent(_activity, SettingsAboutActivity.class));
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private void deleteAllItems() {
        ItemDBAdapter itemDbAdapter = new ItemDBAdapter();
        itemDbAdapter.open();

        if(itemDbAdapter.deleteAllItems()) {
            Toast.makeText(getActivity(), getString(R.string.msg_all_delete_success), Toast.LENGTH_SHORT).show();
        }

        itemDbAdapter.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            _activity.onBackPressed();
            Log.d(TAG, "Home button pressed");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
