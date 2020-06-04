package com.yuenov.open.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.yuenov.open.R;
import com.yuenov.open.activitys.ReadDetailActivity;
import com.yuenov.open.activitys.ChapterSelectDownloadListActivity;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.database.tb.TbBookShelf;
import com.yuenov.open.database.tb.TbReadHistory;
import com.yuenov.open.interfaces.IDownloadContentListener;
import com.yuenov.open.interfaces.IDownloadMenuListListener;
import com.yuenov.open.interfaces.IGetCategoryListListener;
import com.yuenov.open.model.eventBus.OnBookShelfChangeEvent;
import com.yuenov.open.model.eventBus.OnDownloadMenuFinishChangeEvent;
import com.yuenov.open.model.httpModel.BookGetRecommendHttpModel;
import com.yuenov.open.model.httpModel.CategoriesListHttpModel;
import com.yuenov.open.model.httpModel.CategoryDiscoveryAllListHttpModel;
import com.yuenov.open.model.httpModel.DownloadChapterHttpModel;
import com.yuenov.open.model.httpModel.GetBookMenuListHttpModel;
import com.yuenov.open.model.httpModel.GetSpecialPageListHttpModel;
import com.yuenov.open.model.responseModel.CategoriesListResponse;
import com.yuenov.open.model.responseModel.DownloadListResponse;
import com.yuenov.open.model.responseModel.MenuListResponse;
import com.yuenov.open.model.standard.BookMenuItemInfo;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.model.standard.DownloadBookContentItemInfo;
import com.yuenov.open.model.standard.DownloadChapterRequestInfo;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.Logger;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilityTime;
import com.renrui.libraries.util.mHttpClient;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class UtilityBusiness {

    public static void updateChapterList(Context context, int bookId, boolean showProcess, IDownloadMenuListListener listener) {
        if (context == null)
            return;

        final GetBookMenuListHttpModel httpModel = new GetBookMenuListHttpModel();
        httpModel.bookId = bookId;

        // 本地无章节数据 获取全部
        final TbBookChapter bookChapter = AppDatabase.getInstance().ChapterDao().getLastChapter(bookId);
        if (bookChapter == null) {
            httpModel.chapterId = 0l;
        }
        // 本地有章节数据 更新最后一章之后的数据
        else {
            httpModel.chapterId = bookChapter.chapterId;
        }

        mHttpClient.Request(context, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (showProcess && context != null && context instanceof BaseActivity)
                    ((BaseActivity) context).getStatusTip().showProgressStyle2();
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, false)) {
                    if (listener != null)
                        listener.onDownloadLoadFail(s);
                    return;
                }

                MenuListResponse res;
                try {
                    res = mHttpClient.fromDataJson(s, MenuListResponse.class);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                    if (listener != null)
                        listener.onDownloadLoadFail(s);
                    return;
                }

                if (res == null)
                    res = new MenuListResponse();

                if (res.chapters == null)
                    res.chapters = new ArrayList<>();

                try {
                    // 接口会返回请求参数中的章节 由于该章节本地已存在，所以从返回结果中删掉此章节
                    if (httpModel.chapterId > 0 && !UtilitySecurity.isEmpty(res.chapters)) {
                        for (int i = 0; i < res.chapters.size(); i++) {
                            if (res.chapters.get(i).id == httpModel.chapterId) {
                                res.chapters.remove(i);
                                break;
                            }
                        }
                    }

                    // 更新数据库
                    AppDatabase.getInstance().ChapterDao().addChapter(bookId, res.chapters);

                    if (listener != null)
                        listener.onDownloadSuccess(res.chapters);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                    if (listener != null)
                        listener.onDownloadLoadFail(s);
                }
            }

            @Override
            public void onErrorResponse(String s) {
                if (listener != null)
                    listener.onDownloadLoadFail(s);
            }

            @Override
            public void onFinish() {

                if (showProcess && context != null && context instanceof BaseActivity)
                    ((BaseActivity) context).getStatusTip().hideProgress();
            }
        });
    }

    /**
     * @param activity
     * @param bId
     * @param lisWaitDownload
     * @param isBackDownload
     * @param listener
     */
    public static void downloadContent(BaseActivity activity, int bId, List<Long> lisWaitDownload, boolean isBackDownload, IDownloadContentListener listener) {
        startDownloadContent(activity, bId, lisWaitDownload, isBackDownload, true, listener);
    }

    /**
     * 下载并保存到数据库
     *
     * @param activity
     * @param bId             bookId
     * @param lisWaitDownload 下载列表
     * @param isBackDownload  静默下载
     * @param listener
     */
    public static void startDownloadContent(BaseActivity activity, int bId, List<Long> lisWaitDownload, boolean isBackDownload, boolean showErrorMessage, IDownloadContentListener listener) {
        DownloadChapterRequestInfo requestInfo = new DownloadChapterRequestInfo();
        requestInfo.bookId = bId;
        requestInfo.chapterIdList.addAll(lisWaitDownload);

        DownloadChapterHttpModel httpModel = new DownloadChapterHttpModel();
        httpModel.setIsPostJson(true);
        httpModel.setTimeOut((int) UtilityTime.lMinuteTimes * 100);
        httpModel.setPostJsonText(mHttpClient.GetGsonInstance().toJson(requestInfo));
        mHttpClient.Request(MyApplication.getAppContext(), httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (!isBackDownload)
                    activity.getStatusTip().showDownloadProgress();
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, showErrorMessage)) {
                    if (listener != null)
                        listener.onDownloadLoadFail();
                    return;
                }

                DownloadListResponse res = null;
                try {
                    res = mHttpClient.fromDataJson(s, DownloadListResponse.class);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }

                if (res == null || UtilitySecurity.isEmpty(res.list)) {
                    UtilityToasty.error(R.string.info_error_download_failure);
                    if (listener != null)
                        listener.onDownloadLoadFail();
                    return;
                }

                try {
                    AppDatabase.getInstance().ChapterDao().addContent(bId, res.list);

                    // 移除下载成功的章节
                    for (int i = 0; i < res.list.size(); i++)
                        lisWaitDownload.remove(res.list.get(i).id);

                    activity.getStatusTip().hideProgress();

                    // 下载完成
                    if (UtilitySecurity.isEmpty(lisWaitDownload)) {
                        if (listener != null)
                            listener.onDownloadSuccess(res.list);
                    }

                    OnDownloadMenuFinishChangeEvent eventModel = new OnDownloadMenuFinishChangeEvent();
                    eventModel.bookId = bId;
                    EventBus.getDefault().post(eventModel);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {

                if (showErrorMessage)
                    UtilityToasty.error(s);

                if (listener != null)
                    listener.onDownloadLoadFail();

                activity.getStatusTip().hideProgress();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 下载并保存到数据库,普通加载进度条
     *
     * @param activity
     * @param bId
     * @param lisWaitDownload
     * @param listener
     */
    public static void downloadChapter(BaseActivity activity, int bId, List<Long> lisWaitDownload, IDownloadContentListener listener) {
        DownloadChapterRequestInfo requestInfo = new DownloadChapterRequestInfo();
        requestInfo.bookId = bId;
        requestInfo.chapterIdList.addAll(lisWaitDownload);

        DownloadChapterHttpModel httpModel = new DownloadChapterHttpModel();
        httpModel.setIsPostJson(true);
        httpModel.setTimeOut((int) UtilityTime.lMinuteTimes * 100);
        httpModel.setPostJsonText(mHttpClient.GetGsonInstance().toJson(requestInfo));
        mHttpClient.Request(MyApplication.getAppContext(), httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                activity.getStatusTip().showProgress();
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    return;
                }

                DownloadListResponse res = null;
                try {
                    res = mHttpClient.fromDataJson(s, DownloadListResponse.class);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }

                if (res == null) {
                    UtilityToasty.error(R.string.info_error_download_failure);
                    if (listener != null)
                        listener.onDownloadLoadFail();
                    return;
                }

                try {
                    AppDatabase.getInstance().ChapterDao().addContent(bId, res.list);

                    if (listener != null) {
                        if (!UtilitySecurity.isEmpty(lisWaitDownload)) {
                            listener.onDownloadSuccess(res.list);
                        } else {
                            listener.onDownloadLoadFail();
                        }
                    }
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                    if (listener != null)
                        listener.onDownloadLoadFail();
                }
            }

            @Override
            public void onErrorResponse(String s) {
                UtilityToasty.error(s);
                if (listener != null)
                    listener.onDownloadLoadFail();
            }

            @Override
            public void onFinish() {
                activity.getStatusTip().hideProgress();
            }
        });
    }

    private static boolean isReplaceing = false;

    /**
     * 首页发现：换一批
     *
     * @param activity
     * @param categoryId
     * @param pageSize
     * @param pageNum
     * @param listListener
     */
    public static void getReplaceCategoryBooks(BaseActivity activity, int categoryId, int pageSize, int pageNum, String type, IGetCategoryListListener listListener) {
        if (isReplaceing)
            return;

        if (pageSize < 8)
            pageNum = 1;

        CategoryDiscoveryAllListHttpModel httpModel = new CategoryDiscoveryAllListHttpModel();
        httpModel.categoryId = categoryId;
        httpModel.pageNum = pageNum;
        httpModel.pageSize = 8;
        httpModel.type = type;

        mHttpClient.Request(activity, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                isReplaceing = true;
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, false)) {
                    return;
                }

                try {
                    CategoriesListResponse res = mHttpClient.fromDataJson(s, CategoriesListResponse.class);

                    if (res != null && listListener != null)
                        listListener.onGetCategoryListSuccess(res.list);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {

            }

            @Override
            public void onFinish() {
                isReplaceing = false;
            }
        });
    }

    /**
     * 完本：换一批
     *
     * @param activity
     * @param categoryId
     * @param pageSize
     * @param pageNum
     * @param listListener
     */
    public static void getReplaceCategoryEndBooks(BaseActivity activity, int categoryId, int pageSize, int pageNum, String type, IGetCategoryListListener listListener) {

        if (isReplaceing)
            return;

        if (pageSize < 8)
            pageNum = 1;

        CategoriesListHttpModel httpModel = new CategoriesListHttpModel();
        httpModel.categoryId = categoryId;
        httpModel.pageNum = pageNum;
        httpModel.pageSize = 8;
        httpModel.orderBy = "end";

        mHttpClient.Request(activity, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                isReplaceing = true;
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, false)) {
                    return;
                }

                try {
                    CategoriesListResponse res = mHttpClient.fromDataJson(s, CategoriesListResponse.class);

                    if (res != null && listListener != null)
                        listListener.onGetCategoryListSuccess(res.list);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {

            }

            @Override
            public void onFinish() {
                isReplaceing = false;
            }
        });
    }

    /**
     * 专题:换一批
     *
     * @param activity
     * @param id
     * @param pageSize
     * @param pageNum
     * @param listListener
     */
    public static void getReplaceSpecialPageBooks(BaseActivity activity, int id, int pageSize, int pageNum, IGetCategoryListListener listListener) {

        if (isReplaceing)
            return;

        if (pageSize < 8)
            pageNum = 1;

        GetSpecialPageListHttpModel httpModel = new GetSpecialPageListHttpModel();
        httpModel.id = id;
        httpModel.pageNum = pageNum;
        httpModel.pageSize = 8;

        mHttpClient.Request(activity, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                isReplaceing = true;
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, false)) {
                    return;
                }

                try {
                    CategoriesListResponse res = mHttpClient.fromDataJson(s, CategoriesListResponse.class);

                    if (res != null && listListener != null)
                        listListener.onGetCategoryListSuccess(res.list);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {

            }

            @Override
            public void onFinish() {
                isReplaceing = false;
            }
        });
    }

    /**
     * 预览页:换一批
     *
     * @param activity
     * @param bId
     * @param pageSize
     * @param pageNum
     * @param listListener
     */
    public static void getPreviewBooks(BaseActivity activity, int bId, int pageSize, int pageNum, IGetCategoryListListener listListener) {

        if (isReplaceing)
            return;

        if (pageSize < 6)
            pageNum = 1;

        BookGetRecommendHttpModel httpModel = new BookGetRecommendHttpModel();
        httpModel.bookId = bId;
        httpModel.pageNum = pageNum;
        httpModel.pageSize = 6;

        mHttpClient.Request(activity, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                isReplaceing = true;
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, false)) {
                    return;
                }

                try {
                    CategoriesListResponse res = mHttpClient.fromDataJson(s, CategoriesListResponse.class);

                    if (res != null && listListener != null)
                        listListener.onGetCategoryListSuccess(res.list);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                    return;
                }
            }

            @Override
            public void onErrorResponse(String s) {

            }

            @Override
            public void onFinish() {
                isReplaceing = false;
            }
        });
    }

    /**
     * 获取自动下载章节列表（取前一章和后一章）
     * 默认下载当前章节的前一章和后一章，当前章节是第一章下载后两章，当前章节最后一章下载前两章。如果当前章节前一章后一章已经存在不需要下载
     *
     * @param tbBookChapter
     * @return
     */
    public static List<Long> getAutoDownLoadChapterId(TbBookChapter tbBookChapter) {

        List<Long> lisWaitDownload = new ArrayList<>();

        // 如果是第一章 则获取后两章， 否则获取后一章
        int afterLimit;
        TbBookChapter firstBookChapter = AppDatabase.getInstance().ChapterDao().getFirstChapter(tbBookChapter.bookId);
        if (firstBookChapter != null && firstBookChapter.chapterId == tbBookChapter.chapterId)
            afterLimit = 2;
        else
            afterLimit = 1;
        List<TbBookChapter> lisAfterWaitDownload = AppDatabase.getInstance().ChapterDao().getAfterChapterId(tbBookChapter.bookId, tbBookChapter.chapterId, afterLimit);
        if (!UtilitySecurity.isEmpty(lisAfterWaitDownload)) {
            for (int i = 0; i < lisAfterWaitDownload.size(); i++) {
                if (UtilitySecurity.isEmpty(lisAfterWaitDownload.get(i).content))
                    lisWaitDownload.add(lisAfterWaitDownload.get(i).chapterId);
            }
        }

        // 如果是最后一章 则获取前两章， 否则获取前一章
        int beforeLimit;
        TbBookChapter LastBookChapter = AppDatabase.getInstance().ChapterDao().getLastChapter(tbBookChapter.bookId);
        if (LastBookChapter != null && LastBookChapter.chapterId == tbBookChapter.chapterId)
            beforeLimit = 2;
        else
            beforeLimit = 1;
        List<TbBookChapter> lisBeforeWaitDownload = AppDatabase.getInstance().ChapterDao().getBeforeChapterId(tbBookChapter.bookId, tbBookChapter.chapterId, beforeLimit);
        if (!UtilitySecurity.isEmpty(lisBeforeWaitDownload)) {
            for (int i = 0; i < lisBeforeWaitDownload.size(); i++)
                if (UtilitySecurity.isEmpty(lisBeforeWaitDownload.get(i).content))
                    lisWaitDownload.add(lisBeforeWaitDownload.get(i).chapterId);
        }

        // 如果当前没有内容，也下载
        if (UtilitySecurity.isEmpty(tbBookChapter.content))
            lisWaitDownload.add(tbBookChapter.chapterId);

        return lisWaitDownload;
    }

    /**
     * 下载目录
     *
     * @param context
     * @param bookId
     */
    public static void toDownloadMenuList(Context context, int bookId) {

        TbBookChapter bookChapter = AppDatabase.getInstance().ChapterDao().getFirstChapter(bookId);
        if (bookChapter != null) {
            Intent intent = ChapterSelectDownloadListActivity.getIntent(context, bookId);
            context.startActivity(intent);
            return;
        }

        UtilityBusiness.updateChapterList(context, bookId, true, new IDownloadMenuListListener() {
            @Override
            public void onDownloadSuccess(List<BookMenuItemInfo> chapters) {
                Intent intent = ChapterSelectDownloadListActivity.getIntent(context, bookId);
                context.startActivity(intent);
            }

            @Override
            public void onDownloadLoadFail(String s) {

            }
        });
    }

    /**
     * 去阅读详情
     *
     * @param activity
     * @param bookBaseInfo
     * @param chapterId
     */
    public static void toRead(BaseActivity activity, BookBaseInfo bookBaseInfo, long chapterId) {
        if (activity == null || bookBaseInfo == null)
            return;

        if (activity.getStatusTip().isShowing())
            return;

        // 本地有该章节 去阅读
        TbBookChapter bookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, chapterId);
        if (bookChapter != null) {
            toReadDetail(activity, bookBaseInfo, chapterId);
        }
        // 否则去下载该章节列表
        else {
            activity.getStatusTip().showProgressStyle2();
            UtilityBusiness.updateChapterList(activity, bookBaseInfo.bookId, false, new IDownloadMenuListListener() {
                @Override
                public void onDownloadSuccess(List<BookMenuItemInfo> chapters) {
                    activity.getStatusTip().hideProgress();

                    toReadDetail(activity, bookBaseInfo, chapterId);
                }

                @Override
                public void onDownloadLoadFail(String s) {
                    activity.getStatusTip().hideProgress();
                    UtilityToasty.error(s);
                }
            });
        }
    }

    /**
     * 去阅读详情
     *
     * @param activity
     * @param bookBaseInfo
     */
    public static void toRead(BaseActivity activity, BookBaseInfo bookBaseInfo) {

        if (activity == null || bookBaseInfo == null)
            return;

        if (activity.getStatusTip().isShowing())
            return;

        // 有阅读记录，从阅读记录的章节开始读
        TbReadHistory readHistory = AppDatabase.getInstance().ReadHistoryDao().getEntity(bookBaseInfo.bookId);
        if (readHistory != null && readHistory.chapterId > 0) {
            toReadDetail(activity, bookBaseInfo, readHistory.chapterId);
        }
        // 没有阅读记录，从第一章开始读
        else {
            // 本地有第一章，直接读
            // 本地没有第一章，先下载目录 再从第一章开始读
            TbBookChapter tbBookChapter = AppDatabase.getInstance().ChapterDao().getFirstChapter(bookBaseInfo.bookId);
            if (tbBookChapter != null && tbBookChapter.chapterId > 0) {
                toReadDetail(activity, bookBaseInfo, tbBookChapter.chapterId);
            } else {
                activity.getStatusTip().showProgressStyle2();
                UtilityBusiness.updateChapterList(activity, bookBaseInfo.bookId, false, new IDownloadMenuListListener() {
                    @Override
                    public void onDownloadSuccess(List<BookMenuItemInfo> chapters) {
                        activity.getStatusTip().hideProgress();

                        TbBookChapter tbBookChapter = AppDatabase.getInstance().ChapterDao().getFirstChapter(bookBaseInfo.bookId);
                        if (tbBookChapter != null && tbBookChapter.chapterId > 0)
                            toReadDetail(activity, bookBaseInfo, tbBookChapter.chapterId);
                    }

                    @Override
                    public void onDownloadLoadFail(String s) {
                        activity.getStatusTip().hideProgress();
                        UtilityToasty.error(s);
                    }
                });
            }
        }
    }

    /**
     * 去阅读详情
     *
     * @param activity
     * @param bookBaseInfo
     * @param startChapterId 指定章节id
     */
    private static void toReadDetail(BaseActivity activity, BookBaseInfo bookBaseInfo, long startChapterId) {
        final TbBookChapter tbBookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, startChapterId);
        if (tbBookChapter == null) {
            UtilityToasty.error(R.string.Utility_unknown);
            return;
        }

        // 该章节有内容去阅读详情
        if (!UtilitySecurity.isEmpty(tbBookChapter.content)) {
            Intent intent = ReadDetailActivity.getIntent(activity, bookBaseInfo, tbBookChapter.chapterId);
            activity.startActivity(intent);
            return;
        }

        // 没有章节内容 下载章节内容(此处下载展示普通进度条)
        List<Long> lisWaitDownload = new ArrayList<>();
        lisWaitDownload.add(startChapterId);
        activity.getStatusTip().showProgress();
        UtilityBusiness.downloadContent(activity, bookBaseInfo.bookId, lisWaitDownload, true, new IDownloadContentListener() {
            @Override
            public void onDownloadSuccess(List<DownloadBookContentItemInfo> list) {
                activity.getStatusTip().hideProgress();

                Intent intent = ReadDetailActivity.getIntent(activity, bookBaseInfo, tbBookChapter.chapterId);
                activity.startActivity(intent);
            }

            @Override
            public void onDownloadLoadFail() {
                activity.getStatusTip().hideProgress();
            }
        });
    }

    /**
     * 添加到书架
     */
    public static void addBookShelf(BookBaseInfo bookBaseInfo) {
        if (bookBaseInfo == null)
            return;

        // 添加书架上的书
        TbBookShelf tbBookShelf = new TbBookShelf();
        tbBookShelf.bookId = bookBaseInfo.bookId;
        tbBookShelf.title = bookBaseInfo.title;
        tbBookShelf.author = bookBaseInfo.author;
        tbBookShelf.coverImg = bookBaseInfo.coverImg;
        tbBookShelf.addTime = System.currentTimeMillis();
        AppDatabase.getInstance().BookShelfDao().addOrUpdate(tbBookShelf);
        // 同步浏览记录
        AppDatabase.getInstance().ReadHistoryDao().resetAddBookShelfStat(bookBaseInfo.bookId, true);

        // 发送通知
        OnBookShelfChangeEvent event = new OnBookShelfChangeEvent();
        event.addTbBookShelf = tbBookShelf;
        EventBus.getDefault().post(event);
    }

    /**
     * 移除书架
     */
    public static void removeBookShelf(int bookId) {

        // 删除书架上的书
        AppDatabase.getInstance().BookShelfDao().deleteByBookId(bookId);
        // 同步浏览记录
        AppDatabase.getInstance().ReadHistoryDao().resetAddBookShelfStat(bookId, false);

        // 发送通知
        OnBookShelfChangeEvent event = new OnBookShelfChangeEvent();
        event.removeBookId = bookId;
        EventBus.getDefault().post(event);
    }

    /**
     * 打开微信app
     */
    public static void openWeChatApp(Activity activity) {
        try {
            Intent lan = activity.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(lan.getComponent());
            activity.startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 打开QQ App
     */
    public static void openQQApp(Activity activity) {

        try {
            Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.tencent.moblieqq");

            if (intent == null) {
                String url = "mqqwpa://im/chat?chat_type=wpa";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(Intent.ACTION_VIEW);
            }

            activity.startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
