package com.yuenov.open.model.httpModel;

/**
 * 保存用户信息
 */
public class UserUpdateHttpModel extends InterFaceBaseHttpModel {

    @Override
    public String getUrl() {
        return getInterFaceStart() + "user/update";
    }
}