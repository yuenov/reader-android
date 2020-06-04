package com.yuenov.open.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.widget.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LightView extends LinearLayout implements View.OnClickListener {

    interface ILightViewListener {
        void onStatChange(View view, boolean select);
    }

    @BindView(R.id.riWgInside)
    protected RoundedImageView riWgInside;

    @BindView(R.id.ivWgCenter)
    protected ImageView ivWgCenter;

    @BindView(R.id.riWgOutside)
    protected RoundedImageView riWgOutside;

    private ILightViewListener listener;

    private boolean select = false;

    public LightView(Context context) {
        super(context);

        initData();
    }

    public LightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initData();

        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LightView);
        if (a != null) {
            int inSideResource = a.getResourceId(R.styleable.LightView_inSideResource, 0);
            UtilitySecurity.setImageResource(riWgInside, inSideResource);

            int inSideCenterResource = a.getResourceId(R.styleable.LightView_centerResource, 0);
            if (inSideCenterResource > 0)
                UtilitySecurity.setImageResource(ivWgCenter, inSideCenterResource);

            int outSideResource = a.getResourceId(R.styleable.LightView_outSideResource, 0);
            UtilitySecurity.setImageResource(riWgOutside, outSideResource);
        }
    }

    private void initData() {
        initLayout();

        initListener();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View viewContent = inflater.inflate(R.layout.view_widget_light, null);

        ButterKnife.bind(this, viewContent);

        this.addView(viewContent, layoutParams);
    }

    private void initListener() {
        UtilitySecurityListener.setOnClickListener(this, riWgInside, riWgOutside);
    }

    public void setListener(ILightViewListener listener) {
        this.listener = listener;
    }

    public void setSelect(boolean value) {
        select = value;
        UtilitySecurity.resetVisibility(riWgOutside, select);
    }

    @Override
    public void onClick(View view) {

        if(select)
            return;

        select = true;
        UtilitySecurity.resetVisibility(riWgOutside, select);

        if (this.listener != null)
            this.listener.onStatChange(this, select);
    }
}
