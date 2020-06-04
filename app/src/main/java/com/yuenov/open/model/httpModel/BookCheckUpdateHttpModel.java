package com.yuenov.open.model.httpModel;

/**
 * 检查图书更新标志
 */
public class BookCheckUpdateHttpModel extends InterFaceBaseHttpModel {
    @Override
    public String getUrl() {
        return getInterFaceStart() + "book/checkUpdate";
    }
}