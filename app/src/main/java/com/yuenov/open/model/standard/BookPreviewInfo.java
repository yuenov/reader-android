package com.yuenov.open.model.standard;

import java.util.List;

/**
 * 预览信息
 */
public class BookPreviewInfo {

    public String word;

    public String title;

    public String desc;

    public String categoryName;

    public String author;

    public int bookId;

    public String coverImg;

    public int chapterNum;

    /**
     * 更新信息
     */
    public  BPreviewUpdateInfo update;

    /**
     * 推荐信息
     */
    public List<CategoriesListItem> recommend;
}
