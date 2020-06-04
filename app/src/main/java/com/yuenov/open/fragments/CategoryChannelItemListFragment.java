package com.yuenov.open.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuenov.open.R;
import com.yuenov.open.activitys.PreviewDetailActivity;
import com.yuenov.open.adapters.CategoryBookListAdapter;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.fragments.baseInfo.BaseFragment;
import com.yuenov.open.model.httpModel.CategoriesListHttpModel;
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
 * 发现页》子目录列表
 */
public class CategoryChannelItemListFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemChildClickListener{

    private static final String extra_int_categoryId = "categoryId";
    private int categoryId;

    private static final String extra_int_channelId = "channelId";
    private int channelId;

    private static final String extra_string_filter = "filter";
    private String filter;

    public static CategoryChannelItemListFragment getFragment(int categoryId, int channelId, String filter) {
        CategoryChannelItemListFragment fragment = new CategoryChannelItemListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(extra_int_categoryId, categoryId);
        bundle.putInt(extra_int_channelId, channelId);
        if (!UtilitySecurity.isEmpty(filter))
            bundle.putString(extra_string_filter, filter);
        fragment.setArguments(bundle);

        return fragment;
    }

    @BindView(R.id.srlFccilList)
    protected SwipeRefreshLayout srlFccilList;
    @BindView(R.id.rvFccilList)
    protected RecyclerView rvFccilList;

    private CategoriesListResponse res;
    protected CategoryBookListAdapter adapter;

    private int thisPage = 1;
    private boolean isEnd = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_categorychannel_item_list;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initExtra() {
        categoryId = UtilitySecurity.getExtrasInt(getArguments(),extra_int_categoryId);
        channelId = UtilitySecurity.getExtrasInt(getArguments(),extra_int_channelId);
        filter = UtilitySecurity.getExtrasString(getArguments(),extra_string_filter,"");
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnRefreshListener(srlFccilList, this);
    }

    @Override
    protected void initData() {

        adapter = new CategoryBookListAdapter(null);
        adapter.setShowOrder(false);
        adapter.setOnLoadMoreListener(this, rvFccilList);
        rvFccilList.setAdapter(adapter);
        adapter.setOnItemChildClickListener(this);
        rvFccilList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        loadData(true);
    }

    private void loadData(boolean isLoadHeader) {

        if (isLoadHeader)
            thisPage = 1;

        CategoriesListHttpModel httpModel = new CategoriesListHttpModel();
        httpModel.categoryId = categoryId;
        httpModel.pageNum = thisPage;
        httpModel.channelId = channelId;
        httpModel.filter = filter;
        mHttpClient.Request(getContext(), httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (isLoadHeader)
                    srlFccilList.setRefreshing(true);
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
                srlFccilList.setRefreshing(false);
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
            Intent intent = PreviewDetailActivity.getIntent(getContext(), item.bookId);
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
}