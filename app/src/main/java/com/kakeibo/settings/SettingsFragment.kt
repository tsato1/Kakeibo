package com.kakeibo.settings

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kakeibo.R
import com.kakeibo.db.KkbAppDBAdapter

class SettingsFragment : PreferenceFragmentCompat() {
    private var _activity: Activity? = null
    private var _context: FragmentActivity? = null
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        addPreferencesFromResource(R.xml.preferences)
        _activity = activity
        _context = activity
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val str = preference.key
        return if (str == getString(R.string.pref_key_date_format)) {
            true
        } else if (str == getString(R.string.pref_key_fraction_digits)) {
            true
        } else if (str == getString(R.string.pref_key_start_date_of_month)) {
            true
        } else if (str == getString(R.string.pref_key_category_add_remove_reorder)) {
//            if (!UtilAds.isBannerAdsDisplayAgreed()) {
//                handleAgreement();
//                return false;
//            }
            startActivity(Intent(_activity, CategoryPlacementActivity::class.java))
            true
        } else if (str == getString(R.string.pref_key_category_reorder)) {
//            if (!UtilAds.isBannerAdsDisplayAgreed()) {
//                handleAgreement();
//                return false;
//            }
            startActivity(Intent(_activity, CategoryReorderActivity::class.java))
            true
        } else if (str == getString(R.string.pref_key_category_creation)) {
//            if (!UtilAds.isBannerAdsDisplayAgreed()) {
//                handleAgreement();
//                return false;
//            }

//            if (UtilCategory.addNewCategory(_context, null) == -2) {
//                String s = getString(R.string.err_reached_max_count_colon) +
//                        UtilCategory.NUM_MAX_CUSTOM_CATEGORIES + "\n" +
//                        getString(R.string.msg_delete_some_categories); //todo 5 for ordinary version, 100 for paid, 1000 for b2b
//                Toast.makeText(_context, s, Toast.LENGTH_LONG).show();
//                return false;
//            }
            startActivity(Intent(_activity, CategoryCreationActivity::class.java))
            true
        } else if (str == getString(R.string.pref_key_category_edition)) {
//            if (!UtilAds.isBannerAdsDisplayAgreed()) {
//                handleAgreement();
//                return false;
//            }
            startActivity(Intent(_activity, CategoryEditionActivity::class.java))
            true
        } else if (str == getString(R.string.pref_key_delete_all_data)) {
            val dialog = AlertDialog.Builder(_activity!!)
            dialog.setIcon(R.mipmap.ic_mikan)
            dialog.setTitle(getString(R.string.delete_all_items))
            dialog.setMessage(getString(R.string.desc_delete_all_items))
            dialog.setPositiveButton(R.string.ok) { dp: DialogInterface?, w: Int ->
                val confirmation = AlertDialog.Builder(_activity!!)
                confirmation.setIcon(R.drawable.ic_warning_black_24dp)
                confirmation.setTitle(getString(R.string.delete_all_items))
                confirmation.setMessage(getString(R.string.warn_delete_all_items))
                confirmation.setPositiveButton(R.string.ok) { dp2: DialogInterface?, which: Int -> deleteAllItems() }
                confirmation.setNegativeButton(R.string.cancel) { dn2: DialogInterface?, which: Int -> }
                confirmation.show()
            }
            dialog.setNegativeButton(R.string.cancel) { dn: DialogInterface?, which: Int -> }
            dialog.show()
            true
        } else if (str == getString(R.string.pref_key_in_app_purchases)) {
            startActivity(Intent(_activity, InAppPurchasesActivity::class.java))
            true
        } else if (str == getString(R.string.pref_key_about)) {
            startActivity(Intent(_activity, SettingsAboutActivity::class.java))
            true
        } else {
            super.onPreferenceTreeClick(preference)
        }
    }

    private fun handleAgreement() {
        val dialog = AlertDialog.Builder(_context!!)
        dialog.setIcon(R.mipmap.ic_mikan)
        dialog.setTitle(R.string.category_management)
        dialog.setMessage(getString(R.string.quest_do_you_want_to_manage_categories))
        dialog.setPositiveButton(R.string.yes) { d: DialogInterface, which: Int ->
            d.dismiss()
            val dialog2 = AlertDialog.Builder(_context!!)
            dialog2.setIcon(R.drawable.ic_warning_black_24dp)
            dialog2.setTitle(R.string.warning)
            dialog2.setMessage(getString(R.string.quest_irreversible_operation_do_you_want_to_proceed))
            dialog2.setPositiveButton(R.string.yes) { d2: DialogInterface?, which2: Int ->
//                val kkbAppDBAdapter = KkbAppDBAdapter()
//                kkbAppDBAdapter.open()
//                if (kkbAppDBAdapter.setValueInt2(KkbAppDBAdapter.COL_VAL_INT_2_SHOWADS)) {
//                    Toast.makeText(_context, R.string.msg_access_to_category_management, Toast.LENGTH_LONG).show()
//                }
//                kkbAppDBAdapter.close()
            }
            dialog2.setNegativeButton(R.string.no, null)
            dialog2.create()
            dialog2.show()
        }
        dialog.setNegativeButton(R.string.no, null)
        dialog.create()
        dialog.show()
    }

    private fun deleteAllItems() {
//        ItemDBAdapter itemDbAdapter = new ItemDBAdapter();
//        itemDbAdapter.open();

//        if(itemDbAdapter.deleteAllItems()) {
//            Toast.makeText(getActivity(), getString(R.string.msg_all_delete_success), Toast.LENGTH_LONG).show();
//        }
//
//        itemDbAdapter.close();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            _activity!!.onBackPressed()
            Log.d(TAG, "Home button pressed")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val TAG = SettingsFragment::class.java.simpleName
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference: Preference, value: Any ->
            val stringValue = value.toString()
            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0) listPreference.entries[index] else null)
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }
    }
}