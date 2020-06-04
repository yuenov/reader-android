package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.SearchBookDefaultListAdapter;
import com.yuenov.open.adapters.SearchBookListAdapter;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.model.httpModel.SearchBookHttpModel;
import com.yuenov.open.model.responseModel.CategoriesListResponse;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.UtilityAppConfig;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * 搜索页
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SearchBookListAdapter.ISearchBookListAdapterListener,
        BaseQuickAdapter.OnItemChildClickListener, View.OnKeyListener {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        return intent;
    }

    @BindView(R.id.rlScBack)
    protected RelativeLayout rlScBack;
    @BindView(R.id.etScContent)
    protected EditText etScContent;
    @BindView(R.id.tvScSearch)
    protected TextView tvScSearch;

    @BindView(R.id.rvScHotList)
    protected RecyclerView rvScHotList;
    @BindView(R.id.rvScSearchList)
    protected RecyclerView rvScSearchList;
    @BindView(R.id.viewScLine)
    protected View viewScLine;
    @BindView(R.id.tvScFeedBack)
    protected TextView tvScFeedBack;

    protected View viewHistory;
    protected TagFlowLayout tflScHistory;
    protected TextView tvScClearSearchHistory;

    private int thisPage = 1;
    private boolean isEnd = false;

    // 历史搜索关键词
    private List<String> lisSearchHistory;
    // 热门列表
    private SearchBookDefaultListAdapter defaultListAdapter;
    // 搜索列表
    private CategoriesListResponse res;
    private SearchBookListAdapter searchAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnKeyListener(etScContent, this);
        UtilitySecurityListener.setOnClickListener(etScContent, this);
        UtilitySecurityListener.setOnClickListener(this, rlScBack, tvScSearch);
        rvScHotList.setOnScrollListener(onScrollListener);
        rvScSearchList.setOnScrollListener(onScrollListener);
        UtilitySecurityListener.setOnClickListener(this, tvScFeedBack);
    }

    @Override
    protected void initData() {
        initSearchHistory();

        initHotList();

        if (!UtilitySecurity.isEmpty(UtilityAppConfig.getInstant().hotSearch))
            UtilitySecurity.setHint(etScContent, UtilityAppConfig.getInstant().hotSearch.get(0).title);
    }

    /**
     * 初始化搜索历史
     */
    private void initSearchHistory() {
        try {
            viewHistory = View.inflate(this, R.layout.view_search_searchhistory, null);
            tflScHistory = viewHistory.findViewById(R.id.tflScHistory);
            tvScClearSearchHistory = viewHistory.findViewById(R.id.tvScClearSearchHistory);
            UtilitySecurityListener.setOnClickListener(this, tvScClearSearchHistory);

            lisSearchHistory = EditSharedPreferences.getSearchHistory();
            tflScHistory.setAdapter(new TagAdapter(lisSearchHistory) {
                View viewName;
                TextView tvIslName;

                @Override
                public View getView(FlowLayout parent, int position, Object o) {
                    try {
                        viewName = View.inflate(getApplicationContext(), R.layout.view_item_search_history, null);
                        tvIslName = viewName.findViewById(R.id.tvIslName);
                        UtilitySecurity.setText(tvIslName, lisSearchHistory.get(position));
                    } catch (Exception ex) {
                        UtilityException.catchException(ex);
                    }
                    return viewName;
                }
            });
            tflScHistory.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {
                    UtilitySecurity.setText(etScContent, lisSearchHistory.get(position));
                    UtilitySecurity.setLastSelection(etScContent);
                    search(true);
                    return true;
                }
            });

            UtilitySecurity.resetVisibility(viewHistory, !UtilitySecurity.isEmpty(lisSearchHistory));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 初始化默认热门图书
     */
    private void initHotList() {
        try {
            defaultListAdapter = new SearchBookDefaultListAdapter(UtilityAppConfig.getInstant().hotSearch);
            defaultListAdapter.addHeaderView(viewHistory);
            defaultListAdapter.addHeaderView(View.inflate(getApplicationContext(), R.layout.view_search_hotlist_header, null));
            rvScHotList.setAdapter(defaultListAdapter);
            defaultListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    try {
                        Intent intent = PreviewDetailActivity.getIntent(getApplicationContext(), UtilityAppConfig.getInstant().hotSearch.get(position).bookId);
                        startActivity(intent);
                    } catch (Exception ex) {
                        UtilityException.catchException(ex);
                    }
                }
            });

            rvScHotList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            UtilitySecurity.resetVisibility(rvScHotList, !UtilitySecurity.isEmpty(UtilityAppConfig.getInstant().hotSearch));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void search(boolean isLoadHeader) {

        if (getStatusTip().isShowing())
            return;

        // 未输入搜索关键字，hint就是关键字
        if (UtilitySecurity.isEmpty(etScContent)) {
            UtilitySecurity.setText(etScContent, etScContent.getHint().toString());
            UtilitySecurity.setLastSelection(etScContent);
        }

        // 关闭软键盘
        LibUtility.CloseKeyBord(this);

        if (searchAdapter == null) {
            searchAdapter = new SearchBookListAdapter(null);
            searchAdapter.setOnLoadMoreListener(this, rvScSearchList);
            rvScSearchList.setAdapter(searchAdapter);
            searchAdapter.setListener(this);
            searchAdapter.setOnItemChildClickListener(this);
            rvScSearchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }

        // 隐藏历史搜索记录 和 热门图书列表
        UtilitySecurity.resetVisibility(viewHistory, false);
        UtilitySecurity.resetVisibility(rvScHotList, false);
        UtilitySecurity.resetVisibility(rvScSearchList, false);
        UtilitySecurity.resetVisibility(viewScLine, false);
        UtilitySecurity.resetVisibility(tvScFeedBack, false);

        if (isLoadHeader)
            thisPage = 1;

        SearchBookHttpModel httpModel = new SearchBookHttpModel();
        httpModel.keyWord = etScContent.getText().toString();
        httpModel.pageNum = thisPage;
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (thisPage == 1)
                    getPubLoadingView().show();
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    searchAdapter.loadMoreFail();
                    return;
                }

                try {
                    setResponse(s, isLoadHeader);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {
                UtilityToasty.error(s);
            }

            @Override
            public void onFinish() {
                getPubLoadingView().hide();
            }
        });
    }

    private void setResponse(String s, boolean isLoadHeader) {
        try {
            res = mHttpClient.fromDataJson(s, CategoriesListResponse.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            res = null;
        }

        if (res == null || res.list == null) {
            isEnd = true;
            searchAdapter.loadMoreEnd(false);
            UtilitySecurity.resetVisibility(tvScFeedBack, true);
            return;
        }

        // 搜索有结果，才添加到搜索历史里
        else {
            if (!lisSearchHistory.contains(etScContent.getText().toString())) {
                lisSearchHistory.add(etScContent.getText().toString());
                tflScHistory.getAdapter().notifyDataChanged();
                EditSharedPreferences.addSearchHistory(etScContent.getText().toString());
            }
        }

        try {
            searchAdapter.setHotWordsText(etScContent.getText().toString());

            if (isLoadHeader) {
                searchAdapter.setNewData(res.list);
            } else {
                searchAdapter.addData(res.list);
            }

            isEnd = (searchAdapter.getData().size() < (thisPage * ConstantInterFace.categoriesListPageSize)) || UtilitySecurity.isEmpty(res.list);

            if (!UtilitySecurity.isEmpty(res.list))
                thisPage++;

            if (isEnd) {
                searchAdapter.loadMoreEnd(false);
            } else {
                searchAdapter.loadMoreComplete();
            }

            UtilitySecurity.resetVisibility(rvScSearchList, true);
            UtilitySecurity.resetVisibility(viewScLine, true);
            UtilitySecurity.resetVisibility(tvScFeedBack, true);
        } catch (Exception ex) {
            searchAdapter.loadMoreFail();
            UtilityException.catchException(ex);
        }
    }

    private void clearSearchHistory() {

        if (getStatusTip().isShowing())
            return;

        // 关闭软键盘
        LibUtility.CloseKeyBord(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDeletePop();
            }
        }, 100);
    }

    private void showDeletePop() {
        //按钮的定义
        PromptButton confirm = new PromptButton("确定", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
                if (lisSearchHistory != null)
                    lisSearchHistory.clear();
                EditSharedPreferences.setSearchHistory(new ArrayList<>());
                UtilitySecurity.resetVisibility(viewHistory, false);
            }
        });
        confirm.setFocusBacColor(Color.parseColor("#FAFAD2"));
        //Alert的调用
        PromptDialog promptDialog = new PromptDialog(this);
        promptDialog.showWarnAlert("你确定清空搜索记录？", new PromptButton("取消", new PromptButtonListener() {
            @Override
            public void onClick(PromptButton button) {
            }
        }), confirm);
    }

    private void toFeedBack() {
        if (getStatusTip().isShowing())
            return;

        startActivity(FeedBackActivity.getIntentByLarkBook(this, UtilitySecurity.getText(etScContent)));
    }

    private void resetDefaultStyle() {
        UtilitySecurity.resetVisibility(viewHistory, !UtilitySecurity.isEmpty(lisSearchHistory));
        UtilitySecurity.resetVisibility(rvScHotList, !UtilitySecurity.isEmpty(UtilityAppConfig.getInstant().hotSearch));
        UtilitySecurity.resetVisibility(rvScSearchList, false);
        UtilitySecurity.resetVisibility(viewScLine, false);
        UtilitySecurity.resetVisibility(tvScFeedBack, false);

        // 清空搜索结果列表
        if (res != null && !UtilitySecurity.isEmpty(res.list)) {
            res.list.clear();
            searchAdapter.notifyDataSetChanged();
            rvScSearchList.smoothScrollToPosition(0);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LibUtility.OpenKeyBord(SearchActivity.this, etScContent);
            }
        }, 100);
    }

    // 滑动时 关闭软键盘
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                LibUtility.CloseKeyBord(SearchActivity.this);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    public void onLoadMoreRequested() {
        search(false);
    }

    @Override
    public void onBookAddBookShelfClick(CategoriesListItem item) {
    }

    @Override
    public void onBookReadClick(CategoriesListItem item) {
        if (item == null)
            return;

        if (getStatusTip().isShowing())
            return;

        try {
            BookBaseInfo bookBaseInfo = new BookBaseInfo();
            bookBaseInfo.bookId = item.bookId;
            bookBaseInfo.title = item.title;
            bookBaseInfo.author = item.author;
            bookBaseInfo.coverImg = item.coverImg;
            UtilityBusiness.toRead(SearchActivity.this, bookBaseInfo);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

        if (getStatusTip().isShowing())
            return;

        try {
            CategoriesListItem item = (CategoriesListItem) adapter.getData().get(position);
            Intent intent = PreviewDetailActivity.getIntent(this, item.bookId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (v.getId() == R.id.etScContent && keyCode == 66) {
                search(true);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        if (getStatusTip().isShowing())
            return;

        switch (v.getId()) {
            case R.id.rlScBack:
                onBackPressed();
                break;

            // 点击焦点框，布局置为默认样式
            case R.id.etScContent:
                resetDefaultStyle();
                break;

            // 清空
            case R.id.tvScClearSearchHistory:
                clearSearchHistory();
                break;

            // 搜索
            case R.id.tvScSearch:
                search(true);
                break;

            // 反馈
            case R.id.tvScFeedBack:
                toFeedBack();
                break;
        }
    }
}
