package com.kakeibo.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kakeibo.db.KkbAppDBAdapter;

@Entity(tableName = KkbAppDBAdapter.TABLE_KKBAPP)
public class KkbAppStatus {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = KkbAppDBAdapter.COL_ID)
    private long id = 1;

    @NonNull
    @ColumnInfo(name = KkbAppDBAdapter.COL_NAME, defaultValue = "")
    private String name;

    @NonNull
    @ColumnInfo(name = KkbAppDBAdapter.COL_TYPE, defaultValue = "")
    private String type;

    @NonNull
    @ColumnInfo(name = KkbAppDBAdapter.COL_UPDATE_DATE, defaultValue = "")
    private String updateDate;

    @ColumnInfo(name = KkbAppDBAdapter.COL_VAL_INT_1, defaultValue = "0")
    private int valInt1; // db version at installation
    @ColumnInfo(name = KkbAppDBAdapter.COL_VAL_INT_2, defaultValue = "-1")
    private int valInt2; // -1: default, 0: banner ads display agreed
    @ColumnInfo(name = KkbAppDBAdapter.COL_VAL_INT_3, defaultValue = "0")
    private int valInt3;

    @NonNull
    @ColumnInfo(name = KkbAppDBAdapter.COL_VAL_STR_1, defaultValue = "")
    private String valStr1;
    @NonNull
    @ColumnInfo(name = KkbAppDBAdapter.COL_VAL_STR_2, defaultValue = "")
    private String valStr2;
    @NonNull
    @ColumnInfo(name = KkbAppDBAdapter.COL_VAL_STR_3, defaultValue = "")
    private String valStr3;

    public KkbAppStatus(long id, String name, String type, String updateDate,
                        int valInt1, int valInt2, int valInt3,
                        String valStr1, String valStr2, String valStr3) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.updateDate = updateDate;
        this.valInt1 = valInt1;
        this.valInt2 = valInt2;
        this.valInt3 = valInt3;
        this.valStr1 = valStr1;
        this.valStr2 = valStr2;
        this.valStr3 = valStr3;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public int getValInt1() {
        return valInt1;
    }

    public int getValInt2() {
        return valInt2;
    }

    public int getValInt3() {
        return valInt3;
    }

    public String getValStr1() {
        return valStr1;
    }

    public String getValStr2() {
        return valStr2;
    }

    public String getValStr3() {
        return valStr3;
    }
}
