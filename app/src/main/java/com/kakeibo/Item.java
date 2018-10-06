package com.kakeibo;

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
    private int categoryCode;
    private String memo;
    private String eventDate;
    private String updateDate;

    public Item(String id, BigDecimal amount, String currencyCode, int categoryCode, String memo, String eventDate, String updateDate)
    {
        this.id = id;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.categoryCode = categoryCode;
        this.memo = memo;
        this.eventDate = eventDate;
        this.updateDate = updateDate;
    }

    public Item(String id, int amount, String currencyCode, int categoryCode, String memo, String eventDate, String updateDate)
    {
        this.id = id;
        Currency currency = Currency.getInstance(currencyCode);
        this.amount = Util.getBDAmount(amount, currency.getDefaultFractionDigits());
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

    public int getIntAmount() {
        Currency currency = Currency.getInstance(currencyCode);
        return Util.getIntAmount(amount, currency.getDefaultFractionDigits());
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
        Currency currency = Currency.getInstance(currencyCode);
        return currency.getDefaultFractionDigits();
    }
}
