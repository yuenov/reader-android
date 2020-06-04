package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.CategoryBookListAdapter;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.model.httpModel.CategoriesListHttpModel;
import com.yuenov.open.model.responseModel.CategoriesListResponse;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import butterknife.BindView;

/**
 * tab分类》二级页
 */
public class CategoryChannelItemListActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {
    private static final String extra_string_categoryName = "categoryName";
    private String categoryName;

    private static final String extra_int_categoryId = "categoryId";
    private int categoryId;

    private static final String extra_int_channelId = "channelId";
    private int channelId;

    public static Intent getIntent(Context context, String categoryName, int categoryId, int channelId) {
        Intent intent = new Intent(context, CategoryChannelItemListActivity.class);
        if (!UtilitySecurity.isEmpty(categoryName))
            intent.putExtra(extra_string_categoryName, categoryName);
        intent.putExtra(extra_int_categoryId, categoryId);
        intent.putExtra(extra_int_channelId, channelId);
        return intent;
    }

    @BindView(R.id.llCcfiFilter)
    protected LinearLayout llCcfiFilter;
    @BindView(R.id.tvCcfiName1)
    protected TextView tvCcfiName1;
    @BindView(R.id.tvCcfiName2)
    protected TextView tvCcfiName2;
    @BindView(R.id.tvCcfiName3)
    protected TextView tvCcfiName3;
    @BindView(R.id.srlCcnList)
    protected SwipeRefreshLayout srlCcnList;
    @BindView(R.id.rvCcnList)
    protected RecyclerView rvCcnList;

    private CategoriesListResponse res;
    protected CategoryBookListAdapter adapter;

    private int thisPage = 1;
    private boolean isEnd = false;

    private String[] arrFilter;
    private int filterPosition = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_categorychanne;
    }

    @Override
    protected void initExtra() {
        categoryName = UtilitySecurity.getExtrasString(getIntent(), extra_string_categoryName);
        categoryId = UtilitySecurity.getExtrasInt(getIntent(), extra_int_categoryId);
        channelId = UtilitySecurity.getExtrasInt(getIntent(), extra_int_channelId);
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, tvCcfiName1, tvCcfiName2, tvCcfiName3);
        UtilitySecurityListener.setOnRefreshListener(srlCcnList,this);
    }

    @Override
    protected void initData() {
        try {
            initMyAppTitle(categoryName);

            arrFilter = new String[]{
                    getString(R.string.CategoryChannelItemListActivity_filter1_value),
                    getString(R.string.CategoryChannelItemListActivity_filter2_value),
                    getString(R.string.CategoryChannelItemListActivity_filter3_value)};

            adapter = new CategoryBookListAdapter(null);
            adapter.setShowOrder(false);
            adapter.setOnLoadMoreListener(this, rvCcnList);
            rvCcnList.setAdapter(adapter);
            adapter.setOnItemChildClickListener(this);
            rvCcnList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            loadFilter1();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void loadData(boolean isLoadHeader) {

        if (isLoadHeader)
            thisPage = 1;

        CategoriesListHttpModel httpModel = new CategoriesListHttpModel();
        httpModel.categoryId = categoryId;
        httpModel.pageNum = thisPage;
        httpModel.channelId = channelId;
        httpModel.orderBy = arrFilter[filterPosition];
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                if (isLoadHeader)
                    srlCcnList.setRefreshing(true);
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
                srlCcnList.setRefreshing(false);
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

            if (isLoadHeader)
                rvCcnList.smoothScrollToPosition(0);

            UtilitySecurity.resetVisibility(llCcfiFilter, true);
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

    private void loadFilter1() {
        if (filterPosition == 0)
            return;

        UtilitySecurity.setTextColor(tvCcfiName1, R.color._b383);
        UtilitySecurity.setTextColor(tvCcfiName2, R.color.gray_6666);
        UtilitySecurity.setTextColor(tvCcfiName3, R.color.gray_6666);

        filterPosition = 0;
        loadData(true);
    }

    private void loadFilter2() {
        if (filterPosition == 1)
            return;

        UtilitySecurity.setTextColor(tvCcfiName1, R.color.gray_6666);
        UtilitySecurity.setTextColor(tvCcfiName2, R.color._b383);
        UtilitySecurity.setTextColor(tvCcfiName3, R.color.gray_6666);

        filterPosition = 1;
        loadData(true);
    }

    private void loadFilter3() {
        if (filterPosition == 2)
            return;

        UtilitySecurity.setTextColor(tvCcfiName1, R.color.gray_6666);
        UtilitySecurity.setTextColor(tvCcfiName2, R.color.gray_6666);
        UtilitySecurity.setTextColor(tvCcfiName3, R.color._b383);

        filterPosition = 2;
        loadData(true);
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            case R.id.tvCcfiName1:
                loadFilter1();
                break;

            case R.id.tvCcfiName2:
                loadFilter2();
                break;

            case R.id.tvCcfiName3:
                loadFilter3();
                break;
        }
    }
}
