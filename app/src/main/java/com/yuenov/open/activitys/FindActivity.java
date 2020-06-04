package com.yuenov.open.activitys;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuenov.open.adapters.CategoryListAdapter;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.interfaces.IGetCategoryListListener;
import com.yuenov.open.model.standard.FindItemBookItemModel;
import com.yuenov.open.model.httpModel.CategoryDiscoveryHttpModel;
import com.yuenov.open.model.responseModel.FindIndexInfoResponse;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityCache;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.renrui.libraries.util.mHttpClient;

import java.util.List;

import butterknife.BindView;

/**
 * 发现
 */
public class FindActivity extends BaseActivity implements View.OnClickListener, CategoryListAdapter.IBookBlItemAdapter, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.rlBiSearch)
    protected RelativeLayout rlBiSearch;
    @BindView(R.id.srlBsList)
    protected SwipeRefreshLayout srlBsList;
    @BindView(R.id.rvBsList)
    protected RecyclerView rvBsList;

    private int thisPage = 1;
    private boolean isEnd = false;
    protected CategoryListAdapter adapter;
    private FindIndexInfoResponse res;

    protected View viewType;
    protected LinearLayout llBst1;
    protected LinearLayout llBst2;
    protected LinearLayout llBst3;
    protected LinearLayout llBst4;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bookindex;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(rlBiSearch, this);
        UtilitySecurityListener.setOnRefreshListener(srlBsList, this);
    }

    @Override
    protected void initData() {

        adapter = new CategoryListAdapter(null);

        //  加到adapter的headerView中
        initTypes();

        adapter.setListener(this);
        adapter.setOnLoadMoreListener(this, rvBsList);
        rvBsList.setAdapter(adapter);
        rvBsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // 先加载缓存数据
        String cacheResContent = UtilityCache.getContent(UtilityCache.FINDINDEX);
        if (!UtilitySecurity.isEmpty(cacheResContent))
            setResponse(cacheResContent, true);

        loadData(true);
    }

    /**
     * 初始化顶部4个类别
     * 先隐藏，等数据加载成功后再展示
     */
    private void initTypes() {
        viewType = View.inflate(this, R.layout.view_findindex_type, null);
        llBst1 = viewType.findViewById(R.id.llBst1);
        llBst2 = viewType.findViewById(R.id.llBst2);
        llBst3 = viewType.findViewById(R.id.llBst3);
        llBst4 = viewType.findViewById(R.id.llBst4);

        UtilitySecurity.resetVisibility(viewType, false);
        adapter.addHeaderView(viewType);

        UtilitySecurityListener.setOnClickListener(this, llBst1, llBst2, llBst3, llBst4);
    }

    private void loadData(boolean isLoadHeader) {
        if (isLoadHeader)
            thisPage = 1;

        CategoryDiscoveryHttpModel httpModel = new CategoryDiscoveryHttpModel();
        httpModel.pageNum = thisPage;
        httpModel.pageSize = ConstantInterFace.categoriesListPageSize;
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (thisPage == 1)
                    srlBsList.setRefreshing(true);
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    adapter.loadMoreFail();
                    return;
                }

                try {
                    setResponse(s, isLoadHeader);

                    // 缓存第一页数据
                    if (isLoadHeader)
                        UtilityCache.saveContent(UtilityCache.FINDINDEX, s);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {

            }

            @Override
            public void onFinish() {
                srlBsList.setRefreshing(false);
            }
        });
    }

    private void setResponse(String s, boolean isLoadHeader) {
        try {
            res = mHttpClient.fromDataJson(s, FindIndexInfoResponse.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            return;
        }

        if (res == null || res.list == null) {
            isEnd = true;
            adapter.loadMoreEnd(false);
            return;
        }

        try {
            UtilitySecurity.resetVisibility(viewType, true);

            if (isLoadHeader) {
                adapter.setNewData(res.list);
            } else {
                adapter.addData(res.list);
            }

            isEnd = (adapter.getData().size() < (thisPage * ConstantInterFace.categoriesListPageSize)) || UtilitySecurity.isEmpty(res.list);
            thisPage++;
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void toSearch() {
        Intent intent = SearchActivity.getIntent(this);
        startActivity(intent);
    }

    private void toType1() {
        Intent intent = CategoryChannelActivity.getIntent(this);
        startActivity(intent);
    }

    private void toType2() {
        Intent intent = RankActivity.getIntent(this);
        startActivity(intent);
    }

    private void toType3() {
        Intent intent = CategoryEndListActivity.getIntent(this);
        startActivity(intent);
    }

    private void toType4() {
        Intent intent = SpecialListActivity.getIntent(this);
        startActivity(intent);
    }

    @Override
    public void onBookBlItemClick(CategoriesListItem item) {
        try {
            Intent intent = PreviewDetailActivity.getIntent(this, item.bookId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onBookBlItemAdapterShowAll(FindItemBookItemModel item) {
        try {
            Intent intent = FindAllListActivity.getIntent(this, item.categoryName, item.type, item.categoryId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 换一匹
     */
    @Override
    public void onBookBlItemAdapterReplace(final FindItemBookItemModel item) {
        if (item == null || UtilitySecurity.isEmpty(item.bookList))
            return;

        UtilityBusiness.getReplaceCategoryBooks(this, item.categoryId, item.bookList.size(), item.page + 1, item.type, new IGetCategoryListListener() {
            @Override
            public void onGetCategoryListSuccess(List<CategoriesListItem> list) {

                if (UtilitySecurity.isEmpty(list))
                    return;

                try {
                    int size = adapter.getData().size();
                    for (int i = 0; i < size; i++) {
                        if (UtilitySecurity.equalsIgnoreCase(adapter.getData().get(i).type, item.type)
                                && UtilitySecurity.equalsIgnoreCase(adapter.getData().get(i).categoryName, item.categoryName)) {
                            adapter.getData().get(i).bookList = list;
                            adapter.getData().get(i).page++;

                            LinearLayoutManager linearManager = (LinearLayoutManager) rvBsList.getLayoutManager();
                            int mFirstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                            int mLastVisibleItemPosition = linearManager.findLastVisibleItemPosition();
                            if (i + 1 >= mFirstVisibleItemPosition && i + 1 <= mLastVisibleItemPosition) {
                                adapter.notifyItemChanged(i + 1);
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onGetCategoryListLoadFail() {

            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        if (isEnd) {
            adapter.loadMoreEnd(false);
        } else {
            adapter.setEnableLoadMore(true);
            adapter.loadMoreComplete();
            loadData(false);
        }
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void onBackPressed() {

        if (getParent() instanceof MainActivity) {
            getParent().onBackPressed();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (view.getId()) {
            case R.id.rlBiSearch:
                toSearch();
                break;

            case R.id.llBst1:
                toType1();
                break;

            case R.id.llBst2:
                toType2();
                break;

            case R.id.llBst3:
                toType3();
                break;

            case R.id.llBst4:
                toType4();
                break;
        }
    }
}