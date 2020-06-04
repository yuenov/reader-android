package com.yuenov.open.model.httpModel;

/**
 *
 */
public class GetRankPageListHttpModel extends InterFaceBaseHttpModel {

    public int channelId;

    public int pageNum;

    public int pageSize;

    public int rankId;

    public String getUrl() {
        return getInterFaceStart() + "rank/getPage";
    }
}