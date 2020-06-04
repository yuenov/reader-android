package com.yuenov.open.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.images.UtilityImage;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryItemImageView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.ivWgcgi1)
    protected ImageView ivWgcgi1;
    @BindView(R.id.ivWgcgi2)
    protected ImageView ivWgcgi2;
    @BindView(R.id.ivWgcgi3)
    protected ImageView ivWgcgi3;

    public interface CategoryItemImageViewListener {
        void categoryItemImageViewOnClick();
    }

    private CategoryItemImageViewListener listener;

    public CategoryItemImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CategoryItemImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setListener(CategoryItemImageViewListener listener) {
        this.listener = listener;
    }

    private void init() {
        initLayout();
        initListener();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View viewContent = inflater.inflate(R.layout.view_widget_categoryitem_image, null);

        ButterKnife.bind(this, viewContent);

        this.addView(viewContent, layoutParams);
    }

    private void initListener() {
        UtilitySecurityListener.setOnClickListener(this, ivWgcgi1, ivWgcgi2, ivWgcgi3);
    }

    public void setData(List<String> imgs) {

        if (UtilitySecurity.isEmpty(imgs))
            return;

        try {
            if (imgs.size() > 0)
                UtilityImage.setImage(ivWgcgi3, imgs.get(0), R.mipmap.ic_book_list_default);

            if (imgs.size() > 1)
                UtilityImage.setImage(ivWgcgi1, imgs.get(1), R.mipmap.ic_book_list_default);

            if (imgs.size() > 2)
                UtilityImage.setImage(ivWgcgi2, imgs.get(2), R.mipmap.ic_book_list_default);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onClick(View view) {

        if (listener == null)
            return;

        switch (view.getId()) {
            case R.id.ivWgcgi1:
            case R.id.ivWgcgi2:
            case R.id.ivWgcgi3:
                listener.categoryItemImageViewOnClick();
                break;
        }
    }
}
