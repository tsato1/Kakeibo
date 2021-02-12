package com.kakeibo.data.disk

import androidx.room.TypeConverter
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.util.UtilCurrency
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun bigDecimalToLong(input: BigDecimal): Long {
        return input.multiply(BigDecimal(1000)).toLong()
//        return UtilCurrency.getLongFromBigDecimal(input.multiply(BigDecimal(1000)))
    }

    @TypeConverter
    fun longToBigDecimal(input: Long): BigDecimal {
        return BigDecimal.valueOf(input)
                .divide(BigDecimal.valueOf(1000),
                        SubApp.getFractionDigits(R.string.pref_key_fraction_digits),
                        BigDecimal.ROUND_HALF_UP)
//        return BigDecimal.valueOf(input)
    }
}