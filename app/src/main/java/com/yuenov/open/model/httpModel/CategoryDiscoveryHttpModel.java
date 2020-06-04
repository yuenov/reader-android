package com.yuenov.open.model.httpModel;

/**
 * 发现
 */
public class CategoryDiscoveryHttpModel extends InterFaceBaseHttpModel {

    public int pageNum;
    public int pageSize;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "category/discovery";
    }
}