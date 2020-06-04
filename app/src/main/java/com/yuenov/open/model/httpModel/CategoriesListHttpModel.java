package com.yuenov.open.model.httpModel;

import com.yuenov.open.constant.ConstantInterFace;

/**
 * 目录列表
 */
public class CategoriesListHttpModel extends InterFaceBaseHttpModel {

    public int categoryId;

    /**
     * 渠道
     */
    public Integer channelId;

    public String filter;

    public String orderBy;

    public int pageNum;
    public int pageSize = ConstantInterFace.pageSize;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "book/getCategoryId";
    }
}