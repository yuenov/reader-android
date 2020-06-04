package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.CategoryListAdapter;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.interfaces.IGetCategoryListListener;
import com.yuenov.open.model.httpModel.GetCategoryEndHttpModel;
import com.yuenov.open.model.responseModel.FindIndexInfoResponse;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.model.standard.FindItemBookItemModel;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import java.util.List;

import butterknife.BindView;

/**
 * 完本
 */
public class CategoryEndListActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, CategoryListAdapter.IBookBlItemAdapter {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CategoryEndListActivity.class);
        return intent;
    }

    @BindView(R.id.srlCeList)
    protected SwipeRefreshLayout srlCeList;
    @BindView(R.id.rvCeList)
    protected RecyclerView rvCeList;

    private FindIndexInfoResponse res;
    protected CategoryListAdapter adapter;

    private int thisPage = 1;
    private boolean isEnd = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_categoryend;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnRefreshListener(srlCeList, this);
    }

    @Override
    protected void initData() {
        initMyAppTitle(R.string.CategoryEndListActivity_title);

        adapter = new CategoryListAdapter(null);
        adapter.setOnLoadMoreListener(this, rvCeList);
        rvCeList.setAdapter(adapter);
        adapter.setOnItemChildClickListener(this);
        adapter.setListener(this);
        rvCeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loadData(true);
    }

    private void loadData(boolean isLoadHeader) {

        if (isLoadHeader)
            thisPage = 1;

        GetCategoryEndHttpModel httpModel = new GetCategoryEndHttpModel();
        httpModel.pageNum = thisPage;
        httpModel.pageSize = ConstantInterFace.pageSize;
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (isLoadHeader)
                    srlCeList.setRefreshing(true);
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    adapter.loadMoreFail();
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
                srlCeList.setRefreshing(false);
            }
        });
    }

    private void setResponse(String s, boolean isLoadHeader) {
        try {
            res = mHttpClient.fromDataJson(s, FindIndexInfoResponse.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            res = null;
        }

        if (res == null || res.list == null) {
            isEnd = true;
            adapter.loadMoreEnd(false);
            return;
        }

        try {
            if (isLoadHeader) {
                adapter.setNewData(res.list);
            } else {
                adapter.addData(res.list);
            }

            isEnd = (adapter.getData().size() < (thisPage * ConstantInterFace.categoriesListPageSize)) || UtilitySecurity.isEmpty(res.list);

            if (!UtilitySecurity.isEmpty(res.list))
                thisPage++;

            if (isEnd) {
                adapter.loadMoreEnd(false);
            } else {
                adapter.loadMoreComplete();
            }
        } catch (Exception ex) {
            adapter.loadMoreFail();
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        try {
            CategoriesListItem item = (CategoriesListItem) adapter.getData().get(position);
            Intent intent = PreviewDetailActivity.getIntent(this, item.bookId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        loadData(false);
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
            Intent intent = CategoryEndAllListActivity.getIntent(this, item.categoryName, item.categoryId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onBookBlItemAdapterReplace(FindItemBookItemModel item) {
        if (item == null || UtilitySecurity.isEmpty(item.bookList))
            return;

        UtilityBusiness.getReplaceCategoryEndBooks(this, item.categoryId, item.bookList.size(), item.page + 1, item.type, new IGetCategoryListListener() {
            @Override
            public void onGetCategoryListSuccess(List<CategoriesListItem> list) {

                if (UtilitySecurity.isEmpty(list))
                    return;

                try {
                    int size = adapter.getData().size();
                    for (int i = 0; i < size; i++) {
                        if (adapter.getData().get(i).categoryId == item.categoryId) {
                            adapter.getData().get(i).bookList = list;
                            adapter.getData().get(i).page++;

                            LinearLayoutManager linearManager = (LinearLayoutManager) rvCeList.getLayoutManager();
                            int mFirstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                            int mLastVisibleItemPosition = linearManager.findLastVisibleItemPosition();
                            if (i >= mFirstVisibleItemPosition && i <= mLastVisibleItemPosition) {
                                adapter.notifyItemChanged(i);
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
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void onBackPressed() {
        mHttpClient.cancelRequests(this);

        super.onBackPressed();
    }
}