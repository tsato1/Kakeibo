package com.kakeibo.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.kakeibo.Constants
import com.kakeibo.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(
    @ApplicationContext val context: Context
) : AppPreferences {

    private var instance: SharedPreferences

    init {
        val nonEncryptedPreferences: SharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            instance = initializeEncryptedSharedPreferencesManager()
//            if (nonEncryptedPreferences.all.isNotEmpty()) {
//                // migrate non encrypted shared preferences
//                // to encrypted shared preferences and clear them once finished.
//                nonEncryptedPreferences.copyTo(instance)
//                nonEncryptedPreferences.clear()
//            }
//        } else {
            instance = nonEncryptedPreferences
//        }

        instance.set(context.resources.getString(R.string.pref_key_date_format), 0) // todo: check migration
        instance.set(context.resources.getString(R.string.pref_key_fraction_digits), 0)
        instance.set(context.resources.getString(R.string.pref_key_num_columns), 1)
    }
//
//    private fun initializeEncryptedSharedPreferencesManager(): SharedPreferences {
//        val masterKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()
////        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC) //?? which is better
//        return EncryptedSharedPreferences.create(
//            context,
//            Constants.ENCRYPTED_SHARED_PREF_NAME,
//            masterKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }

    /* dateFormat */
    override
    fun getDateFormatIndex(): Int {
        return getInt(
            context.resources.getString(R.string.pref_key_date_format), 0
        )
//        return getString(
//            context.resources.getString(R.string.pref_key_date_format), "0"
//        )?.toInt() ?: 0
    }
    override
    fun getDateFormat(): String {
        val dateFormats = context.resources.getStringArray(R.array.pref_list_date_format)
        return dateFormats[getDateFormatIndex()]
    }

    /* fraction digits */
    override
    fun getFractionDigitsIndex(): Int {
        val locale = Locale.getDefault()
        var defValue = 0
        try {
            val currency = Currency.getInstance(locale)
            defValue = currency.defaultFractionDigits
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return getInt(
            context.resources.getString(R.string.pref_key_fraction_digits), defValue
        )
//        val locale = Locale.getDefault()
//        var defValue = 0
//        try {
//            val currency = Currency.getInstance(locale)
//            defValue = currency.defaultFractionDigits
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//        }
//        return getString(
//            context.resources.getString(R.string.pref_key_fraction_digits), "$defValue"
//        )?.toInt() ?: defValue
    }
    override
    fun getFractionDigits(): Int {
        val fractionDigits = context.resources.getStringArray(R.array.pref_list_fraction_digits)
        return fractionDigits[getFractionDigitsIndex()].toInt()
    }

    /* num category icons per row */
    override
    fun getNumColumnsIndex(): Int {
        return getInt(
            context.resources.getString(R.string.pref_key_num_columns), 1
        )
//        return getString(
//            context.resources.getString(R.string.pref_key_num_columns), "1"
//        )?.toInt() ?: 1
    }
    override
    fun getNumColumns(): Int {
        val numColumns = context.resources.getStringArray(R.array.pref_list_num_columns)
        return numColumns[getNumColumnsIndex()].toInt()
    }




    override fun <T : Any?> set(key: Int, value: T) {
        instance.set(context.resources.getString(key), value)
    }

    override fun getString(key: String, defaultValue: String?): String? {
        val value = getValue(key, defaultValue)
        return value as String?
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val value = getValue(key, defaultValue)
        return value as Int
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = getValue(key, defaultValue)
        return value as Boolean
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val value = getValue(key, defaultValue)
        return value as Long
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val value = getValue(key, defaultValue)
        return value as Float
    }

    private fun getValue(key: String, defaultValue: Any?): Any? {
        var value = instance.all[key]
        value = value ?: defaultValue
        return value
    }

    override fun contains(key: String): Boolean {
        return instance.contains(key)
    }

    override fun remove(key: String) {
        instance.remove(key)
    }

    override fun clear() {
        instance.clear()
    }
}