package com.kakeibo.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.kakeibo.db.CategoryDBAdapter;

@Entity(tableName = "categories")
public class CategoryStatus {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CategoryDBAdapter.COL_ID)
    private long id = 1;

    @ColumnInfo(name = CategoryDBAdapter.COL_CODE, defaultValue = "0")
    private int code = 0;

    @ColumnInfo(name = CategoryDBAdapter.COL_NAME, defaultValue = "")
    @NonNull
    private String name = "";

    @ColumnInfo(name = CategoryDBAdapter.COL_COLOR, defaultValue = "0")
    private int color = 0;

    @ColumnInfo(name = CategoryDBAdapter.COL_SIGNIFICANCE, defaultValue = "0")
    private int significance = 0;

    @ColumnInfo(name = CategoryDBAdapter.COL_DRAWABLE, defaultValue = "")
    @NonNull
    private String drawable = "";

    @ColumnInfo(name = CategoryDBAdapter.COL_IMAGE)
    private byte[] image;

    @ColumnInfo(name = CategoryDBAdapter.COL_PARENT, defaultValue = "-1")
    private int parent = -1;

    @ColumnInfo(name = CategoryDBAdapter.COL_DESC, defaultValue = "")
    @NonNull
    private String description = "";

    @ColumnInfo(name = CategoryDBAdapter.COL_SAVED_DATE, defaultValue = "")
    @NonNull
    private String savedDate = "";

    public CategoryStatus(long id,
                          int code,
                          String name,
                          int color,
                          int significance,
                          String drawable,
                          byte[] image,
                          int parent,
                          String description,
                          String savedDate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.color = color;
        this.significance = significance;
        this.drawable = drawable;
        this.image = image;
        this.parent = parent;
        this.description = description;
        this.savedDate = savedDate;
    }

    @Ignore
    public CategoryStatus(
                          int code,
                          int color,
                          int significance,
                          String drawable,
                          byte[] image,
                          int parent,
                          String description,
                          String savedDate) {
        this.code = code;
        this.color = color;
        this.significance = significance;
        this.drawable = drawable;
        this.image = image;
        this.parent = parent;
        this.description = description;
        this.savedDate = savedDate;
    }
    /***
     * Getter
     */
    public long getId() { return id; }

    public int getCode() { return code; }

    public String getName() { return name; }

    public int getColor() { return color; }

    public int getSignificance() { return significance; }

    public String getDrawable() { return drawable; }

    public byte[] getImage() { return image; }

    public int getParent() { return parent; }

    public String getDescription() { return description; }

    public String getSavedDate() { return savedDate; }

    /***
     * Setter
     */
    public void setCode(int code) { this.code = code; }

    public void setName(String name) { this.name = name; }

    public void setColor(int color) { this.color = color; }

    public void setSignificance(int significance) { this.significance = significance; }

    public void setDrawable(String drawable) { this.drawable = drawable; }

    public void setImage(byte[] image) { this.image = image; }

    public void setParent(int parent) { this.parent = parent; }

    public void setDescription(String description) { this.description = description; }

    public void setSavedDate(String savedDate) { this.savedDate = savedDate; }
}