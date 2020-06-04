package com.yuenov.open.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyInfoItemView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.rlWmiContent)
    protected RelativeLayout rlWmiContent;
    @BindView(R.id.ivWmiIcon)
    protected ImageView ivWmiIcon;
    @BindView(R.id.tvWmiName)
    protected TextView tvWmiName;

    public interface MyInfoClickListener {
        void myInfoOnClick(String name);
    }

    private MyInfoClickListener listener;

    public MyInfoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyInfoItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setListener(MyInfoClickListener listener) {
        this.listener = listener;
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        initLayout();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Attr_MyInfoItemView);
        int iconResource = a.getResourceId(R.styleable.Attr_MyInfoItemView_MyInfoIconResource, 0);
        String name = a.getString(R.styleable.Attr_MyInfoItemView_MyInfoName);

        setData(iconResource, name);
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View viewContent = inflater.inflate(R.layout.view_widget_myinfo_item, null);

        ButterKnife.bind(this,viewContent);

        UtilitySecurityListener.setOnClickListener(this, rlWmiContent, ivWmiIcon, tvWmiName);

        this.addView(viewContent, layoutParams);
    }

    public void setData(int iconResourceID, String name) {
        if (iconResourceID > 0)
            ivWmiIcon.setImageResource(iconResourceID);

        UtilitySecurity.setText(tvWmiName, name);
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick())
            return;

        if (listener != null)
            listener.myInfoOnClick(UtilitySecurity.getText(tvWmiName));
    }
}