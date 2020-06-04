package com.yuenov.open.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.database.tb.TbBookShelf;
import com.yuenov.open.utils.images.UtilityImage;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.List;

/**
 * 首页书架
 */
public class IndexBookShelfListAdapter extends BaseAdapter {
    private List<TbBookShelf> lisData;

    public IndexBookShelfListAdapter(List<TbBookShelf> value) {
        this.lisData = value;
    }

    @Override
    public int getCount() {
        return UtilitySecurity.size(this.lisData);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private View viewContent;
    private com.makeramen.roundedimageview.RoundedImageView rivAdiBsImg;
    private ImageView ivAdiBsUpdate;
    private TextView tvAdiBsTitle;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewContent = View.inflate(MyApplication.getAppContext(), R.layout.view_adapter_item_bookshelf, null);
        rivAdiBsImg = viewContent.findViewById(R.id.rivAdiBsImg);
        ivAdiBsUpdate = viewContent.findViewById(R.id.ivAdiBsUpdate);
        tvAdiBsTitle = viewContent.findViewById(R.id.tvAdiBsTitle);

        UtilityImage.setImage(rivAdiBsImg, lisData.get(position).coverImg, R.mipmap.ic_book_list_default);
        UtilitySecurity.resetVisibility(ivAdiBsUpdate, lisData.get(position).hasUpdate);
        UtilitySecurity.setText(tvAdiBsTitle, lisData.get(position).title);

        return viewContent;
    }
}