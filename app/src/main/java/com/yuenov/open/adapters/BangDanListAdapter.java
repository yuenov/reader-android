package com.yuenov.open.adapters;

import androidx.annotation.NonNull;

import com.yuenov.open.R;
import com.yuenov.open.model.standard.CategoryMenuItem;
import com.yuenov.open.utils.images.UtilityImage;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.List;

public class BangDanListAdapter extends BaseQuickAdapter<CategoryMenuItem, BaseViewHolder> {

    public BangDanListAdapter(List<CategoryMenuItem> inviteList) {
        super(R.layout.view_adapter_bangdan_item, inviteList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CategoryMenuItem item) {

        UtilitySecurity.resetVisibility(helper.getView(R.id.viewAccLine), helper.getAdapterPosition() > 0);

        com.makeramen.roundedimageview.RoundedImageView rivAccCoverImg = helper.getView(R.id.rivAccCoverImg);
        UtilityImage.setImage(rivAccCoverImg, item.coverImg, R.mipmap.ic_book_list_default);

        helper.setText(R.id.tvAccCategoryName, item.categoryName);

        helper.addOnClickListener(R.id.rivClCoverImg);
    }
}
