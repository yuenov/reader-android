package com.yuenov.open.model.httpModel;

/**
 * 预览
 */
public class BookPreviewHttpModel extends InterFaceBaseHttpModel {

    public int bookId;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "book/getDetail";
    }
}