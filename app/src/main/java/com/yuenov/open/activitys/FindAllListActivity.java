package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.CategoryBookListAdapter;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.model.httpModel.CategoryDiscoveryAllListHttpModel;
import com.yuenov.open.model.responseModel.CategoriesListResponse;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import butterknife.BindView;

/**
 * 发现-查看全部
 */
public class FindAllListActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemChildClickListener {

    private static final String EXTRA_STRING_TITLE = "title";
    private String bTitle;

    private static final String EXTRA_INT_CATEGORYID = "categoryId";
    private int categoryId;

    private static final String EXTRA_STRING_TYPE = "type";
    private String type;

    public static Intent getIntent(Context context, String title,String type, int bId) {
        Intent intent = new Intent(context, FindAllListActivity.class);
        if (!UtilitySecurity.isEmpty(title))
            intent.putExtra(EXTRA_STRING_TITLE, title);
        intent.putExtra(EXTRA_INT_CATEGORYID, bId);
        if (!UtilitySecurity.isEmpty(type))
            intent.putExtra(EXTRA_STRING_TYPE, type);
        return intent;
    }

    @BindView(R.id.srlClList)
    protected SwipeRefreshLayout srlClList;
    @BindView(R.id.rvClList)
    protected RecyclerView rvClList;

    private CategoriesListResponse res;
    protected CategoryBookListAdapter adapter;

    private int thisPage = 1;
    private boolean isEnd = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_findalllist;
    }

    @Override
    protected void initExtra() {
        bTitle = UtilitySecurity.getExtrasString(getIntent(), EXTRA_STRING_TITLE);
        categoryId = UtilitySecurity.getExtrasInt(getIntent(), EXTRA_INT_CATEGORYID);
        type = UtilitySecurity.getExtrasString(getIntent(), EXTRA_STRING_TYPE);
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnRefreshListener(srlClList, this);
    }

    @Override
    protected void initData() {
        initMyAppTitle(bTitle);

        adapter = new CategoryBookListAdapter(null);
        adapter.setOnLoadMoreListener(this, rvClList);
        rvClList.setAdapter(adapter);
        adapter.setOnItemChildClickListener(this);
        rvClList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loadData(true);
    }

    private void loadData(boolean isLoadHeader) {

        if (isLoadHeader)
            thisPage = 1;

        CategoryDiscoveryAllListHttpModel httpModel = new CategoryDiscoveryAllListHttpModel();
        httpModel.categoryId = categoryId;
        httpModel.pageNum = thisPage;
        httpModel.pageSize = ConstantInterFace.pageSize;
        httpModel.type = type;
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (isLoadHeader)
                    srlClList.setRefreshing(true);
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
                srlClList.setRefreshing(false);
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
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void onLoadMoreRequested() {
        loadData(false);
    }

    @Override
    public void onBackPressed() {
        if (getStatusTip().isShowing())
            getStatusTip().hideProgress();

        super.onBackPressed();
    }
}