package com.yuenov.open.model.responseModel;

import com.yuenov.open.model.standard.DownloadBookContentItemInfo;
import com.renrui.libraries.model.baseObject.BaseResponseModel;

import java.util.List;

/**
 * 下载
 */
public class DownloadListResponse extends BaseResponseModel {

    public List<DownloadBookContentItemInfo> list;
}
