package com.yuenov.open.model.standard;

import com.renrui.libraries.model.baseObject.BaseDataProvider;

import java.util.List;

/**
 * 榜单列表
 */
public class RankListInfo extends BaseDataProvider {
    public int channelId;
    public String channelName;

    public List<RankItemInfo> ranks;
}
