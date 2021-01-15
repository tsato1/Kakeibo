//package com.kakeibo.ui;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Query implements Parcelable {
//    public static final int QUERY_TYPE_NEW = 0;
//    public static final int QUERY_TYPE_SEARCH = 1;
//
//    private static int type;
//    private static Map<Integer, String> queryCs = new HashMap<>();
//    private static String queryC;
//    private static String queryD;
//
//    public Query (int arg) { type = arg; }
//
//    public int getType() { return type; }
//
//    public Map<Integer, String> getQueryCs() { return queryCs; }
//
//    public String getQueryC() { return queryC; }
//
//    public String getQueryD() { return queryD; }
//
//    public void setQueryCs(Map<Integer, String> queries) { queryCs = queries; }
//
//    public void setQueryC(String query) { queryC = query; }
//
//    public void setQueryD(String query) { queryD = query; }
//
//    /******** parcel ********/
//    private Query (Parcel in) {
//        type = in.readInt();
//        in.readMap(queryCs, String.class.getClassLoader());
//        queryC = in.readString();
//        queryD = in.readString();
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(type);
//        dest.writeMap(queryCs);
//        dest.writeString(queryC);
//        dest.writeString(queryD);
//    }
//
//    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//        public Query createFromParcel(Parcel in) {
//            return new Query(in);
//        }
//
//        public Query[] newArray(int size) {
//            return new Query[size];
//        }
//    };
//}
