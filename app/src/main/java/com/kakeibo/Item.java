package com.kakeibo;

import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by T on 2015/09/15.
 */
public class Item
{
    private String id;
    private BigDecimal amount;
    private String currencyCode;
    private int currencyFractionDigits;
    private int categoryCode;
    private String memo;
    private String eventDate;
    private String updateDate;

    Item(String id, BigDecimal amount, String currencyCode, int categoryCode, String memo, String eventDate, String updateDate) {
        this.id = id;
        this.amount = amount;

        if (UtilCurrency.CURRENCY_NONE.equals(currencyCode)) {
            this.currencyFractionDigits = 0;
        } else {
            Currency currency = Currency.getInstance(currencyCode);
            this.currencyFractionDigits = currency.getDefaultFractionDigits();
        }

        this.currencyCode = currencyCode;
        this.categoryCode = categoryCode;
        this.memo = memo;
        this.eventDate = eventDate;
        this.updateDate = updateDate;
    }

    Item(String id, int amount, String currencyCode, int categoryCode, String memo, String eventDate, String updateDate) {
        this.id = id;

        if (UtilCurrency.CURRENCY_NONE.equals(currencyCode)) {
            this.amount = BigDecimal.valueOf(amount, 0);
            this.currencyFractionDigits = 0;
        } else {
            Currency currency = Currency.getInstance(currencyCode);
            this.amount = BigDecimal.valueOf(amount, currency.getDefaultFractionDigits());
            this.currencyFractionDigits = currency.getDefaultFractionDigits();
        }

        this.currencyCode = currencyCode;
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

    public String getCurrencyCode() {
        return this.currencyCode;
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
        return this.currencyFractionDigits;
    }
}
