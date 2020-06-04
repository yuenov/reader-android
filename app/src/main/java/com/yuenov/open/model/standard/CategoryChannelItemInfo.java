package com.yuenov.open.model.standard;

import com.renrui.libraries.model.baseObject.BaseDataProvider;

import java.util.ArrayList;

public class CategoryChannelItemInfo extends BaseDataProvider {

    public int channelId;
    public String channelName;

    public ArrayList<CategoryMenuItem> categories = new ArrayList<>();
}