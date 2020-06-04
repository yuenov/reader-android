package com.yuenov.open.model.httpModel;

import com.yuenov.open.constant.ConstantInterFace;

/**
 * 搜索
 */
public class SearchBookHttpModel extends InterFaceBaseHttpModel {
    public String keyWord;
    public int pageNum;
    public int pageSize = ConstantInterFace.pageSize;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "book/search";
    }
}