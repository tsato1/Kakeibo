package com.kakeibo.db

class TmpCategory {
    var id = 0
    var code = 0
    var color = 0
    var drawable = 0
    var location = 0
    var image: ByteArray? = null
    var ara: String? = null
    var eng: String? = null
    var spa: String? = null
    var fra: String? = null
    var hin: String? = null
    var ind: String? = null
    var ita: String? = null
    var jpn: String? = null
    var kor: String? = null
    var pol: String? = null
    var por: String? = null
    var rus: String? = null
    var tur: String? = null
    var vie: String? = null
    var hans: String? = null
    var hant: String? = null

    constructor() {}
    constructor(id: Int, code: Int, color: Int, drawable: Int, location: Int, image: ByteArray?,
                ara: String?, eng: String?, spa: String?, fra: String?, hin: String?, ind: String?,
                ita: String?, jpn: String?, kor: String?, pol: String?, por: String?, rus: String?,
                tur: String?, vie: String?, hans: String?, hant: String?) {
        this.id = id
        this.code = code
        this.color = color
        this.drawable = drawable
        this.location = location
        this.image = image
        this.ara = ara
        this.eng = eng
        this.spa = spa
        this.fra = fra
        this.hin = hin
        this.ind = ind
        this.ita = ita
        this.jpn = jpn
        this.kor = kor
        this.pol = pol
        this.por = por
        this.rus = rus
        this.tur = tur
        this.vie = vie
        this.hans = hans
        this.hant = hant
    } //    /* everything below here is for implementing Parcelable */
    //    @Override
    //    public int describeContents() {
    //        return 0;
    //    }
    //
    //    // write your object's data to the passed-in Parcel
    //    @Override
    //    public void writeToParcel(Parcel out, int flags) {
    //        out.writeInt(code);
    //        out.writeInt(color);
    //        out.writeInt(drawable);
    //        out.writeInt(location);
    //        out.writeByteArray(image);
    //    }
    //
    //    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    //    public static final Parcelable.Creator<TmpCategory> CREATOR = new Parcelable.Creator<TmpCategory>() {
    //        public TmpCategory createFromParcel(Parcel in) {
    //            return new TmpCategory(in);
    //        }
    //
    //        public TmpCategory[] newArray(int size) {
    //            return new TmpCategory[size];
    //        }
    //    };
    //
    //    // constructor
    //    private TmpCategory(Parcel in) {
    //        code = in.readInt();
    //        color = in.readInt();
    //        drawable = in.readInt();
    //        location = in.readInt();
    //        image = in.createByteArray();
    //    }
}