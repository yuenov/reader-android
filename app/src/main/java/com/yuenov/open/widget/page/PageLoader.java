package com.yuenov.open.widget.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.constant.ConstantPageInfo;
import com.yuenov.open.constant.ConstantSetting;
import com.yuenov.open.constant.stat.TurnPageType;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.database.tb.TbReadHistory;
import com.yuenov.open.interfaces.IDownloadContentListener;
import com.yuenov.open.interfaces.IDownloadMenuListListener;
import com.yuenov.open.model.PageInfoModel;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.model.standard.BookMenuItemInfo;
import com.yuenov.open.model.standard.DownloadBookContentItemInfo;
import com.yuenov.open.model.standard.ReadSettingInfo;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.Utility;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityMeasure;
import com.yuenov.open.utils.UtilityReadInfo;
import com.yuenov.open.utils.UtilityToasty;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BATTERY_SERVICE;

public class PageLoader {

    private BaseActivity activity;
    private Context mContext;
    // 页面显示类
    public PageView mPageView;

    // 阅读记录
    private TbReadHistory readHistory = null;

    // 书本基本信息
    protected BookBaseInfo bookBaseInfo;

    // 上一次翻页状态
    private TurnPageType lastTurnPageType = TurnPageType.NONE;
    // 共查看多少章
    private int showChapterCounts = 0;
    // 当前章节id
    private long chapterId;
    // 当前页码
    private int thisPage;
    // 当前页面
    private PageInfoModel mCurPage;

    // 当前章节信息
    private TbBookChapter mCurBookChapter;

    // 当前章节的页面列表
    private List<PageInfoModel> mCurPageList;

    // 一共有多少章
    private List<TbBookChapter> lisChapterId;

    // 画笔
    private Paint paint = new Paint();

    // 绘制电池的画笔
    private Paint mBatteryPaint;
    // 绘制时间的画笔
    private Paint mTimePaint;
    // 绘制进度的画笔
    private Paint mProcessPaint;

    // x轴基点
    private int x;
    // y轴基点
    private int y;

    //当前页面的背景
    private int mBgColor;

    // 控件是否准备就绪
    private boolean pageViewInitSuccess = false;
    // 数据是否准备就绪
    private boolean dataInitSuccess = false;
    // 数据是否加载成功
    private boolean initSuccess = false;

    private ReadSettingInfo thisSettingInfo;

    private Canvas canvas;

    private Thread threadUpdateBookShelf;
    private Thread threadGetChapterIdList1;
    private Thread threadGetChapterIdList2;
    private Thread threadAddReadHistory;
    private Thread threadUpdateReadPercentage;
    private Thread threadInitAutoDownLoad;
    private Thread threadSaveNowReadBook;

