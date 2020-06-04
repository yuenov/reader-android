package com.yuenov.open.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.model.httpModel.ChapterUpdateForceHttpModel;
import com.yuenov.open.model.responseModel.DownloadListResponse;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.model.standard.ChapterUpdateForceInfo;
import com.yuenov.open.model.standard.ReadSettingInfo;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.UtilityBrightness;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.yuenov.open.widget.DetailOperationView;
import com.yuenov.open.widget.page.IPagerLoader;
import com.yuenov.open.widget.page.PageLoader;
import com.yuenov.open.widget.page.PageView;
import com.yuenov.open.widget.page.ScreenUtils;
import com.yuenov.open.widget.page.SystemBarUtils;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.Logger;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import java.util.ArrayList;

import butterknife.BindView;

import static android.view.View.LAYER_TYPE_SOFTWARE;

public class ReadDetailActivity extends BaseActivity implements DetailOperationView.IDetailOperationView,
        View.OnClickListener, IPagerLoader {

    private static final String EXTRA_MODEL_BOOKBASEINFO = "BookBaseInfo";
    private BookBaseInfo bookBaseInfo;

    private static final String EXTRA_LONG_CHAPTERID = "chapterId";
    private long chapterId;

    public static Intent getIntent(Context context, BookBaseInfo bookBaseInfo, long chapterId) {
        Intent intent = new Intent(context, ReadDetailActivity.class);
        if (bookBaseInfo != null)
            intent.putExtra(EXTRA_MODEL_BOOKBASEINFO, bookBaseInfo);
        intent.putExtra(EXTRA_LONG_CHAPTERID, chapterId);
        return intent;
    }

    public static Intent getIntent(Context context, BookBaseInfo bookBaseInfo) {
        return getIntent(context, bookBaseInfo, 0);
    }

    @BindView(R.id.viewCiTop)
    protected View viewCiTop;
    @BindView(R.id.tvDiDownLoad)
    protected ImageView tvDiDownLoad;
    @BindView(R.id.tvDiUpdateContent)
    protected ImageView tvDiUpdateContent;
    @BindView(R.id.tvDiAddShelf)
    protected ImageView tvDiAddShelf;

    @BindView(R.id.pvDiContent)
    protected PageView pvDiContent;

    @BindView(R.id.rlDiTop)
    protected RelativeLayout rlDiTop;
    @BindView(R.id.tvDiLeft)
    protected TextView tvDiLeft;

    @BindView(R.id.dovDiOperation)
    protected DetailOperationView dovDiOperation;

    private PageLoader mPageLoader;

    private Animation showAnimation;
    private Animation hideAnimation;

    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                if (mPageLoader != null)
                    mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                if (mPageLoader != null)
                    mPageLoader.updateTime();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_readdetail;
    }

    @Override
    protected void initExtra() {
        bookBaseInfo = UtilitySecurity.getExtrasSerializable(getIntent(), EXTRA_MODEL_BOOKBASEINFO);
        chapterId = UtilitySecurity.getExtrasLong(getIntent(), EXTRA_LONG_CHAPTERID, 0);

        if (bookBaseInfo == null || bookBaseInfo.bookId < 1) {
            UtilityToasty.error(R.string.Utility_unknown);
            finish();
        }
    }

    @Override
    protected void initListener() {

        UtilitySecurityListener.setOnClickListener(this, rlDiTop, tvDiLeft,  tvDiDownLoad, tvDiUpdateContent, tvDiAddShelf);

        dovDiOperation.setListener(this);

        pvDiContent.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                if (getStatusTip().isShowing())
                    return false;

                if (rlDiTop.getVisibility() == View.VISIBLE) {
                    hideOperation();
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void center() {
                showOrHideOperation();
            }

            @Override
            public boolean allowPrePage() {
                if (rlDiTop.getVisibility() == View.VISIBLE) {
                    hideOperation();
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void prePage() {
                Logger.firstE("prePage");
            }

            @Override
            public boolean allowNextPage() {
                if (rlDiTop.getVisibility() == View.VISIBLE) {
                    hideOperation();
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void nextPage() {
                Logger.firstE("nextPage");
            }

            @Override
            public void cancel() {
            }
        });
    }

    @Override
    protected void initData() {

        initDefaultStyle();

        initPageLoader();

        registerReceiver();
    }

    private void initDefaultStyle() {

        SystemBarUtils.hideStableStatusBar(this);

        if (Build.VERSION.SDK_INT >= 19) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlDiTop.getLayoutParams();
            params.setMargins(0, ScreenUtils.getStatusBarHeight(), 0, 0);
            rlDiTop.setLayoutParams(params);
        }

        // 屏幕亮度
        if (EditSharedPreferences.getReadSettingInfo().lightValue > 0)
            UtilityBrightness.setAppScreenBrightness(this, EditSharedPreferences.getReadSettingInfo().lightValue);

        // 未加入书架  展示加入到书架的按钮
        boolean existsBookShelf = AppDatabase.getInstance().BookShelfDao().exists(bookBaseInfo.bookId);
        UtilitySecurity.resetVisibility(tvDiAddShelf, !existsBookShelf);

        // 底部操作栏
        dovDiOperation.setActivity(this);
        dovDiOperation.setData(bookBaseInfo.title, bookBaseInfo.bookId);
    }

    private void initPageLoader() {
        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            pvDiContent.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        mPageLoader = pvDiContent.getPageLoader(bookBaseInfo, chapterId);
        mPageLoader.setActivity(this);
        mPageLoader.setListener(this);
        mPageLoader.dataInitSuccess();
    }

    /**
     * 注册电量变化和 分钟变更的广播
     */
    private void registerReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(mReceiver, intentFilter);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void showAnimation(int animResourceId, View view) {
        if (showAnimation != null)
            showAnimation.cancel();

        showAnimation = AnimationUtils.loadAnimation(this, animResourceId);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SystemBarUtils.showUnStableStatusBar(ReadDetailActivity.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        UtilitySecurity.resetVisibility(view, true);
        view.startAnimation(showAnimation);
    }

    private void hideAnimation(int animResourceId, View view) {
        if (hideAnimation != null)
            hideAnimation.cancel();

        hideAnimation = AnimationUtils.loadAnimation(this, animResourceId);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SystemBarUtils.hideStableStatusBar(ReadDetailActivity.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        UtilitySecurity.resetVisibility(view, false);
        view.startAnimation(hideAnimation);
    }

    /**
     * 隐藏操作菜单
     */
    private void hideOperation() {
        try {
            dovDiOperation.hideAllMenuContent();
            hideAnimation(R.anim.slide_top_out, rlDiTop);
            hideAnimation(R.anim.slide_bottom_out, dovDiOperation);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 展示或隐藏操作菜单
     */
    private void showOrHideOperation() {
        if (rlDiTop.getVisibility() == View.VISIBLE) {
            // 已展开menu，先关闭menu
            if (dovDiOperation.isShowMenuContent()) {
                dovDiOperation.hideAllMenuContent();
            } else {
                hideOperation();
            }
        } else {
            dovDiOperation.initMenuList();
            showAnimation(R.anim.slide_top_in, rlDiTop);
            showAnimation(R.anim.slide_bottom_in, dovDiOperation);
        }
    }

    /**
     * 添加到书架
     */
    private void addBookShelf() {
        UtilityBusiness.addBookShelf(bookBaseInfo);
        UtilitySecurity.resetVisibility(tvDiAddShelf, false);

        UtilityToasty.success(R.string.info_addBookShelf_success);
    }

    /**
     * 更新本章节内容
     */
    private void updateContent() {
        if (bookBaseInfo == null || mPageLoader.getChapterId() < 1)
            return;

        hideOperation();

        ChapterUpdateForceInfo updateForceInfo = new ChapterUpdateForceInfo();
        updateForceInfo.bookId = bookBaseInfo.bookId;
        updateForceInfo.chapterIdList = new ArrayList<>();
        updateForceInfo.chapterIdList.add(mPageLoader.getChapterId());

        ChapterUpdateForceHttpModel httpModel = new ChapterUpdateForceHttpModel();
        httpModel.setIsPostJson(true);
        httpModel.setPostJsonText(mHttpClient.GetGsonInstance().toJson(updateForceInfo));
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                getStatusTip().showProgress("正在刷新章节...");
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, false)) {
                    return;
                }

                DownloadListResponse downloadList = null;
                try {
                    downloadList = mHttpClient.fromDataJson(s, DownloadListResponse.class);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }

                if (downloadList == null)
                    return;

                try {
                    // 更新数据库
                    AppDatabase.getInstance().ChapterDao().addContent(bookBaseInfo.bookId, downloadList.list);

                    // 打开该章
                    mPageLoader.openChapter(mPageLoader.getChapterId());

                    UtilityToasty.success("刷新成功");
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {
            }

            @Override
            public void onFinish() {
                getStatusTip().hideProgress();
            }
        });
    }

    /**
     * 查看上一章，自动下载前后章节
     */
    @Override
    public void onPreChapter(TbBookChapter newBookChapter) {
    }

    /**
     * 查看下一章，自动下载前后章节
     */
    @Override
    public void onNextChapter(TbBookChapter newBookChapter) {
    }

    @Override
    public void onTurnPage() {
    }

    @Override
    public void showAd() {
    }

    @Override
    public void onDetailOperationViewChange(ReadSettingInfo newData) {
        if (newData == null)
            return;

        try {
            // 改变背景
            if (EditSharedPreferences.getReadSettingInfo().lightType != newData.lightType) {
                mPageLoader.resetBgColor(newData);
            }
            // 改变字体大小
            else if (EditSharedPreferences.getReadSettingInfo().frontSize != newData.frontSize) {
                mPageLoader.resetFrontSize(newData);
            }
            // 改变动画类型
            else if (EditSharedPreferences.getReadSettingInfo().pageAnimType != newData.pageAnimType) {
                mPageLoader.resetPageMode(newData);
                hideOperation();
            }
            // 改变亮度
            else if (EditSharedPreferences.getReadSettingInfo().lightValue != newData.lightValue) {
                UtilityBrightness.setAppScreenBrightness(this, newData.lightValue);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onSelectChapter(TbBookChapter chapter) {
        if (chapter == null || chapter.chapterId < 1)
            return;

        hideOperation();

        // 打开该章
        chapterId = chapter.chapterId;
        mPageLoader.openChapter(chapterId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            SystemBarUtils.hideStableStatusBar(ReadDetailActivity.this);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
        mPageLoader.closeBook();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getStatusTip().isShowing()) {
            getStatusTip().hideProgress();
            mHttpClient.cancelRequests(this);
        }

        // 正常退出清掉最后阅读的图书
        EditSharedPreferences.clearNowReadBook();

        if (dovDiOperation != null)
            dovDiOperation.close();

        if (mPageLoader != null)
            mPageLoader.closeBook();
    }

    @Override
    public void onClick(View view) {
        if (getStatusTip().isShowing())
            return;

        switch (view.getId()) {
            case R.id.rlDiTop:
                if (rlDiTop.getVisibility() == View.VISIBLE)
                    dovDiOperation.hideAllMenuContent();
                break;

            // 返回
            case R.id.tvDiLeft:
                UtilitySecurity.resetVisibility(rlDiTop, false);
                onBackPressed();
                break;

            // 加入到书架
            case R.id.tvDiAddShelf:
                addBookShelf();
                break;

            // 重刷章节
            case R.id.tvDiUpdateContent:
                updateContent();
                break;

            // 下载
            case R.id.tvDiDownLoad:
                UtilityBusiness.toDownloadMenuList(this, bookBaseInfo.bookId);
                break;
        }
    }
}