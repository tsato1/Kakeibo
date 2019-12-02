package com.kakeibo;

public class KkbCategory {
    public int code;
    public String name;
    public int color;
    public int drawable;
    public int location;
    public int subCategories;
    public String description;
    public String savedDate;

    public String ar;
    public String en;
    public String es;
    public String fr;
    public String hi;
    public String in;
    public String it;
    public String jp;
    public String ko;
    public String pt;
    public String ru;

    public KkbCategory() {}

    public KkbCategory(int code,
                       String name,
                       int color,
                       int drawable,
                       int location,
                       int subCategories,
                       String description,
                       String savedDate) {
        this.code = code;
        this.name = name;
        this.color = color;
        this.drawable = drawable;
        this.location = location;
        this.subCategories = subCategories;
        this.description = description;
        this.savedDate = savedDate;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getLocation() {
        return location;
    }

    public int getSubCategories() {
        return subCategories;
    }

    public String getDescription() {
        return description;
    }

    public String getSavedDate() {
        return savedDate;
    }

    public String getAR() {
        return ar;
    }

    public String getEN() {
        return en;
    }

    public String getES() {
        return es;
    }

    public String getIT() {
        return it;
    }

    public String getIN() {
        return in;
    }

    public String getFR() {
        return fr;
    }

    public String getHI() {
        return hi;
    }

    public String getJA() {
        return jp;
    }

    public String getKO() {
        return ko;
    }

    public String getPT() {
        return pt;
    }

    public String getRU() {
        return ru;
    }
}
