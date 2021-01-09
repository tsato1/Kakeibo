//package com.kakeibo.data;
//
//import androidx.room.ColumnInfo;
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;
//
//import com.kakeibo.db.CategoryLanDBAdapter;
//
//import org.jetbrains.annotations.NotNull;
//
//@Entity(tableName = "categories_lan")
//public class CategoryLanStatus {
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_ID)
//    private long id = 1;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_CODE)
//    @NotNull
//    private int code;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_ARA)
//    @NotNull
//    private String ara;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_ENG)
//    @NotNull
//    private String eng;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_SPA)
//    @NotNull
//    private String spa;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_FRA)
//    @NotNull
//    private String fra;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_HIN)
//    @NotNull
//    private String hin;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_IND)
//    @NotNull
//    private String ind;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_ITA)
//    @NotNull
//    private String ita;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_JPN)
//    @NotNull
//    private String jpn;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_KOR)
//    @NotNull
//    private String kor;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_POL)
//    @NotNull
//    private String pol;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_POR)
//    @NotNull
//    private String por;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_RUS)
//    @NotNull
//    private String rus;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_TUR)
//    @NotNull
//    private String tur;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_VIE)
//    @NotNull
//    private String vie;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_Hans)
//    @NotNull
//    private String hans;
//
//    @ColumnInfo(name = CategoryLanDBAdapter.COL_Hant)
//    @NotNull
//    private String hant;
//
//    public CategoryLanStatus(long id,
//                             int code,
//                             String ara,
//                             String eng,
//                             String spa,
//                             String fra,
//                             String hin,
//                             String ind,
//                             String ita,
//                             String jpn,
//                             String kor,
//                             String pol,
//                             String por,
//                             String rus,
//                             String tur,
//                             String vie,
//                             String hans,
//                             String hant) {
//        this.id = id;
//        this.code = code;
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
//    public long getId() {
//        return this.id;
//    }
//
//    public int getCode() {
//        return this.code;
//    }
//
//    public String getAra() {
//        return this.ara;
//    }
//
//    public String getEng() {
//        return this.eng;
//    }
//
//    public String getSpa() {
//        return this.spa;
//    }
//
//    public String getFra() {
//        return this.fra;
//    }
//
//    public String getHin() {
//        return this.hin;
//    }
//
//    public String getInd() {
//        return this.ind;
//    }
//
//    public String getIta() {
//        return this.ita;
//    }
//
//    public String getJpn() {
//        return this.jpn;
//    }
//
//    public String getKor() {
//        return this.kor;
//    }
//
//    public String getPol() {
//        return this.pol;
//    }
//
//    public String getPor() {
//        return this.por;
//    }
//
//    public String getRus() {
//        return this.rus;
//    }
//
//    public String getTur() {
//        return this.tur;
//    }
//
//    public String getVie() {
//        return this.vie;
//    }
//
//    public String getHans() {
//        return this.hans;
//    }
//
//    public String getHant() {
//        return this.hant;
//    }
//}