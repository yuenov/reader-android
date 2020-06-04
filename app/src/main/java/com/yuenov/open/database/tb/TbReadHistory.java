package com.yuenov.open.database.tb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 阅读记录
 */
@Entity(indices = {@Index(value = {"id","bookId"})})
public class TbReadHistory {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "bookId")
    public int bookId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "chapterId")
    public long chapterId;

    @ColumnInfo(name = "page")
    public int page;

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
     * 是否加入书架
     */
    @ColumnInfo(name = "addBookShelf")
    public boolean addBookShelf;

    /**
     * 最后阅读时间
     */
    @ColumnInfo(name = "lastReadTime")
    public long lastReadTime;
}