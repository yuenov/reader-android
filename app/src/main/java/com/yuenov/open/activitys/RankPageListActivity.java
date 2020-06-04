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
import com.yuenov.open.model.httpModel.GetRankPageListHttpModel;
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

public class RankPageListActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemChildClickListener {

    private static final String EXTRA_STRING_TITLE = "title";
    private String bTitle;

    private static final String EXTRA_INT_CHANNELID = "channelId";
    private int channelId;

    private static final String EXTRA_INT_RANKID = "rankId";
    private int rankId;

    public static Intent getIntent(Context context, String bTitle, int categoryId, int rankId) {
        Intent intent = new Intent(context, RankPageListActivity.class);
        if (!UtilitySecurity.isEmpty(bTitle))
            intent.putExtra(EXTRA_STRING_TITLE, bTitle);
        intent.putExtra(EXTRA_INT_CHANNELID, categoryId);
        intent.putExtra(EXTRA_INT_RANKID, rankId);
        return intent;
    }

    @BindView(R.id.srlRklList)
    protected SwipeRefreshLayout srlRklList;
    @BindView(R.id.rvRklList)
    protected RecyclerView rvRklList;

    private CategoriesListResponse res;
    protected CategoryBookListAdapter adapter;

    private int thisPage = 1;
    private boolean isEnd = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ranklist;
    }

    @Override
    protected void initExtra() {
        bTitle = UtilitySecurity.getExtrasString(getIntent(), EXTRA_STRING_TITLE);
        channelId = UtilitySecurity.getExtrasInt(getIntent(), EXTRA_INT_CHANNELID);
        rankId = UtilitySecurity.getExtrasInt(getIntent(), EXTRA_INT_RANKID);

        if (channelId < 1 || rankId < 1) {
            UtilityToasty.error(R.string.info_loaddata_error);
            finish();
        }
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnRefreshListener(srlRklList, this);
    }

    @Override
    protected void initData() {
        initMyAppTitle(bTitle);

        adapter = new CategoryBookListAdapter(null);
        adapter.setShowOrder(true);
        adapter.setOnLoadMoreListener(this, rvRklList);
        rvRklList.setAdapter(adapter);
        adapter.setOnItemChildClickListener(this);
        rvRklList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loadData(true);
    }

    private void loadData(boolean isLoadHeader) {

        if (isLoadHeader)
            thisPage = 1;

        GetRankPageListHttpModel httpModel = new GetRankPageListHttpModel();
        httpModel.channelId = channelId;
        httpModel.rankId = rankId;
        httpModel.pageNum = thisPage;
        httpModel.pageSize = ConstantInterFace.pageSize;
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (isLoadHeader)
                    srlRklList.setRefreshing(true);
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
                srlRklList.setRefreshing(false);
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
