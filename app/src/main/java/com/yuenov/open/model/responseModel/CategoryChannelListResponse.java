package com.yuenov.open.model.responseModel;

import com.yuenov.open.model.standard.CategoryChannelItemInfo;
import com.renrui.libraries.model.baseObject.BaseResponseModel;

import java.util.ArrayList;

/**
 * 分类
 */
public class CategoryChannelListResponse extends BaseResponseModel {

    public ArrayList<CategoryChannelItemInfo> channels = new ArrayList<>();
}
