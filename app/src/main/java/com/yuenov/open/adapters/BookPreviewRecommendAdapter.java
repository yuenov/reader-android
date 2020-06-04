package com.yuenov.open.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.utils.images.UtilityImage;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 预览页推荐书籍
 */
public class BookPreviewRecommendAdapter extends BaseAdapter {

    public List<CategoriesListItem> list;
    private View viewContent;

    @BindView(R.id.rivAdrCoverImg)
    protected com.makeramen.roundedimageview.RoundedImageView rivAdrCoverImg;
    @BindView(R.id.tvAdrTitle)
    protected TextView tvAdrTitle;
    @BindView(R.id.tvAdrAuthor)
    protected TextView tvAdrAuthor;

    public BookPreviewRecommendAdapter(List<CategoriesListItem> value) {
        this.list = value;
    }

    public void setData(List<CategoriesListItem> value) {
        this.list = value;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        viewContent = View.inflate(MyApplication.getAppContext(), R.layout.view_adapter_item_detailpewview_recommend, null);
        ButterKnife.bind(this,viewContent);

        UtilityImage.setImage(rivAdrCoverImg, list.get(i).coverImg,R.mipmap.ic_book_list_default);
        UtilitySecurity.setText(tvAdrTitle, list.get(i).title);
        UtilitySecurity.setText(tvAdrAuthor, list.get(i).author);

        return viewContent;
    }
}
