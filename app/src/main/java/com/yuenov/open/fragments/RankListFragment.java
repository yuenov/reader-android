package com.yuenov.open.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.RankPageListActivity;
import com.yuenov.open.adapters.RankListAdapter;
import com.yuenov.open.fragments.baseInfo.BaseFragment;
import com.yuenov.open.model.standard.RankListInfo;
import com.yuenov.open.utils.UtilityException;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.util.UtilitySecurity;

import butterknife.BindView;

/**
 * 榜单
 */
public class RankListFragment extends BaseFragment implements  BaseQuickAdapter.OnItemChildClickListener {

    private static final String EXTRA_MODEL_LISTRANKITEMINFO = "listRankItemInfo";
    private RankListInfo list;

    public static RankListFragment getFragment( RankListInfo list) {
        RankListFragment fragment = new RankListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MODEL_LISTRANKITEMINFO, list);
        fragment.setArguments(bundle);

        return fragment;
    }

    @BindView(R.id.rvFrkList)
    protected RecyclerView rvFrkList;

    protected RankListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rank_list;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initExtra() {
        list = UtilitySecurity.getExtrasSerializable(getArguments(),EXTRA_MODEL_LISTRANKITEMINFO);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        adapter = new RankListAdapter(list.ranks);
        adapter.setOnLoadMoreListener(null, rvFrkList);
        adapter.setOnItemChildClickListener(this);
        adapter.loadMoreEnd(false);
        rvFrkList.setAdapter(adapter);
        rvFrkList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        try {
            String rankName = list.ranks.get(position).rankName;
            int channelId = list.channelId;
            int rankId = list.ranks.get(position).rankId;

            Intent intent = RankPageListActivity.getIntent(getContext(),rankName,channelId,rankId);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
