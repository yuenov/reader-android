package com.yuenov.open.adapters;

import android.view.View;

import androidx.annotation.NonNull;

import com.yuenov.open.R;
import com.yuenov.open.model.standard.CategoryMenuItem;
import com.yuenov.open.model.standard.CategoryMenuListItem;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.widget.CategoryItemImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.List;

/**
 * 分类列表页item
 */
public class CategoryChannelListAdapter extends BaseQuickAdapter<CategoryMenuListItem, BaseViewHolder> {

    public interface ICategoryChannelListAdapterListener {
        void onCategoryChannelListAdapterClick(CategoryMenuItem item);
    }

    private ICategoryChannelListAdapterListener listener;

    public void setListener(ICategoryChannelListAdapterListener listener) {
        this.listener = listener;
    }

    public CategoryChannelListAdapter(List<CategoryMenuListItem> inviteList) {
        super(R.layout.view_adapter_categorychannel_item, inviteList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CategoryMenuListItem item) {

        if (UtilitySecurity.isEmpty(item.list))
            return;

        try {
            helper.setText(R.id.tvAcciName1, item.list.get(0).categoryName);
            helper.getView(R.id.llAcci1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && !UtilitySecurity.isEmpty(item.list))
                        listener.onCategoryChannelListAdapterClick(item.list.get(0));
                }
            });
            CategoryItemImageView ciivAcciImg1 = helper.getView(R.id.ciivAcciImg1);
            ciivAcciImg1.setData(item.list.get(0).coverImgs);
            ciivAcciImg1.setListener(new CategoryItemImageView.CategoryItemImageViewListener() {
                @Override
                public void categoryItemImageViewOnClick() {
                    if (listener != null && !UtilitySecurity.isEmpty(item.list))
                        listener.onCategoryChannelListAdapterClick(item.list.get(0));
                }
            });

            if (item.list.size() > 1) {
                helper.setText(R.id.tvAcciName2, item.list.get(1).categoryName);
                helper.getView(R.id.llAcci2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null && !UtilitySecurity.isEmpty(item.list) && item.list.size() > 1)
                            listener.onCategoryChannelListAdapterClick(item.list.get(1));
                    }
                });
                CategoryItemImageView ciivAcciImg2 = helper.getView(R.id.ciivAcciImg2);
                ciivAcciImg2.setData(item.list.get(0).coverImgs);
                ciivAcciImg2.setListener(new CategoryItemImageView.CategoryItemImageViewListener() {
                    @Override
                    public void categoryItemImageViewOnClick() {
                        if (listener != null && !UtilitySecurity.isEmpty(item.list) && item.list.size() > 1)
                            listener.onCategoryChannelListAdapterClick(item.list.get(1));
                    }
                });
            }
            UtilitySecurity.resetVisibility(helper.getView(R.id.llAcci2), item.list.size() > 1 ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
