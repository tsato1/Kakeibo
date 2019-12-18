package com.kakeibo;

public class KkbCategory {
    private int code;
    private String name;
    private int color;
    private int significance;
    private int drawable;
    private int location;
    private int parent;
    private String description;
    private String savedDate;

    public KkbCategory(int code,
                       String name,
                       int color,
                       int significance,
                       int drawable,
                       int location,
                       int parent,
                       String description,
                       String savedDate) {
        this.code = code;
        this.name = name;
        this.color = color;
        this.significance = significance;
        this.drawable = drawable;
        this.location = location;
        this.parent = parent;
        this.description = description;
        this.savedDate = savedDate;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getColor() { return color; }

    public int getSignificance() { return significance; }

    public int getDrawable() {
        return drawable;
    }

    public int getLocation() {
        return location;
    }

    public int getParent() {
        return parent;
    }

    public String getDescription() {
        return description;
    }

    public String getSavedDate() {
        return savedDate;
    }
}
