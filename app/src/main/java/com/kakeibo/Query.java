package com.kakeibo;

public class Query {
    String id;
    int type;
    String query;
    String date;

    public Query(String id, int type, String query, String date)
    {
        this.id = id;
        this.type = type;
        this.query = query;
        this.date = date;
    }

    public String getId()
    {
        return this.id;
    }

    public int getType()
    {
        return this.type;
    }

    public String getQuery() {
        return this.query;
    }

    public String getDate() {
        return this.date;
    }
}
