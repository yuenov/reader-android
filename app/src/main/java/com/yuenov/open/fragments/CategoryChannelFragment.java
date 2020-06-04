package com.yuenov.open.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.CategoryChannelItemListActivity;
import com.yuenov.open.adapters.CategoryChannelListAdapter;
import com.yuenov.open.fragments.baseInfo.BaseFragment;
import com.yuenov.open.model.standard.CategoryMenuItem;
import com.yuenov.open.model.standard.CategoryMenuListItem;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 分类列表页
 */
public class CategoryChannelFragment extends BaseFragment implements CategoryChannelListAdapter.ICategoryChannelListAdapterListener {

    private static final String extra_int_channelId = "channelId";
    private int channelId;

    private static final String extra_list_CategoryChanne = "CategoryChanne";
    private List<CategoryMenuItem> lisData;

    public static CategoryChannelFragment getFragment(int channelId,ArrayList<CategoryMenuItem> inviteList) {
        CategoryChannelFragment fragment = new CategoryChannelFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(extra_int_channelId, channelId);
        if (!UtilitySecurity.isEmpty(inviteList))
            bundle.putSerializable(extra_list_CategoryChanne, inviteList);

        fragment.setArguments(bundle);

        return fragment;
    }

    @BindView(R.id.rvFccList)
    protected RecyclerView rvFccList;

    protected CategoryChannelListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_categorychannel_item;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initExtra() {
        channelId= UtilitySecurity.getExtrasInt(getArguments(), extra_int_channelId);
        lisData = UtilitySecurity.getExtrasSerializable(getArguments(), extra_list_CategoryChanne);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

        try {
            List<CategoryMenuListItem> inviteList = new ArrayList<>();
            CategoryMenuListItem categoryMenuListItem;
            for (int i = 0; i < lisData.size(); i += 2) {
                categoryMenuListItem = new CategoryMenuListItem();
                categoryMenuListItem.list.add(lisData.get(i));
                if (lisData.size() - 1 > i) {
                    categoryMenuListItem.list.add(lisData.get(i + 1));
                }

                inviteList.add(categoryMenuListItem);
            }

            adapter = new CategoryChannelListAdapter(inviteList);
            adapter.setListener(this);
            rvFccList.setAdapter(adapter);
            rvFccList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onCategoryChannelListAdapterClick(CategoryMenuItem item) {
        try {
            Intent intent = CategoryChannelItemListActivity.getIntent(getContext(), item.categoryName, item.categoryId, channelId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}