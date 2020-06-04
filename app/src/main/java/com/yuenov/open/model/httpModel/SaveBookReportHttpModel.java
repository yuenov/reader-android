package com.yuenov.open.model.httpModel;

/**
 * 意见反馈-缺少书籍
 */
public class SaveBookReportHttpModel extends InterFaceBaseHttpModel {

    public String author;
    public String title;
    public Integer bookId;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "problem/saveBookReport";
    }
}