package com.yuenov.open.model.httpModel;

/**
 * 获取目录列表
 */
public class GetBookMenuListHttpModel extends InterFaceBaseHttpModel {

    public int bookId;
    public Long chapterId;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "chapter/getByBookId";
    }
}