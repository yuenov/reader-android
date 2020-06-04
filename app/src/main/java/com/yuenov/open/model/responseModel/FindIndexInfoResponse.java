package com.yuenov.open.model.responseModel;

import com.yuenov.open.model.standard.FindItemBookItemModel;
import com.renrui.libraries.model.baseObject.BaseResponseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现首页
 */
public class FindIndexInfoResponse extends BaseResponseModel {

    public List<FindItemBookItemModel> list = new ArrayList<>();

    public int total;
    public int pageNum;
    public int pageSize;
}
