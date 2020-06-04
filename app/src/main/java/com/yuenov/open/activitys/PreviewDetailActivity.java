package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.BookPreviewRecommendAdapter;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.database.tb.TbReadHistory;
import com.yuenov.open.interfaces.IDownloadMenuListListener;
import com.yuenov.open.interfaces.IGetCategoryListListener;
import com.yuenov.open.model.httpModel.BookPreviewHttpModel;
import com.yuenov.open.model.standard.BookMenuItemInfo;
import com.yuenov.open.model.standard.BookPreviewInfo;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.utils.UtilityBlur;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.yuenov.open.utils.images.UtilityImage;
import com.yuenov.open.widget.WrapHeightGridView;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import java.util.List;

import butterknife.BindView;

/**
 * 详情页
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PreviewDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnScrollChangeListener {

    private static final String EXTRA_INT_BOOKID = "bookId";
    private int bookId;

    public static Intent getIntent(Context context, int bookId) {
        Intent intent = new Intent(context, PreviewDetailActivity.class);
        intent.putExtra(EXTRA_INT_BOOKID, bookId);
        return intent;
    }

    @BindView(R.id.rlDpContent)
    protected RelativeLayout rlDpContent;
    @BindView(R.id.llDpTop)
    protected LinearLayout llDpTop;
    @BindView(R.id.llDpBack)
    protected LinearLayout llDpBack;

    @BindView(R.id.svDpContent)
    protected ScrollView svDpContent;
    @BindView(R.id.ivDpBgBlur)
    protected ImageView ivDpBgBlur;
    @BindView(R.id.rivDpCoverImg)
    protected com.makeramen.roundedimageview.RoundedImageView rivDpCoverImg;
    @BindView(R.id.tvDpTitle)
    protected TextView tvDpTitle;
    @BindView(R.id.tvDpAuthor)
    protected TextView tvDpAuthor;
    @BindView(R.id.tvDpCategory)
    protected TextView tvDpCategory;

    @BindView(R.id.tvDpDesc)
    protected TextView tvDpDesc;

    @BindView(R.id.tvDpChapterName)
    protected TextView tvDpChapterName;

    @BindView(R.id.tvDpIsEnd)
    protected TextView tvDpIsEnd;
    @BindView(R.id.tvDpMenuTotal)
    protected TextView tvDpMenuTotal;

    @BindView(R.id.llDpMenu)
    protected LinearLayout llDpMenu;

    @BindView(R.id.llDpRecommend)
    protected LinearLayout llDpRecommend;

    @BindView(R.id.wgvDpRecommend)
    protected WrapHeightGridView wgvDpRecommend;

    @BindView(R.id.tvDpRecommendMore)
    protected TextView tvDpRecommendMore;
    @BindView(R.id.llDpReplace)
    protected LinearLayout llDpReplace;
    @BindView(R.id.tvDpReplace)
    protected TextView tvDpReplace;
    @BindView(R.id.ilDpReplace)
    protected ImageView ilDpReplace;

    @BindView(R.id.llDpDownload)
    protected LinearLayout llDpDownload;

    @BindView(R.id.tvDpRead)
    protected TextView tvDpRead;

    @BindView(R.id.llDpAddBookShelf)
    protected LinearLayout llDpAddBookShelf;
    @BindView(R.id.ivDpAddBookShelf)
    protected ImageView ivDpAddBookShelf;
    @BindView(R.id.tvDpAddBookShelf)
    protected TextView tvDpAddBookShelf;

    private BookPreviewInfo res;
    private int recommendPage = 1;
    private BookPreviewRecommendAdapter recommendAdapter;

    private boolean hasBookShelf;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detailpreview;
    }

    @Override
    protected void initExtra() {
        bookId = UtilitySecurity.getExtrasInt(getIntent(), EXTRA_INT_BOOKID);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, llDpBack, llDpMenu, tvDpMenuTotal, llDpDownload, llDpDownload, tvDpRead, llDpAddBookShelf);
        UtilitySecurityListener.setOnClickListener(this, tvDpChapterName);
        UtilitySecurityListener.setOnClickListener(this, tvDpRecommendMore, llDpReplace, tvDpReplace, ilDpReplace);
        UtilitySecurityListener.setOnClickListener(this, ivDpAddBookShelf, tvDpAddBookShelf);
        svDpContent.setOnScrollChangeListener(this);
        wgvDpRecommend.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        BookPreviewHttpModel httpModel = new BookPreviewHttpModel();
        httpModel.bookId = bookId;
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                getPubLoadingView().show();
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    return;
                }

                try {
                    setResponse(s);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {
                UtilityToasty.error(s);
                finish();
            }

            @Override
            public void onFinish() {
                getPubLoadingView().hide();
            }
        });
    }

    private void setResponse(String s) {
        try {
            res = mHttpClient.fromDataJson(s, BookPreviewInfo.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        if (res == null) {
            UtilityToasty.error(R.string.info_loaddata_error);
            finish();
            return;
        }

        UtilityBlur.blur(ivDpBgBlur,UtilityImage.getImageUrl(res.coverImg));
        UtilityImage.setImage(rivDpCoverImg, res.coverImg, R.mipmap.ic_book_list_default);
        UtilitySecurity.setText(tvDpTitle, res.title);
        UtilitySecurity.setText(tvDpAuthor, res.author);
        UtilitySecurity.setText(tvDpCategory, res.categoryName + " " + res.word);
        UtilitySecurity.setText(tvDpDesc, UtilityData.deleteStartAndEndNewLine(res.desc));

        // 最后章节名称，是否完结
        if (res.update != null) {
            UtilitySecurity.setText(tvDpChapterName, res.update.chapterName);

            if (UtilityData.isSerialize(res.update.chapterStatus)) {
                UtilitySecurity.setTextEmptyIsGone(tvDpIsEnd, UtilityData.getDiffTimeText(res.update.time));
            } else {
                UtilitySecurity.setTextEmptyIsGone(tvDpIsEnd, getString(R.string.ChapterStatus_wanjie));
            }
        }

        // 共多少章
        UtilitySecurity.setText(tvDpMenuTotal, getString(R.string.DetailPreviewActivity_Chapter, res.chapterNum));
        // 推荐数据
        recommendAdapter = new BookPreviewRecommendAdapter(res.recommend);
        wgvDpRecommend.setAdapter(recommendAdapter);

        // 有阅读记录继续阅读，没有的话开始阅读
        boolean hasReadHistory = AppDatabase.getInstance().ReadHistoryDao().existsRealRead(bookId);
        String readText = getString(hasReadHistory ? R.string.DetailPreviewActivity_continueRead : R.string.DetailPreviewActivity_startRead);
        UtilitySecurity.setText(tvDpRead, readText);

        // 是否在书架
        hasBookShelf = AppDatabase.getInstance().BookShelfDao().exists(bookId);
        refBookShelfStat();

        // 更新章节目录
        updateChapter();

        // 添加阅读记录
        addReadHistory();

        UtilitySecurity.resetVisibility(rlDpContent, true);
    }

    private void toChapterMenuList() {
        if (res == null)
            return;

        UtilityBusiness.updateChapterList(this, bookId, true, new IDownloadMenuListListener() {
            @Override
            public void onDownloadSuccess(List<BookMenuItemInfo> chapters) {

                try {
                    BookBaseInfo bookBaseInfo = new BookBaseInfo();
                    bookBaseInfo.bookId = res.bookId;
                    bookBaseInfo.title = res.title;
                    bookBaseInfo.author = res.author;
                    bookBaseInfo.coverImg = res.coverImg;

                    if (res.update != null)
                        bookBaseInfo.chapterStatus = res.update.chapterStatus;

                    Intent intent = ChapterMenuListActivity.getIntent(PreviewDetailActivity.this, bookBaseInfo);
                    startActivity(intent);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onDownloadLoadFail(String s) {

            }
        });
    }

    /**
     * 更多热门推荐
     */
    protected void toMoreRecommend() {
        try {
            Intent intent = BookRecommendMoreActivity.getIntent(this, "热门推荐", bookId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 开始或继续阅读
     */
    private void toRead() {
        try {
            BookBaseInfo bookBaseInfo = new BookBaseInfo();
            bookBaseInfo.bookId = res.bookId;
            bookBaseInfo.title = res.title;
            bookBaseInfo.author = res.author;
            bookBaseInfo.coverImg = res.coverImg;
            UtilityBusiness.toRead(this, bookBaseInfo);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 去阅读最新章节
     */
    private void toUpdateRead() {
        if (res == null || res.update == null || res.update.chapterId < 1)
            return;

        try {
            BookBaseInfo bookBaseInfo = new BookBaseInfo();
            bookBaseInfo.bookId = res.bookId;
            bookBaseInfo.title = res.title;
            bookBaseInfo.author = res.author;
            bookBaseInfo.coverImg = res.coverImg;
            UtilityBusiness.toRead(this, bookBaseInfo, res.update.chapterId);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 刷新书架状态
     */
    private void refBookShelfStat() {
        if (hasBookShelf) {
            UtilitySecurity.setImageResource(ivDpAddBookShelf, R.mipmap.ic_remove_bookshelf);
            UtilitySecurity.setText(tvDpAddBookShelf, getString(R.string.DetailPreviewActivity_removeBookShelf));
            UtilitySecurity.setTextColor(tvDpAddBookShelf, R.color.gary_c5c7);
        } else {
            UtilitySecurity.setImageResource(ivDpAddBookShelf, R.mipmap.ic_add_bookshelf);
            UtilitySecurity.setText(tvDpAddBookShelf, getString(R.string.DetailPreviewActivity_addBookShelf));
            UtilitySecurity.setTextColor(tvDpAddBookShelf, R.color.blue_b383);
        }
    }

    /**
     * 添加或移除书架
     */
    private void addOrRemoveShelf() {
        try {
            if (hasBookShelf) {
                UtilityBusiness.removeBookShelf(bookId);
                hasBookShelf = false;
            } else {
                BookBaseInfo bookBaseInfo = new BookBaseInfo();
                bookBaseInfo.bookId = bookId;
                bookBaseInfo.title = res.title;
                bookBaseInfo.author = res.author;
                bookBaseInfo.coverImg = res.coverImg;
                UtilityBusiness.addBookShelf(bookBaseInfo);
                hasBookShelf = true;
            }

            refBookShelfStat();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 换一批推荐数据
     */
    private void replaceRecommend() {
        int pageSize = res != null && !UtilitySecurity.isEmpty(res.recommend) ? res.recommend.size() : 3;
        UtilityBusiness.getPreviewBooks(this, bookId, pageSize, recommendPage + 1, new IGetCategoryListListener() {
            @Override
            public void onGetCategoryListSuccess(List<CategoriesListItem> list) {
                if (UtilitySecurity.isEmpty(list))
                    return;

                try {
                    res.recommend = list;

                    recommendPage++;
                    recommendAdapter.setData(res.recommend);
                    recommendAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onGetCategoryListLoadFail() {

            }
        });
    }

    /**
     * 更新章节目录 (只有本地有章节信息的图书 才更新)
     */
    private void updateChapter() {
        try {
            TbBookChapter bookChapter = AppDatabase.getInstance().ChapterDao().getFirstChapter(bookId);
            if (bookChapter != null)
                UtilityBusiness.updateChapterList(this, bookId, false, null);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void addReadHistory() {
        if (getStatusTip().isShowing())
            return;

        try {
            TbReadHistory readHistory = new TbReadHistory();
            readHistory.bookId = res.bookId;
            readHistory.title = res.title;
            readHistory.author = res.author;
            readHistory.coverImg = res.coverImg;
            readHistory.addBookShelf = AppDatabase.getInstance().BookShelfDao().exists(res.bookId);
            readHistory.lastReadTime = System.currentTimeMillis();
            AppDatabase.getInstance().ReadHistoryDao().addOrUpdateByPreview(readHistory);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            if (getStatusTip().isShowing())
                return;

            Intent intent = PreviewDetailActivity.getIntent(this, res.recommend.get(position).bookId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        if (v.getId() != R.id.svDpContent)
            return;

        if (llDpTop.getMeasuredHeight() < 1)
            return;

        try {
            if (scrollY > llDpTop.getMeasuredHeight()) {
                if (llDpTop.getVisibility() != View.VISIBLE) {
                    UtilitySecurity.resetVisibility(llDpTop, true);
                    llDpTop.setAlpha(0f);
                    llDpTop.animate().alpha(1f).setDuration(800).start();
                }
            } else {
                UtilitySecurity.resetVisibility(llDpTop, View.INVISIBLE);
//                if (llDpTop.isShown()) {
//                    llDpTop.setAlpha(1f);
//                    llDpTop.animate().alpha(0f).setDuration(600).start();
//                    llDpTop.animate().setListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            UtilitySecurity.resetVisibility(llDpTop, View.INVISIBLE);
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//
//                        }
//                    });
//                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onBackPressed() {

        if (getStatusTip().isShowing()) {
            getStatusTip().hideProgress();
            mHttpClient.cancelRequests(this);
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick())
            return;

        if (getStatusTip().isShowing())
            return;

        Intent intent = null;
        switch (view.getId()) {
            case R.id.llDpBack:
                onBackPressed();
                break;

            // 最新章节
            case R.id.tvDpChapterName:
                toUpdateRead();
                break;

            // 共多少章
            case R.id.tvDpMenuTotal:
            case R.id.llDpMenu:
                toChapterMenuList();
                break;

            // 更多热门推荐
            case R.id.tvDpRecommendMore:
                toMoreRecommend();
                break;

            // 换一批
            case R.id.llDpReplace:
            case R.id.tvDpReplace:
            case R.id.ilDpReplace:
                replaceRecommend();
                break;

            // 下载
            case R.id.llDpDownload:
                UtilityBusiness.toDownloadMenuList(this, bookId);
                break;

            // 开始或继续阅读
            case R.id.tvDpRead:
                toRead();
                break;

            // 加入或移除书架
            case R.id.ivDpAddBookShelf:
            case R.id.tvDpAddBookShelf:
                addOrRemoveShelf();
                break;

            case R.id.llDpAddBookShelf:
                break;
        }

        if (intent != null)
            startActivity(intent);
    }
}