package com.yuenov.open.model.httpModel;

import com.yuenov.open.constant.ConstantInterFace;

/**
 * 完本
 */
public class GetCategoryEndHttpModel extends InterFaceBaseHttpModel {

    public int pageNum;
    public int pageSize = ConstantInterFace.pageSize;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "category/getCategoryEnd";
    }
}