package com.yuenov.open.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.model.standard.FindItemBookItemModel;
import com.yuenov.open.model.standard.SpecialItemModel;
import com.yuenov.open.utils.images.UtilityImage;
import com.yuenov.open.widget.OrderConfirmGridView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题
 */
public class SpecialListAdapter extends BaseQuickAdapter<SpecialItemModel, BaseViewHolder> {

    public interface ISpecialListAdapter {
        void onBookBlItemAdapterShowAll(SpecialItemModel item);

        void onBookBlItemClick(CategoriesListItem item);

        void onBookBlItemAdapterReplace(SpecialItemModel item);
    }

    private ISpecialListAdapter listener;

    public void setListener(ISpecialListAdapter listener) {
        this.listener = listener;
    }

    public SpecialListAdapter(List<SpecialItemModel> inviteList) {
        super(R.layout.view_adapter_find_item, inviteList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SpecialItemModel item) {

        helper.setText(R.id.tvAblCategoryName, item.name);

        OrderConfirmGridView ocgAblItem = helper.getView(R.id.ocgAblItem);
        ocgAblItem.setAdapter(new GridViewAdapter(item.bookList));
        ocgAblItem.measure(0, 0);
        ocgAblItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listener != null)
                    listener.onBookBlItemClick(item.bookList.get(i));
            }
        });

        TextView tvAblShowAll = helper.getView(R.id.tvAblShowAll);
        LinearLayout llAblReplace = helper.getView(R.id.llAblReplace);
        ImageView ivAblReplace = helper.getView(R.id.ivAblReplace);

        Animation animation = AnimationUtils.loadAnimation(MyApplication.getAppContext(), R.anim.anim_widget_rotate);

        if (this.listener != null) {
            tvAblShowAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onBookBlItemAdapterShowAll(item);
                }
            });
            llAblReplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animation.cancel();
                    ivAblReplace.startAnimation(animation);

                    listener.onBookBlItemAdapterReplace(item);
                }
            });
        }
    }

    public interface IBookBlItemAdapter {

        void onBookBlItemClick(CategoriesListItem item);

        void onBookBlItemAdapterShowAll(FindItemBookItemModel item);

        void onBookBlItemAdapterReplace(FindItemBookItemModel item);
    }

    class GridViewAdapter extends BaseAdapter {

        private List<CategoriesListItem> lisBook;
        private com.makeramen.roundedimageview.RoundedImageView rivAbliCoverImg;
        private TextView tvAbliTitle;
        private TextView tvAbliAuthor;

        public GridViewAdapter(List<CategoriesListItem> value) {

            if (value == null)
                this.lisBook = new ArrayList<>();
            else
                this.lisBook = value;
        }

        @Override
        public int getCount() {
            return this.lisBook.size();
        }

        @Override
        public Object getItem(int i) {
            return this.lisBook.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = View.inflate(MyApplication.getAppContext(), R.layout.view_adapter_find_item_item, null);

            rivAbliCoverImg = view.findViewById(R.id.rivAbliCoverImg);
            tvAbliTitle = view.findViewById(R.id.tvAbliTitle);
            tvAbliAuthor = view.findViewById(R.id.tvAbliAuthor);

            UtilityImage.setImage(rivAbliCoverImg, lisBook.get(i).coverImg, R.mipmap.ic_book_list_default);
            UtilitySecurity.setText(tvAbliTitle, lisBook.get(i).title);
            UtilitySecurity.setText(tvAbliAuthor, lisBook.get(i).author);

            return view;
        }
    }
}