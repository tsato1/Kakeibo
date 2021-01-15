//package com.kakeibo.settings;
//
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.FragmentActivity;
//import androidx.preference.ListPreference;
//import androidx.preference.Preference;
//import androidx.preference.PreferenceFragmentCompat;
//
//import com.kakeibo.R;
//import com.kakeibo.db.ItemDBAdapter;
//import com.kakeibo.db.KkbAppDBAdapter;
//import com.kakeibo.util.UtilAds;
//import com.kakeibo.util.UtilCategory;
//
//public class SettingsFragment extends PreferenceFragmentCompat {
//    public static final String TAG = SettingsFragment.class.getSimpleName();
//
//    private Activity _activity;
//    private FragmentActivity _context;
//
//    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
//            = (Preference preference, Object value) -> {
//        String stringValue = value.toString();
//
//        if (preference instanceof ListPreference) {
//            // For list preferences, look up the correct display value in
//            // the preference's 'entries' list.
//            ListPreference listPreference = (ListPreference) preference;
//            int index = listPreference.findIndexOfValue(stringValue);
//
//            // Set the summary to reflect the new value.
//            preference.setSummary(
//                    index >= 0
//                            ? listPreference.getEntries()[index]
//                            : null);
//
//        } else {
//            // For all other preferences, set the summary to the value's
//            // simple string representation.
//            preference.setSummary(stringValue);
//        }
//        return true;
//    };
//
//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        addPreferencesFromResource(R.xml.preferences);
//        _activity = getActivity();
//        _context = getActivity();
//    }
//
//    @Override
//    public boolean onPreferenceTreeClick(Preference preference) {
//        String str = preference.getKey();
//
//        if (str.equals(getString(R.string.pref_key_date_format))) {
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_fraction_digits))) {
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_start_date_of_month))) {
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_category_add_remove_reorder))) {
////            if (!UtilAds.isBannerAdsDisplayAgreed()) {
////                handleAgreement();
////                return false;
////            }
//            startActivity(new Intent(_activity, CategoryPlacementActivity.class));
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_category_reorder))) {
////            if (!UtilAds.isBannerAdsDisplayAgreed()) {
////                handleAgreement();
////                return false;
////            }
//            startActivity(new Intent(_activity, CategoryReorderActivity.class));
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_category_creation))) {
////            if (!UtilAds.isBannerAdsDisplayAgreed()) {
////                handleAgreement();
////                return false;
////            }
//
////            if (UtilCategory.addNewCategory(_context, null) == -2) {
////                String s = getString(R.string.err_reached_max_count_colon) +
////                        UtilCategory.NUM_MAX_CUSTOM_CATEGORIES + "\n" +
////                        getString(R.string.msg_delete_some_categories); //todo 5 for ordinary version, 100 for paid, 1000 for b2b
////                Toast.makeText(_context, s, Toast.LENGTH_LONG).show();
////                return false;
////            }
//            startActivity(new Intent(_activity, CategoryCreationActivity.class));
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_category_edition))) {
////            if (!UtilAds.isBannerAdsDisplayAgreed()) {
////                handleAgreement();
////                return false;
////            }
//            startActivity(new Intent(_activity, CategoryEditionActivity.class));
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_delete_all_data))) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
//            dialog.setIcon(R.mipmap.ic_mikan);
//            dialog.setTitle(getString(R.string.delete_all_items));
//            dialog.setMessage(getString(R.string.desc_delete_all_items));
//            dialog.setPositiveButton(R.string.ok, (DialogInterface dp, int w) -> {
//                AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
//                confirmation.setIcon(R.drawable.ic_warning_black_24dp);
//                confirmation.setTitle(getString(R.string.delete_all_items));
//                confirmation.setMessage(getString(R.string.warn_delete_all_items));
//                confirmation.setPositiveButton(R.string.ok, (DialogInterface dp2, int which) -> {
//                    deleteAllItems();
//                });
//                confirmation.setNegativeButton(R.string.cancel, (DialogInterface dn2, int which) -> {
//                });
//                confirmation.show();
//            });
//            dialog.setNegativeButton(R.string.cancel, (DialogInterface dn, int which) -> {
//            });
//            dialog.show();
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_in_app_purchases))) {
//            startActivity(new Intent(_activity, InAppPurchasesActivity.class));
//            return true;
//        } else if (str.equals(getString(R.string.pref_key_about))) {
//            startActivity(new Intent(_activity, SettingsAboutActivity.class));
//            return true;
//        } else {
//            return super.onPreferenceTreeClick(preference);
//        }
//    }
//
//    private void handleAgreement() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
//        dialog.setIcon(R.mipmap.ic_mikan);
//        dialog.setTitle(R.string.category_management);
//        dialog.setMessage(getString(R.string.quest_do_you_want_to_manage_categories));
//        dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
//            d.dismiss();
//            AlertDialog.Builder dialog2 = new AlertDialog.Builder(_context);
//            dialog2.setIcon(R.drawable.ic_warning_black_24dp);
//            dialog2.setTitle(R.string.warning);
//            dialog2.setMessage(getString(R.string.quest_irreversible_operation_do_you_want_to_proceed));
//            dialog2.setPositiveButton(R.string.yes, (DialogInterface d2, int which2) -> {
//                KkbAppDBAdapter kkbAppDBAdapter = new KkbAppDBAdapter();
//                kkbAppDBAdapter.open();
//                if (kkbAppDBAdapter.setValueInt2(KkbAppDBAdapter.COL_VAL_INT_2_SHOWADS)) {
//                    Toast.makeText(_context, R.string.msg_access_to_category_management, Toast.LENGTH_LONG).show();
//                }
//                kkbAppDBAdapter.close();
//            });
//            dialog2.setNegativeButton(R.string.no, null);
//            dialog2.create();
//            dialog2.show();
//        });
//        dialog.setNegativeButton(R.string.no, null);
//        dialog.create();
//        dialog.show();
//    }
//
//    private void deleteAllItems() {
////        ItemDBAdapter itemDbAdapter = new ItemDBAdapter();
////        itemDbAdapter.open();
//
////        if(itemDbAdapter.deleteAllItems()) {
////            Toast.makeText(getActivity(), getString(R.string.msg_all_delete_success), Toast.LENGTH_LONG).show();
////        }
////
////        itemDbAdapter.close();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            _activity.onBackPressed();
//            Log.d(TAG, "Home button pressed");
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//}
