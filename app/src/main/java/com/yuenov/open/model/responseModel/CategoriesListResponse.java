package com.yuenov.open.model.responseModel;

import com.yuenov.open.model.standard.CategoriesListItem;
import com.renrui.libraries.model.baseObject.BaseResponseModel;

import java.util.List;

/**
 * 目录
 */
public class CategoriesListResponse extends BaseResponseModel {

    public int total;
    public int page;
    public int page_size;

    public List<CategoriesListItem> list;
}
