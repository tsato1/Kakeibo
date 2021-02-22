package com.kakeibo.settings

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kakeibo.R
import com.kakeibo.ui.viewmodel.ItemStatusViewModel
import com.kakeibo.ui.viewmodel.KkbAppViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private var showAds: Boolean = false

    private lateinit var _itemStatusViewModel: ItemStatusViewModel
    private lateinit var _kkbAppViewModel: KkbAppViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        _itemStatusViewModel = ViewModelProviders.of(this)[ItemStatusViewModel::class.java]
        _kkbAppViewModel = ViewModelProviders.of(this)[KkbAppViewModel::class.java]

        _kkbAppViewModel.all.observe( this, {
            showAds = it?.valInt2==0 // val2 = -1:original, 0:agreed to show ads
        })
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            getString(R.string.pref_key_date_format) -> {
                true
            }
            getString(R.string.pref_key_fraction_digits) -> {
                true
            }
            getString(R.string.pref_key_start_date_of_month) -> {
                true
            }
            getString(R.string.pref_key_category_add_remove_reorder) -> {
                if (!showAds) {
                    handleAgreement()
                    return false
                }
                startActivity(Intent(activity, CategoryPlacementActivity::class.java))
                true
            }
            getString(R.string.pref_key_category_reorder) -> {
                if (!showAds) {
                    handleAgreement()
                    return false
                }
                startActivity(Intent(activity, CategoryReorderActivity::class.java))
                true
            }
            getString(R.string.pref_key_category_creation) -> {
                if (!showAds) {
                    handleAgreement()
                    return false
                }

//            if (UtilCategory.addNewCategory(_context, null) == -2) {
//                String s = getString(R.string.err_reached_max_count_colon) +
//                        UtilCategory.NUM_MAX_CUSTOM_CATEGORIES + "\n" +
//                        getString(R.string.msg_delete_some_categories); //todo 5 for ordinary version, 100 for paid, 1000 for b2b
//                Toast.makeText(_context, s, Toast.LENGTH_LONG).show();
//                return false;
//            }
                startActivity(Intent(activity, CategoryCreationActivity::class.java))
                true
            }
            getString(R.string.pref_key_category_edition) -> {
                if (!showAds) {
                    handleAgreement()
                    return false
                }
                startActivity(Intent(activity, CategoryEditionActivity::class.java))
                true
            }
            getString(R.string.pref_key_delete_all_data) -> {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setIcon(R.mipmap.ic_mikan)
                dialog.setTitle(getString(R.string.delete_all_items))
                dialog.setMessage(getString(R.string.desc_delete_all_items))
                dialog.setPositiveButton(R.string.ok) { dp: DialogInterface?, w: Int ->
                    val confirmation = AlertDialog.Builder(requireContext())
                    confirmation.setIcon(R.drawable.ic_warning_black_24dp)
                    confirmation.setTitle(getString(R.string.delete_all_items))
                    confirmation.setMessage(getString(R.string.warn_delete_all_items))
                    confirmation.setPositiveButton(R.string.ok) { dp2: DialogInterface?, _: Int ->
                        _itemStatusViewModel.deleteAll()
                    }
                    confirmation.setNegativeButton(R.string.cancel) { dn2: DialogInterface?, _: Int -> }
                    confirmation.show()
                }
                dialog.setNegativeButton(R.string.cancel) { dn: DialogInterface?, which: Int -> }
                dialog.show()
                true
            }
            getString(R.string.pref_key_in_app_purchases) -> {
                startActivity(Intent(activity, InAppPurchasesActivity::class.java))
                true
            }
            getString(R.string.pref_key_about) -> {
                startActivity(Intent(activity, SettingsAboutActivity::class.java))
                true
            }
            else -> {
                super.onPreferenceTreeClick(preference)
            }
        }
    }

    private fun handleAgreement() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setIcon(R.mipmap.ic_mikan)
        dialog.setTitle(R.string.category_management)
        dialog.setMessage(getString(R.string.quest_do_you_want_to_manage_categories))
        dialog.setPositiveButton(R.string.yes) { d: DialogInterface, which: Int ->
            d.dismiss()
            val dialog2 = AlertDialog.Builder(requireContext())
            dialog2.setIcon(R.drawable.ic_warning_black_24dp)
            dialog2.setTitle(R.string.warning)
            dialog2.setMessage(getString(R.string.quest_irreversible_operation_do_you_want_to_proceed))
            dialog2.setPositiveButton(R.string.yes) { d2: DialogInterface?, which2: Int ->
                _kkbAppViewModel.updateVal2(0)
            }
            dialog2.setNegativeButton(R.string.no, null)
            dialog2.create()
            dialog2.show()
        }
        dialog.setNegativeButton(R.string.no, null)
        dialog.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            Log.d(TAG, "Home button pressed")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val TAG = SettingsFragment::class.java.simpleName
//disposable!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference: Preference, value: Any ->
//            val stringValue = value.toString()
//
//            if (preference is ListPreference) {
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                val index = preference.findIndexOfValue(stringValue)
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(
//                        if (index >= 0) preference.entries[index] else null)
//            } else {
//                // For all other preferences, set the summary to the value's
//                // simple string representation.
//                preference.summary = stringValue
//            }
//            true
//        }
    }
}