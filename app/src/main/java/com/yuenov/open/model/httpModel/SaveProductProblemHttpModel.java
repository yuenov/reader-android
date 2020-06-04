package com.yuenov.open.model.httpModel;

/**
 * 意见反馈-产品问题
 */
public class SaveProductProblemHttpModel extends InterFaceBaseHttpModel {

    @Override
    public String getUrl() {
        return getInterFaceStart() + "problem/saveProductProblem";
    }
}