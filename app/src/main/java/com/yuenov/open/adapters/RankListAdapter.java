package com.yuenov.open.adapters;

import androidx.annotation.NonNull;

import com.yuenov.open.R;
import com.yuenov.open.model.standard.RankItemInfo;
import com.yuenov.open.widget.RankItemImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.List;

/**
 * 榜单
 */
public class RankListAdapter extends BaseQuickAdapter<RankItemInfo, BaseViewHolder> {

    public RankListAdapter(List<RankItemInfo> inviteList) {
        super(R.layout.view_adapter_ranklist_item, inviteList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RankItemInfo item) {

        UtilitySecurity.resetVisibility(helper.getView(R.id.viewAiRkLine), helper.getAdapterPosition() > 0);

        RankItemImageView riAiRkImg = helper.getView(R.id.riAiRkImg);
        riAiRkImg.setData(item.coverImgs);
        helper.setText(R.id.tvAiRkName, item.rankName);

        // 点击事件
        helper.addOnClickListener(R.id.rlAiRkContent, R.id.riAiRkImg, R.id.tvAiRkName);
    }
}
