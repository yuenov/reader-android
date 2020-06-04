package com.yuenov.open.interfaces;

import com.yuenov.open.model.standard.BookMenuItemInfo;

import java.util.List;

/**
 * 下载目录
 */
public interface IDownloadMenuListListener {

    void onDownloadSuccess(List<BookMenuItemInfo> chapters);

    void onDownloadLoadFail(String s);
}
