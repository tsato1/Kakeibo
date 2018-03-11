package com.kakeibo;

import java.util.Calendar;

/**
 * Created by T on 2015/09/15.
 */
public class Item
{
    String id;
    String amount;
    int categoryCode;
    String memo;
    String eventD;
    String eventYM;
    String updateDate;

    public Item(String id, String amount, int categoryCode, String memo, String eventD, String eventYM, String updateDate)
    {
        this.id = id;
        this.amount = amount;
        this.categoryCode = categoryCode;
        this.memo = memo;
        this.eventD = eventD;
        this.eventYM = eventYM;
        this.updateDate = updateDate;
    }

    public String getId()
    {
        return this.id;
    }

    public String getAmount()
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

    public String getEventD()
    {
        return this.eventD;
    }

    public String getEventYM()
    {
        return this.eventYM;
    }

    public String getUpdateDate()
    {
        return this.updateDate;
    }
}
