package com.kakeibo.data.disk;

import androidx.room.TypeConverter;
import com.kakeibo.util.UtilCurrency;
import java.math.BigDecimal;

public class Converters {
    @TypeConverter
    public long bigDecimalToLong(BigDecimal input) {
        return UtilCurrency.getLongFromBigDecimal(input.multiply(new BigDecimal(1000)));
    }

    @TypeConverter
    public BigDecimal longToBigDecimal(long input) {
        return BigDecimal.valueOf(input);
    }
}
