package com.yuenov.open.model.httpModel;

// 用户服务协议
public class PrivacyUseragreementHttpModel extends InterFaceBaseHttpModel {

    @Override
    public String getUrl() {
        return getInterFaceStart() + "privacy/useragreement";
    }
}
