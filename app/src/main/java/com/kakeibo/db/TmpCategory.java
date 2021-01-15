//package com.kakeibo.db;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//public class TmpCategory {
//    public int id;
//    public int code;
//    public int color;
//    public int drawable;
//    public int location;
//    public byte[] image;
//    public String ara;
//    public String eng;
//    public String spa;
//    public String fra;
//    public String hin;
//    public String ind;
//    public String ita;
//    public String jpn;
//    public String kor;
//    public String pol;
//    public String por;
//    public String rus;
//    public String tur;
//    public String vie;
//    public String hans;
//    public String hant;
//
//    public TmpCategory() {}
//
//    public TmpCategory (int id, int code, int color, int drawable, int location, byte[] image,
//                       String ara, String eng, String spa, String fra, String hin, String ind,
//                       String ita, String jpn, String kor, String pol, String por, String rus,
//                       String tur, String vie, String hans, String hant) {
//        this.id = id;
//        this.code = code;
//        this.color = color;
//        this.drawable = drawable;
//        this.location = location;
//        this.image = image;
//        this.ara = ara;
//        this.eng = eng;
//        this.spa = spa;
//        this.fra = fra;
//        this.hin = hin;
//        this.ind = ind;
//        this.ita = ita;
//        this.jpn = jpn;
//        this.kor = kor;
//        this.pol = pol;
//        this.por = por;
//        this.rus = rus;
//        this.tur = tur;
//        this.vie = vie;
//        this.hans = hans;
//        this.hant = hant;
//    }
//
////    /* everything below here is for implementing Parcelable */
////    @Override
////    public int describeContents() {
////        return 0;
////    }
////
////    // write your object's data to the passed-in Parcel
////    @Override
////    public void writeToParcel(Parcel out, int flags) {
////        out.writeInt(code);
////        out.writeInt(color);
////        out.writeInt(drawable);
////        out.writeInt(location);
////        out.writeByteArray(image);
////    }
////
////    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
////    public static final Parcelable.Creator<TmpCategory> CREATOR = new Parcelable.Creator<TmpCategory>() {
////        public TmpCategory createFromParcel(Parcel in) {
////            return new TmpCategory(in);
////        }
////
////        public TmpCategory[] newArray(int size) {
////            return new TmpCategory[size];
////        }
////    };
////
////    // constructor
////    private TmpCategory(Parcel in) {
////        code = in.readInt();
////        color = in.readInt();
////        drawable = in.readInt();
////        location = in.readInt();
////        image = in.createByteArray();
////    }
//}