    private final int handlerWhat_UpdateChapterIds = 1;
    private final int handlerWhat_StartDownload = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                // 更新目录
                if (msg.what == handlerWhat_UpdateChapterIds) {
                    updateChapterIds();
                }
                // 下载章节
                else if (msg.what == handlerWhat_StartDownload) {
                    List<Long> lisWaitDownload = (List<Long>) msg.obj;
                    startDownLoad(lisWaitDownload);
                }
            } catch (Exception ex) {
                UtilityException.catchException(ex);
            }
        }
    };

    private IPagerLoader listener;

    private int mDisplayWidth;
    private int mDisplayHeight;

    private int visibleRight;
    private int visibleBottom;
    private int outFrameWidth;
    private int outFrameHeight;

    private int polarHeight;
    private int polarWidth;
    private int border = 1;
    private int innerMargin = 1;

    private int polarLeft;
    private int polarTop;
    private int polarRight;
    private int polarBottom;
    private Rect polar;

    //外框的制作
    private int outFrameLeft;
    private int outFrameTop;
    private int outFrameBottom;
    private Rect outFrame;

    //内框的制作
    private float innerWidth;
    private int innerLeft;
    private int innerTop;
    private int innerRight;
    private int innerBottom;
    private RectF innerFrame;

    private String time;
    private float timeX;
    private float timeY;

    private String percentage;
    private float processX;
    private float processY;

    private int battery = 100;

    /*****************************init params*******************************/
    public PageLoader(PageView pageView, BookBaseInfo bookBaseInfo, long chapterId) {
        this.mPageView = pageView;
        this.mContext = pageView.getContext();
        this.bookBaseInfo = bookBaseInfo;
        this.chapterId = chapterId;

        initPageView();
    }

    public long getChapterId() {
        return this.chapterId;
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public void setListener(IPagerLoader listener) {
        this.listener = listener;
    }

    /**
     * pageView控件加载完成
     */
    public void pageViewInitSuccess(int width, int height) {

        mDisplayWidth = width;
        mDisplayHeight = height;

        pageViewInitSuccess = true;

        init();
    }

    /**
     * 数据加载完成
     */
    public void dataInitSuccess() {
        dataInitSuccess = true;

        init();
    }

    /**
     * 更新书架上的最后阅读时间
     */
    private void updateBookShelf() {
        try {
            threadUpdateBookShelf = new Thread() {
                @Override
                public void run() {
                    AppDatabase.getInstance().BookShelfDao().updateHasUpdate(bookBaseInfo.bookId, false, System.currentTimeMillis());
                }
            };
            threadUpdateBookShelf.start();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void init() {

        if (!pageViewInitSuccess || !dataInitSuccess || initSuccess)
            return;

        // 更新书架上的最后阅读时间
        updateBookShelf();

        // 重置 PageMode
        mPageView.setPageMode(thisSettingInfo.pageAnimType);

        // 初始化电量和时间
        initBatteryAndTime();

        // 章节目录列表
        initChapterIds();

        // 初始化起始阅读位置
        initStartReadInfo();

        saveNowReadBook();

        mCurBookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, chapterId);

        // 没有内容去下载并打开
        if (mCurBookChapter == null || UtilitySecurity.isEmpty(mCurBookChapter.content)) {
            mPageView.drawCurPage(false);
            downloadAndOpenChapter(chapterId);
        } else {
            // 初始化阅读相关信息
            initReadInfo();

            // 绘制页面
            mPageView.drawCurPage(false);

            initSuccess = true;
        }
    }

    /**
     * 更新当前图书所有章节目录
     */
    private void initChapterIds() {

        threadGetChapterIdList1 = new Thread() {
            @Override
            public void run() {
                try {
                    lisChapterId = AppDatabase.getInstance().ChapterDao().getChapterList(bookBaseInfo.bookId);
                    handler.sendEmptyMessage(handlerWhat_UpdateChapterIds);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }
        };
        threadGetChapterIdList1.start();

    }

    private void updateChapterIds() {
        // 更新目录列表
        UtilityBusiness.updateChapterList(MyApplication.getAppContext(), bookBaseInfo.bookId, true, new IDownloadMenuListListener() {
            @Override
            public void onDownloadSuccess(List<BookMenuItemInfo> chapters) {

                if (UtilitySecurity.isEmpty(chapters)) {
                    return;
                }

                threadGetChapterIdList2 = new Thread() {
                    @Override
                    public void run() {

                        if (lisChapterId == null)
                            lisChapterId = new ArrayList<>();

                        TbBookChapter bookChapter;
                        for (int i = 0; i < chapters.size(); i++) {
                            bookChapter = new TbBookChapter();
                            bookChapter.chapterId = chapters.get(i).id;
                            bookChapter.chapterName = chapters.get(i).name;
                            lisChapterId.add(bookChapter);
                        }
                    }
                };
                threadGetChapterIdList2.start();
            }

            @Override
            public void onDownloadLoadFail(String s) {

            }
        });

    }

    /**
     * 初始化起始阅读位置
     */
    private void initStartReadInfo() {
        try {
            readHistory = AppDatabase.getInstance().ReadHistoryDao().getEntity(bookBaseInfo.bookId);

            // 有该阅读记录，从最后阅读位置开始
            if (chapterId > 0) {
                if (readHistory != null && readHistory.chapterId == chapterId) {
                    thisPage = readHistory.page;
                } else {
                    thisPage = 0;
                }
            }
            // 没有阅读记录
            // 未指定章节，从第一章第一页开始
            else {
                TbBookChapter tbBookChapter = AppDatabase.getInstance().ChapterDao().getFirstChapter(bookBaseInfo.bookId);
                if (tbBookChapter == null) {
                    UtilityToasty.error(R.string.Utility_unknown);
                } else {
                    chapterId = tbBookChapter.chapterId;
                    thisPage = 0;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 初始化电量，时间
     */
    private void initBatteryAndTime() {
        try {
            BatteryManager bm = (BatteryManager) activity.getSystemService(BATTERY_SERVICE);
            battery = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            time = UtilityData.getShowTimeText();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void initPageView() {
        try {
            thisSettingInfo = new ReadSettingInfo();
            thisSettingInfo.lightType = EditSharedPreferences.getReadSettingInfo().lightType;
            thisSettingInfo.lightValue = EditSharedPreferences.getReadSettingInfo().lightValue;
            thisSettingInfo.frontSize = EditSharedPreferences.getReadSettingInfo().frontSize;
            thisSettingInfo.frontColor = EditSharedPreferences.getReadSettingInfo().frontColor;
            thisSettingInfo.lineSpacingExtra = UtilityMeasure.getLineSpacingExtra(thisSettingInfo.frontSize);
            thisSettingInfo.pageAnimType = EditSharedPreferences.getReadSettingInfo().pageAnimType;

            // 背景色
            mBgColor = getBgColor(thisSettingInfo.lightType);
            mPageView.setBgColor(mBgColor);
            // 字体颜色
            paint.setColor(MyApplication.getAppContext().getResources().getColor(thisSettingInfo.frontColor));
            // 翻页动画
            mPageView.setPageMode(thisSettingInfo.pageAnimType);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 获取背景颜色
     */
    private int getBgColor(int lightType) {

        int color;

        switch (lightType) {
            case ConstantSetting.LIGHTTYPE_1:
                color = ContextCompat.getColor(mContext, R.color.bg1);
                break;

            case ConstantSetting.LIGHTTYPE_2:
                color = ContextCompat.getColor(mContext, R.color.bg2);
                break;

            case ConstantSetting.LIGHTTYPE_3:
                color = ContextCompat.getColor(mContext, R.color.bg3);
                break;

            case ConstantSetting.LIGHTTYPE_4:
                color = ContextCompat.getColor(mContext, R.color.bg4);
                break;

            case ConstantSetting.LIGHTTYPE_5:
                color = ContextCompat.getColor(mContext, R.color.bg5);
                break;

            default:
                color = ContextCompat.getColor(mContext, R.color.bg1);
                break;
        }

        return color;
    }

    /**
     * 重绘背景，字体颜色
     */
    public void resetBgColor(ReadSettingInfo newData) {
        try {
            mBgColor = getBgColor(newData.lightType);

            // 字体颜色
            if (newData.frontColor > 0)
                paint.setColor(MyApplication.getAppContext().getResources().getColor(newData.frontColor));

            // 绘制页面
            mPageView.drawCurPage(false);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 重设字体大小
     */
    public void resetFrontSize(ReadSettingInfo newData) {
        try {
            mCurPageList = UtilityMeasure.getPageInfos(mCurBookChapter, newData, mPageView);

            // 总页数小于最后阅读记录的页数(重新设置字体后)，直接看最后一页
            if (mCurPageList.size() - 1 < thisPage)
                thisPage = mCurPageList.size() - 1;

            // 当前页
            mCurPage = mCurPageList.get(thisPage);

            // 绘制页面
            mPageView.drawCurPage(false);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 重设翻页动画
     */
    public void resetPageMode(ReadSettingInfo newData) {
        try {
            thisSettingInfo.pageAnimType = newData.pageAnimType;
            mPageView.setPageMode(thisSettingInfo.pageAnimType);

            // 重新绘制当前页
            mPageView.drawCurPage(false);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public void drawPage(Bitmap bitmap, boolean isUpdate) {

        try {
            drawBackground(mPageView.getBgBitmap(), isUpdate);

            if (!isUpdate) {
                drawContent(bitmap);
            }

            //更新绘制
            mPageView.invalidate();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void drawBackground(Bitmap bitmap, boolean isUpdate) {
        try {
            canvas = new Canvas(bitmap);

            // 绘制背景
            if (!isUpdate) {
                canvas.drawColor(mBgColor);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 绘制内容
     */
    private void drawContent(Bitmap bitmap) {
        try {
            canvas = new Canvas(bitmap);

            if (thisSettingInfo.pageAnimType == PageMode.SCROLL) {
                canvas.drawColor(mBgColor);
            }

            if (mCurPage == null || UtilitySecurity.isEmpty(mCurPage.lisText))
                return;

            for (int i = 0; i < mCurPage.lisText.size(); i++) {

                paint.setTextSize(Utility.dip2px(mCurPage.lisText.get(i).textSize));
                paint.setFakeBoldText(mCurPage.lisText.get(i).fakeBoldText);
                paint.setAntiAlias(true);

                // 第一行：paddingTop + 文字高度
                if (i == 0) {
                    x = mPageView.getPaddingLeft();
                    y = mPageView.getPaddingTop() + mCurPage.lisText.get(i).height;
                }
                // 其他行：文字高度
                else {
                    y += mCurPage.lisText.get(i).height;
                }

                // 绘制
                if (!UtilitySecurity.isEmpty(mCurPage.lisText.get(i).text))
                    canvas.drawText(mCurPage.lisText.get(i).text, x, y, paint);
            }

            drawBattery();
            drawTime();
            drawProcess();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 绘制电量
     */
    private void drawBattery() {
        try {
            // 绘制电池的画笔
            if (mBatteryPaint == null) {
                mBatteryPaint = new Paint();
                mBatteryPaint.setAntiAlias(true);
                mBatteryPaint.setDither(true);

                visibleRight = mPageView.getPaddingLeft() + ScreenUtils.dpToPx(6);
                visibleBottom = mDisplayHeight - ScreenUtils.dpToPx(12);

                outFrameWidth = ScreenUtils.dpToPx(21);
                outFrameHeight = ScreenUtils.dpToPx(10);

                polarHeight = ScreenUtils.dpToPx(6);
                polarWidth = ScreenUtils.dpToPx(3);

                //电极的制作(最右面)
                polarLeft = visibleRight + outFrameWidth;
                polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
                polarRight = polarLeft + polarWidth;
                polarBottom = polarTop + polarHeight - ScreenUtils.dpToPx(2);
                polar = new Rect(polarLeft, polarTop, polarRight, polarBottom);

                //外框的制作
                outFrameLeft = polarLeft - outFrameWidth;
                outFrameTop = visibleBottom - outFrameHeight;
                outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
                outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);
            }

            // 电极
            mBatteryPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(polar, mBatteryPaint);
            // 外框
            mBatteryPaint.setStyle(Paint.Style.STROKE);
            mBatteryPaint.setStrokeWidth(border);
            canvas.drawRect(outFrame, mBatteryPaint);
            //内框的制作
            innerWidth = (outFrame.width() - innerMargin * 2 - border) * (battery / 100.0f);
            innerLeft = outFrameLeft + border + innerMargin;
            innerTop = outFrameTop + border + innerMargin;
            innerRight = (outFrameLeft + border + innerMargin + (int) innerWidth);
            innerBottom = outFrameBottom - border - innerMargin;
            innerFrame = new RectF(innerLeft, innerTop, innerRight, innerBottom);
            mBatteryPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(innerFrame, mBatteryPaint);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 绘制时间
     */
    private void drawTime() {

        if (UtilitySecurity.isEmpty(time))
            return;

        try {
            // 当前时间
            if (mTimePaint == null) {
                mTimePaint = new Paint();
                mTimePaint.setTextSize(Utility.dip2px(ConstantPageInfo.timeTextSize));
                mTimePaint.setAntiAlias(true);
                mTimePaint.setDither(true);

                timeX = polarRight + ScreenUtils.dpToPx(6);
                timeY = polarTop + ScreenUtils.dpToPx(5);
            }

            canvas.drawText(time, timeX, timeY, mTimePaint);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 绘制进度
     */
    private void drawProcess() {
        if (UtilitySecurity.isEmpty(percentage))
            return;

        try {
            // 当前时间
            if (mProcessPaint == null) {
                mProcessPaint = new Paint();
                mProcessPaint.setTextSize(Utility.dip2px(ConstantPageInfo.processTextSize));
                mProcessPaint.setAntiAlias(true);
                mProcessPaint.setDither(true);

                processX = mDisplayWidth - mPageView.getPaddingRight() - ScreenUtils.dpToPx(15) - ScreenUtils.dpToPx(12);
                processY = polarTop + ScreenUtils.dpToPx(4);
            }

            canvas.drawText(percentage, processX, processY, mProcessPaint);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 更新电量
     */
    public void updateBattery(int value) {
        battery = value;
        mPageView.drawCurPage(true);
    }

    /**
     * 更新时间
     */
    public void updateTime() {
        time = UtilityData.getShowTimeText();
        mPageView.drawCurPage(true);
    }

    /**
     * 下载并打开某章节
     */
    private void downloadAndOpenChapter(long chapterId) {

        List<Long> lisWaitDownload = new ArrayList<>();
        lisWaitDownload.add(chapterId);
        UtilityBusiness.downloadChapter(activity, bookBaseInfo.bookId, lisWaitDownload, new IDownloadContentListener() {
            @Override
            public void onDownloadSuccess(List<DownloadBookContentItemInfo> list) {
                if (UtilitySecurity.isEmpty(list)) {
                    UtilityToasty.error("数据错误~");
                    activity.finish();
                    return;
                }

                thisPage = 0;

                // 初始化阅读相关信息
                initReadInfo();

                // 绘制页面
                mPageView.drawCurPage(false);

                initSuccess = true;
            }

            @Override
            public void onDownloadLoadFail() {
                UtilityToasty.error("数据错误~");
            }
        });
    }

    /**
     * 自动下载前后章节, 下载完成后：如果前后章节都没有内容 自动加载
     */
    private void autoDownload() {

        if (threadInitAutoDownLoad != null && threadInitAutoDownLoad.getState() == Thread.State.RUNNABLE)
            return;

        threadInitAutoDownLoad = new Thread() {
            @Override
            public void run() {
                List<Long> lisWaitDownload = UtilityBusiness.getAutoDownLoadChapterId(mCurBookChapter);
                if (UtilitySecurity.isEmpty(lisWaitDownload))
                    return;

                Message msg = new Message();
                msg.obj = lisWaitDownload;
                msg.what = handlerWhat_StartDownload;
                handler.sendMessage(msg);
            }
        };
        threadInitAutoDownLoad.start();
    }

    private void startDownLoad(List<Long> lisWaitDownload) {

        if (activity.getStatusTip().isShowing())
            return;

        UtilityBusiness.startDownloadContent(activity, bookBaseInfo.bookId, lisWaitDownload, true, false, new IDownloadContentListener() {
            @Override
            public void onDownloadSuccess(List<DownloadBookContentItemInfo> list) {
            }

            @Override
            public void onDownloadLoadFail() {

            }
        });
    }

    private void downloadAndAutoPage(long downloadChapterId, TurnPageType pageType) {
        if (downloadChapterId < 1)
            return;

        if (UtilitySecurity.isEmpty(lisChapterId))
            return;

        if (activity.getStatusTip().isShowing())
            return;

        List<Long> lisWaitDownload = new ArrayList<>();
        lisWaitDownload.add(downloadChapterId);

        activity.getStatusTip().showProgress();
        UtilityBusiness.startDownloadContent(activity, bookBaseInfo.bookId, lisWaitDownload, true, true, new IDownloadContentListener() {
            @Override
            public void onDownloadSuccess(List<DownloadBookContentItemInfo> list) {

                activity.getStatusTip().hideProgress();

                try {
                    lastTurnPageType = pageType;

                    // 自动翻页
                    if (pageType == TurnPageType.PRE) {
                        mPageView.autoPrevPage();
                    } else if (pageType == TurnPageType.NEXT) {
                        mPageView.autoNextPage();
                    }
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onDownloadLoadFail() {
                activity.getStatusTip().hideProgress();
            }
        });
    }

    /**
     * 打开指定章节
     */
    public void openChapter(long toChapterId) {
        this.chapterId = toChapterId;

        try {
            mCurBookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, this.chapterId);
            if (mCurBookChapter == null || UtilitySecurity.isEmpty(mCurBookChapter.content)) {
                downloadAndOpenChapter(this.chapterId);
            } else {
                this.thisPage = 0;

                // 初始化阅读相关信息
                initReadInfo();

                // 绘制页面
                mPageView.drawCurPage(false);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 初始化阅读相关信息
     */
    private void initReadInfo() {

        try {
            // 记录查看新章节
            if (thisPage == 0)
                showNewChapter();

            // 当前章
            if (mCurBookChapter == null || mCurBookChapter.chapterId != chapterId || UtilitySecurity.isEmpty(mCurBookChapter.content))
                mCurBookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, chapterId);
            mCurPageList = UtilityMeasure.getPageInfos(mCurBookChapter, UtilityReadInfo.getReadSettingInfo(), mPageView);
            // 当前页
            mCurPage = mCurPageList.get(thisPage);

            // 添加阅读记录
            addReadHistory();

            // 更新进度
            updateReadPercentage();

            // 自动下载前后章节
            autoDownload();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 翻阅上一页
     */
    public boolean prePage(boolean execute) {

        try {
            // 章内上一页
            if (thisPage > 0) {

                if (execute) {
                    thisPage--;
                    mCurPage = mCurPageList.get(thisPage);
                    lastTurnPageType = TurnPageType.PRE;
                    updateReadPercentage();
                    addReadHistory();

                    mPageView.drawNextPage();
                }
                return true;
            } else {
                long preChapterId = getPreChapterId(mCurBookChapter.chapterId);

                // 没有上一章了
                if (preChapterId == 0)
                    return false;

                TbBookChapter preBookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, preChapterId);
                if (preBookChapter == null) {
                    return false;
                }
                // 有章节没内容，去下载
                else if (UtilitySecurity.isEmpty(preBookChapter.content)) {
                    downloadAndAutoPage(preChapterId, TurnPageType.PRE);
                    return false;
                } else {
                    if (execute) {
                        mCurBookChapter = preBookChapter;
                        lastTurnPageType = TurnPageType.PRE;
                        chapterId = mCurBookChapter.chapterId;
                        mCurPageList = UtilityMeasure.getPageInfos(mCurBookChapter, UtilityReadInfo.getReadSettingInfo(), mPageView);
                        thisPage = mCurPageList.size() - 1;
                        mCurPage = mCurPageList.get(thisPage);

                        if (listener != null)
                            listener.onPreChapter(mCurBookChapter);

                        autoDownload();
                        updateReadPercentage();
                        addReadHistory();

                        mPageView.drawNextPage();
                    }
                    return true;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            return false;
        }
    }

    /**
     * 翻到下一页
     */
    public boolean nextPage(boolean execute) {
        try {
            // 当前章节正常翻页
            if (thisPage < mCurPageList.size() - 1) {

                if (execute) {
                    thisPage++;
                    mCurPage = mCurPageList.get(thisPage);

                    updateReadPercentage();
                    addReadHistory();
                    lastTurnPageType = TurnPageType.NEXT;

                    mPageView.drawNextPage();
                }
                return true;
            }
            // 下一章
            else {
                long nextChapterId = getNextChapterId(mCurBookChapter.chapterId);

                // 没有下一章了
                if (nextChapterId == 0)
                    return false;

                TbBookChapter bookChapterNext = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, nextChapterId);
                if (bookChapterNext == null) {
                    return false;
                }
                // 有章节 没有内容，去下载
                else if (UtilitySecurity.isEmpty(bookChapterNext.content)) {
                    downloadAndAutoPage(nextChapterId, TurnPageType.NEXT);
                    return false;
                } else {
                    if (execute) {
                        lastTurnPageType = TurnPageType.NEXT;
                        mCurBookChapter = bookChapterNext;
                        chapterId = mCurBookChapter.chapterId;
                        mCurPageList = UtilityMeasure.getPageInfos(mCurBookChapter, UtilityReadInfo.getReadSettingInfo(), mPageView);
                        thisPage = 0;
                        mCurPage = mCurPageList.get(thisPage);

                        if (listener != null)
                            listener.onNextChapter(mCurBookChapter);

                        autoDownload();
                        updateReadPercentage();
                        addReadHistory();

                        mPageView.drawNextPage();
                    }
                    return true;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            return false;
        }
    }

    public boolean isRequesting() {
        return activity.getStatusTip().isShowing();
    }

    /**
     * 记录最后阅读的书
     */
    private void saveNowReadBook() {
        threadSaveNowReadBook = new Thread() {
            @Override
            public void run() {
                EditSharedPreferences.setNowReadBook(bookBaseInfo);
            }
        };
        threadSaveNowReadBook.start();
    }

    private long getPreChapterId(long thisChapterId) {
        long preChapterId = 0;

        try {
            for (int i = 0; i < lisChapterId.size(); i++) {
                if (lisChapterId.get(i).chapterId == thisChapterId) {
                    if (i == 0)
                        preChapterId = 0;
                    else
                        preChapterId = lisChapterId.get(i - 1).chapterId;
                    break;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return preChapterId;
    }

    private long getNextChapterId(long thisChapterId) {
        long nextChapterId = 0;

        try {
            for (int i = 0; i < lisChapterId.size(); i++) {
                if (lisChapterId.get(i).chapterId == thisChapterId) {
                    if (i == lisChapterId.size() - 1)
                        nextChapterId = 0;
                    else
                        nextChapterId = lisChapterId.get(i + 1).chapterId;
                    break;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return nextChapterId;
    }

    /**
     * 取消翻页
     */
    public void pageCancel() {

        try {
            // 取消上一页
            if (lastTurnPageType == TurnPageType.PRE) {

                if (thisPage + 1 >= mCurPageList.size()) {

                    long nextChapterId = getNextChapterId(mCurBookChapter.chapterId);
                    if (nextChapterId == 0)
                        return;
                    mCurBookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, nextChapterId);
                    chapterId = mCurBookChapter.chapterId;
                    mCurPageList = UtilityMeasure.getPageInfos(mCurBookChapter, UtilityReadInfo.getReadSettingInfo(), mPageView);
                    thisPage = 0;
                    mCurPage = mCurPageList.get(thisPage);
                } else {
                    thisPage++;
                    mCurPage = mCurPageList.get(thisPage);
                }
            }
            // 取消下一页
            else if (lastTurnPageType == TurnPageType.NEXT) {
                if (thisPage <= 0) {
                    long preChapterId = getPreChapterId(mCurBookChapter.chapterId);
                    if (preChapterId == 0)
                        return;
                    mCurBookChapter = AppDatabase.getInstance().ChapterDao().getEntity(bookBaseInfo.bookId, preChapterId);
                    chapterId = mCurBookChapter.chapterId;
                    mCurPageList = UtilityMeasure.getPageInfos(mCurBookChapter, UtilityReadInfo.getReadSettingInfo(), mPageView);
                    thisPage = mCurPageList.size() - 1;
                    mCurPage = mCurPageList.get(thisPage);
                } else {
                    thisPage--;
                    mCurPage = mCurPageList.get(thisPage);
                }
            }

            // 更新阅读率
            updateReadPercentage();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 确定翻页
     */
    public void turnPage() {
        if (thisPage == 0)
            showNewChapter();

        if (listener != null)
            listener.onTurnPage();
    }

    /**
     * 每查看10章弹一次广告
     */
    private void showNewChapter() {
        showChapterCounts++;

        if (listener != null && showChapterCounts % ConstantSetting.adShowChapterCounts == 0)
            listener.showAd();
    }

    /**
     * 增加最后阅读记录
     */
    private void addReadHistory() {
        try {
            threadAddReadHistory = new Thread() {
                @Override
                public void run() {

                    try {
                        if (readHistory == null)
                            readHistory = new TbReadHistory();

                        readHistory.bookId = bookBaseInfo.bookId;
                        readHistory.title = bookBaseInfo.title;
                        readHistory.chapterId = chapterId;
                        readHistory.page = thisPage;
                        readHistory.author = bookBaseInfo.author;
                        readHistory.coverImg = bookBaseInfo.coverImg;
                        readHistory.addBookShelf = AppDatabase.getInstance().BookShelfDao().exists(bookBaseInfo.bookId);
                        readHistory.lastReadTime = System.currentTimeMillis();
                        AppDatabase.getInstance().ReadHistoryDao().addOrUpdateByReadDetail(readHistory);
                    } catch (Exception ex) {
                        UtilityException.catchException(ex);
                    }
                }
            };
            threadAddReadHistory.start();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 更新阅读率
     */
    private void updateReadPercentage() {
        try {
            threadUpdateReadPercentage = new Thread() {
                @Override
                public void run() {

                    if (UtilitySecurity.isEmpty(lisChapterId)
                            || UtilitySecurity.isEmpty(mCurPageList)
                            || mCurBookChapter == null)
                        return;

                    try {
                        int allPages = lisChapterId.size() * mCurPageList.size();
                        int currentPage;
                        for (int i = 0; i < lisChapterId.size(); i++) {
                            if (lisChapterId.get(i).chapterId == mCurBookChapter.chapterId) {
                                currentPage = (i * mCurPageList.size()) + (thisPage + 1);
                                percentage = UtilityData.getPercent(currentPage, allPages) + "%";
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        UtilityException.catchException(ex);
                    }
                }
            };
            threadUpdateReadPercentage.start();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 关闭书本
     */
    public void closeBook() {

        try {
            if (threadUpdateBookShelf != null) {
                threadUpdateBookShelf.interrupt();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        try {
            if (threadGetChapterIdList1 != null) {
                threadGetChapterIdList1.interrupt();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        try {
            if (threadGetChapterIdList2 != null) {
                threadGetChapterIdList2.interrupt();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        try {
            if (threadAddReadHistory != null) {
                threadAddReadHistory.interrupt();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        try {
            if (threadUpdateReadPercentage != null) {
                threadUpdateReadPercentage.interrupt();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        try {
            if (threadInitAutoDownLoad != null) {
                threadInitAutoDownLoad.interrupt();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        try {
            if (threadSaveNowReadBook != null) {
                threadSaveNowReadBook.interrupt();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}