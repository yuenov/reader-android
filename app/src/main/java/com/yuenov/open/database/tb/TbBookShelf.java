package com.yuenov.open.database.tb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 书架
 */
@Entity(indices = {@Index(value = {"id", "bookId"})})
public class TbBookShelf {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "bookId")
    public int bookId;

    @ColumnInfo(name = "title")
    public String title;

    /**
     * 图片地址
     */
    @ColumnInfo(name = "coverImg")
    public String coverImg;

    /**
     * 作者
     */
    @ColumnInfo(name = "author")
    public String author;

    /**
     * 需要更新
     */
    @ColumnInfo(name = "hasUpdate")
    public boolean hasUpdate;

    /**
     * 加入时间
     */
    @ColumnInfo(name = "addTime")
    public long addTime;
}