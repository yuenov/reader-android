package com.yuenov.open.model.responseModel;

import com.yuenov.open.model.standard.SpecialItemModel;
import com.renrui.libraries.model.baseObject.BaseResponseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题首页
 */
public class IndexSpecialListResponse extends BaseResponseModel {

    public List<SpecialItemModel> specialList = new ArrayList<>();
}
