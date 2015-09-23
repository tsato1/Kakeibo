package com.kakeibo;

import java.util.Calendar;

/**
 * Created by T on 2015/09/15.
 */
public class Item
{
    String amount;
    String category;
    String memo;
    String eventD;
    String eventYM;
    String updateDate;

    public Item(String amount, String category, String memo, String eventD, String eventYM, String updateDate)
    {
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.eventD = eventD;
        this.eventYM = eventYM;
        this.updateDate = updateDate;
    }

    public String getAmount()
    {
        return this.amount;
    }

    public String getCategory()
    {
        return this.category;
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
