//package com.kakeibo.util;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.util.Log;
//
//import com.kakeibo.Item;
//import com.kakeibo.ui.Query;
//import com.kakeibo.db.ItemDBAdapter;
//
//import java.util.List;
//
//public class UtilItem {
//    private static final String TAG = UtilItem.class.getSimpleName();
//
//    private static List<Item> sItemInExList_Date;
//    private static List<Item> sItemInList_Date;
//    private static List<Item> sItemExList_Date;
//    private static List<Item> sItemInList_Category;
//    private static List<Item> sItemExList_Category;
//
//    public static void reloadItemLists_Category(Context context, Query query) {
//        setItemList_Category(context, query);
//    }
//
//    private static void setItemList_Category(Context context, Query query) {
//        Log.d(TAG, "setItemList_Category() called");
//
//        sItemInList_Category.clear();
//        sItemExList_Category.clear();
//
//        ItemDBAdapter itemDBAdapter = new ItemDBAdapter();
//        itemDBAdapter.open();
//        Cursor c = itemDBAdapter.getItemsByRawQuery(query.getQueryC());
//
//        itemDBAdapter.close();
//    }
//
//    public static List<Item> getItemInList_Category(Context context, Query query) {
//        if (sItemInList_Category!=null && !sItemInList_Category.isEmpty()) return sItemInList_Category;
//
//        setItemList_Category(context, query);
//
//        return sItemInList_Category;
//    }
//
//    public static List<Item> getItemExList_Category(Context context, Query query) {
//        if (sItemExList_Category!=null && !sItemExList_Category.isEmpty()) return sItemExList_Category;
//
//        setItemList_Category(context, query);
//
//        return sItemExList_Category;
//    }
//}
