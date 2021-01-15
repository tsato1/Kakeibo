package com.kakeibo.data.disk

import androidx.room.TypeConverter
import com.kakeibo.util.UtilCurrency
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun bigDecimalToLong(input: BigDecimal): Long {
        return UtilCurrency.getLongFromBigDecimal(input.multiply(BigDecimal(1000)))
    }

    @TypeConverter
    fun longToBigDecimal(input: Long): BigDecimal {
        return BigDecimal.valueOf(input)
    }
}