package com.yuenov.open.model.httpModel;

import com.yuenov.open.constant.ConstantInterFace;

/**
 * 专题
 */
public class GetSpecialHttpModel extends InterFaceBaseHttpModel {

    public int pageNum;
    public int pageSize = ConstantInterFace.pageSize;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "book/getSpecialList";
    }
}