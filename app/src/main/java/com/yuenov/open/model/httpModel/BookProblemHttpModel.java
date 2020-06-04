package com.yuenov.open.model.httpModel;

/**
 * 书籍问题反馈
 */
public class BookProblemHttpModel extends InterFaceBaseHttpModel {

    public int bookId;
    public Long chapterId;
    public String correctType;

    @Override
    public String getUrl() {
        return getInterFaceStart() + "problem/saveBookCorrect";
    }
}