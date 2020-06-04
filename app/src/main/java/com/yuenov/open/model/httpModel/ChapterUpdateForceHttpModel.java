package com.yuenov.open.model.httpModel;

/**
 * 更新当前章节内容
 */
public class ChapterUpdateForceHttpModel extends InterFaceBaseHttpModel {

    @Override
    public String getUrl() {
        return getInterFaceStart() + "chapter/updateForce";
    }
}