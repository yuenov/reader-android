package com.yuenov.open.model.standard;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现首页item中书的item
 */
public class SpecialItemModel {

    public int id;
    public String name;

    public int page = 1;

    public List<CategoriesListItem> bookList = new ArrayList<>();
}
