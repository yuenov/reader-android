package com.yuenov.open.model.httpModel;

import com.yuenov.open.constant.ConstantInterFace;
import com.renrui.libraries.model.baseObject.BaseHttpModel;

public class InterFaceBaseHttpModel extends BaseHttpModel {

    @Override
    public String getUrl() {
        return "";
    }

    public String getInterFaceStart() {
        return ConstantInterFace.getInterfaceDomain() + "open/api/";
    }
}