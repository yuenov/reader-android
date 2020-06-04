package com.yuenov.open.interfaces;

import com.yuenov.open.model.standard.CategoriesListItem;

import java.util.List;

/**
 * 获取目录下的图书列表
 */
public interface IGetCategoryListListener {

    void onGetCategoryListSuccess(List<CategoriesListItem> list);

    void onGetCategoryListLoadFail();
}
