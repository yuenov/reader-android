package com.yuenov.open.model.httpModel;

import com.yuenov.open.constant.ConstantInterFace;

/**
 * 专题：换一匹 && 查看全部
 */
public class GetSpecialPageListHttpModel extends InterFaceBaseHttpModel {

    public int id;
    public int pageNum;
    public int pageSize = ConstantInterFace.pageSize;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "book/getSpecialPage";
    }
}