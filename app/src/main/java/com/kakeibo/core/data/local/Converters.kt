package com.kakeibo.core.data.local

import android.content.Context
import androidx.room.TypeConverter
import com.kakeibo.core.AppPreferencesImpl
import com.kakeibo.KakeiboApp
import com.kakeibo.R
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.math.BigDecimal

class Converters {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface SharedPrefsProviderEntryPoint {
        fun getPrefs(): AppPreferencesImpl
    }

    private fun getPrefs(appContext: Context): AppPreferencesImpl {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            SharedPrefsProviderEntryPoint::class.java
        )
        return hiltEntryPoint.getPrefs()
    }

    @TypeConverter
    fun bigDecimalToLong(input: BigDecimal): Long {
        return input.multiply(BigDecimal(1000)).toLong()
    }

    @TypeConverter
    fun longToBigDecimal(input: Long): BigDecimal {
        return BigDecimal.valueOf(input)
            .divide(
                BigDecimal.valueOf(1000),
                getPrefs(KakeiboApp.context).getFractionDigits(),
                BigDecimal.ROUND_HALF_UP
            )
    }

}