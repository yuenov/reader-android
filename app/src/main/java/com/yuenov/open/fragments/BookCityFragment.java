package com.yuenov.open.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuenov.open.R;
import com.yuenov.open.activitys.PreviewDetailActivity;
import com.yuenov.open.adapters.BookPreviewItemAdapter;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.fragments.baseInfo.BaseFragment;
import com.yuenov.open.model.httpModel.CategoriesListHttpModel;
import com.yuenov.open.model.responseModel.CategoriesListResponse;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.utils.UtilityCache;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

/**
 * 书城
 */
public class BookCityFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {

    private static final String extra_string_menuName = "menuName";
    private String menuName;

    private static final String extra_int_categoryid = "categoryId";
    private int categoryId;

    public static BookCityFragment getFragment(String menuName, int categoryId) {
        BookCityFragment fragment = new BookCityFragment();

        Bundle bundle = new Bundle();
        if (!UtilitySecurity.isEmpty(menuName))
            bundle.putString(extra_string_menuName, menuName);
        bundle.putInt(extra_int_categoryid, categoryId);

        fragment.setArguments(bundle);

        return fragment;
    }

    protected SwipeRefreshLayout srlBcList;
    protected RecyclerView rvBcList;

    private CategoriesListResponse res;
    protected BookPreviewItemAdapter adapter;

    private int thisPage = 1;
    private boolean isEnd = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bc;
    }

    @Override
    protected void initLayout() {
        srlBcList = viewContent.findViewById(R.id.srlBcList);
        rvBcList = viewContent.findViewById(R.id.rvBcList);
    }

    @Override
    protected void initExtra() {
        menuName = UtilitySecurity.getExtrasString(getArguments(), extra_string_menuName, "");
        categoryId = UtilitySecurity.getExtrasInt(getArguments(), extra_int_categoryid);

        if (categoryId < 1)
            UtilityToasty.error(R.string.info_loaddata_error);
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnRefreshListener(srlBcList, this);
    }

    @Override
    protected void initData() {
        adapter = new BookPreviewItemAdapter(null);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnLoadMoreListener(this, rvBcList);
        rvBcList.setAdapter(adapter);
        rvBcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // 先加载缓存数据
        String cacheResContent = UtilityCache.getContent(UtilityCache.BOOKCITY_START + categoryId);
        if (!UtilitySecurity.isEmpty(cacheResContent))
            setResponse(cacheResContent, true);

        loadData(true);
    }

    private void loadData(boolean isLoadHeader) {

        if (isLoadHeader)
            thisPage = 1;

        CategoriesListHttpModel httpModel = new CategoriesListHttpModel();
        httpModel.categoryId = categoryId;
        httpModel.pageNum = thisPage;
        mHttpClient.Request(getContext(), httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (thisPage == 1)
                    srlBcList.setRefreshing(true);
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
                        UtilityCache.saveContent(UtilityCache.BOOKCITY_START + categoryId, s);
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
                srlBcList.setRefreshing(false);
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