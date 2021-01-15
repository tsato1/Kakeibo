//package com.kakeibo.data;
//
//
//import androidx.annotation.Nullable;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//
//import java.util.Map;
//
//public class ContentResource {
//    private static final String URL_KEY = "url";
//
//    public final String url;
//
//    public ContentResource(String url) {
//        this.url = url;
//    }
//
//    @Nullable
//    public static ContentResource listFromMap(Map<String, Object> map) {
//        Object url = map.get(URL_KEY);
//        if (url instanceof String) {
//            return new ContentResource((String) url);
//        } else {
//            return null;
//        }
//    }
//
//    @Nullable
//    public static ContentResource fromJsonString(String dataString) {
//        Gson gson = new Gson();
//        try {
//            return gson.fromJson(dataString, ContentResource.class);
//        } catch (JsonSyntaxException e) {
//            return null;
//        }
//    }
//}
//
