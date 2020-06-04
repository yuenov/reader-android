package com.yuenov.open.interfaces;

import com.yuenov.open.model.standard.DownloadBookContentItemInfo;

import java.util.List;

/**
 * 下载文章
 */
public interface IDownloadContentListener {

    void onDownloadSuccess(List<DownloadBookContentItemInfo> list);

    void onDownloadLoadFail();
}
