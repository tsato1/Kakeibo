package com.kakeibo;

import com.kakeibo.util.UtilCurrency;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by T on 2015/09/15.
 */
public class Item
{
    private String id;
    private BigDecimal amount;
    private int fractionDigits;
    private int categoryCode;
    private String memo;
    private String eventDate;
    private String updateDate;

    /*** called from TabFragment1 before getting saved ***/
    Item (String id, BigDecimal amount, int fractionDigits, int categoryCode, String memo, String eventDate, String updateDate) {
        this.id = id;
        this.amount = amount;
        this.fractionDigits = fractionDigits;
        this.categoryCode = categoryCode;
        this.memo = memo;
        this.eventDate = eventDate;
        this.updateDate = updateDate;
    }

    /*** called from TabFragment2 before getting displayed ***/
    Item (String id, long amount, String currencyCode, int fractionDigits, int categoryCode,
          String memo, String eventDate, String updateDate) {
        this.id = id;

        if (UtilCurrency.CURRENCY_OLD.equals(currencyCode)) {
            this.amount = BigDecimal.valueOf(amount, 0);
        }

        this.amount = BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(1000), fractionDigits, BigDecimal.ROUND_HALF_UP);
        this.fractionDigits = fractionDigits;
        this.categoryCode = categoryCode;
        this.memo = memo;
        this.eventDate = eventDate;
        this.updateDate = updateDate;
    }

    public String getId()
    {
        return this.id;
    }

    public BigDecimal getAmount()
    {
        return this.amount;
    }

    public int getCategoryCode()
    {
        return this.categoryCode;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo()
    {
        return this.memo;
    }

    public String getEventDate()
    {
        return this.eventDate;
    }

    public String getUpdateDate()
    {
        return this.updateDate;
    }

    public int getFractionDigits() {
        return this.fractionDigits;
    }
}
