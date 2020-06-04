package com.yuenov.open.database.tb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 缓存
 */
@Entity(indices = {@Index(value = {"id", "cType"})})
public class TbCache {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "cType")
    public String cType;

    @ColumnInfo(name = "cContent")
    public String cContent;
}