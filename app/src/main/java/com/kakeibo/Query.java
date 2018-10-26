package com.kakeibo;

import android.os.Parcel;
import android.os.Parcelable;

public class Query implements Parcelable {
    static final int QUERY_TYPE_NEW = 0;
    static final int QUERY_TYPE_SEARCH = 1;

    private int type;
    private String[] queryCs;
    private String queryC;
    private String queryD;

    Query (int type) {
        this.type = type;
    }

    int getType() {
        return this.type;
    }

    String[] getQueryCs() {
        return queryCs;
    }

    String getQueryC() {
        return queryC;
    }

    String getQueryD() {
        return queryD;
    }

    void setQueryCs(String[] queries) {
        queryCs = queries;
    }

    void setQueryC(String query) {
        queryC = query;
    }

    void setQueryD(String query) {
        queryD = query;
    }

    /******** parcel ********/
    private Query (Parcel in) {
        this.type = in.readInt();
        this.queryCs = in.createStringArray();
        this.queryC = in.readString();
        this.queryD = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeStringArray(this.queryCs);
        dest.writeString(this.queryC);
        dest.writeString(this.queryD);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Query createFromParcel(Parcel in) {
            return new Query(in);
        }

        public Query[] newArray(int size) {
            return new Query[size];
        }
    };
}
