package com.yuenov.open.adapters;

import androidx.annotation.NonNull;

import com.yuenov.open.R;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.images.UtilityImage;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.List;

/**
 * 图书列表
 */
public class CategoryBookListAdapter extends BaseQuickAdapter<CategoriesListItem, BaseViewHolder> {

    /**
     * 是否展示order
     */
    private boolean showOrder = false;

    public void setShowOrder(boolean value) {
        this.showOrder = value;
    }

    public CategoryBookListAdapter(List<CategoriesListItem> inviteList) {
        super(R.layout.view_adapter_categorylist_item, inviteList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CategoriesListItem item) {

        com.makeramen.roundedimageview.RoundedImageView rivClCoverImg = helper.getView(R.id.rivClCoverImg);
        UtilityImage.setImage(rivClCoverImg, item.coverImg, R.mipmap.ic_book_list_default);

        helper.setText(R.id.tvClTitle, item.title);
        helper.setText(R.id.tvClDesc, item.desc);
        helper.setText(R.id.tvClAuthor, item.author);
        helper.setText(R.id.tvClCategoryName, item.categoryName);
        UtilitySecurity.resetVisibility(helper.getView(R.id.tvClLz), UtilityData.isSerialize(item.chapterStatus));
        UtilitySecurity.resetVisibility(helper.getView(R.id.tvClWj), !UtilityData.isSerialize(item.chapterStatus));
        // 第一个item不展示分割线
        UtilitySecurity.resetVisibility(helper.getView(R.id.viewClLine), helper.getLayoutPosition() > 0);

        // 排序 1,2,3
        com.makeramen.roundedimageview.RoundedImageView rivClOrder = helper.getView(R.id.rivClOrder);
        if (helper.getAdapterPosition() == 0) {
            UtilitySecurity.setImageResource(rivClOrder, R.mipmap.ic_img_num1);
        } else if (helper.getAdapterPosition() == 1) {
            UtilitySecurity.setImageResource(rivClOrder, R.mipmap.ic_img_num2);
        } else if (helper.getAdapterPosition() == 2) {
            UtilitySecurity.setImageResource(rivClOrder, R.mipmap.ic_img_num3);
        }
        UtilitySecurity.resetVisibility(rivClOrder, showOrder && helper.getAdapterPosition() <= 2);

        // 点击事件
        helper.addOnClickListener(
                R.id.rivClCoverImg, R.id.rivClCoverImg,
                R.id.rivClOrder, R.id.tvClTitle,
                R.id.tvClDesc, R.id.tvClAuthor, R.id.tvClLz, R.id.tvClWj);
    }
}
