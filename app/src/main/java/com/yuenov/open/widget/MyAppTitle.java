package com.yuenov.open.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;

/**
 * My app title
 **/
public class MyAppTitle extends LinearLayout {
    private OnLeftButtonClickListener mLeftButtonClickListener;
    private OnRightButtonClickListener mRightButtonClickListener;
    private MyViewHolder mViewHolder;
    private int mRightRes;
    private int mLeftRes;

    public TextView getCenterTextView() {
        if (null != mViewHolder)
            return mViewHolder.tvCenterTitle;
        else
            return null;
    }

    public TextView getRightTextView() {
        if (null != mViewHolder)
            return mViewHolder.tvRight;
        else
            return null;
    }

    public MyAppTitle(Context context) {
        super(context);
        init();
    }

    public MyAppTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MyAppTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        try {
            final LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            View viewAppTitle = inflater.inflate(R.layout.view_activity_titlebar, null);
            this.addView(viewAppTitle, layoutParams);

            mViewHolder = new MyViewHolder(this);

            mViewHolder.tvLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LibUtility.isFastDoubleClick()) {
                        return;
                    }

                    if (mLeftButtonClickListener != null) {
                        mLeftButtonClickListener.onLeftButtonClick(v);
                    }
                }
            });

            mViewHolder.tvRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LibUtility.isFastDoubleClick()) {
                        return;
                    }

                    if (mRightButtonClickListener != null) {
                        mRightButtonClickListener.OnRightButtonClick(v);
                    }
                }
            });
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public void initViewsVisible(boolean isLeftButtonVisible, boolean isCenterTitleVisible, boolean isRightIconVisible, boolean isRightTitleVisible) {
        // 左侧返回
        UtilitySecurity.resetVisibility(mViewHolder.tvLeft, isLeftButtonVisible);
        // 中间标题
        UtilitySecurity.resetVisibility(mViewHolder.tvCenterTitle, isCenterTitleVisible);

        //右侧图标和文案
        if (mViewHolder.tvRight != null) {
            //图标和文案都不可见
            if (!isRightIconVisible && !isRightTitleVisible) {
                UtilitySecurity.resetVisibility(mViewHolder.tvRight, View.INVISIBLE);
            }
            //图标可见文案不可见
            else if (isRightIconVisible && !isRightTitleVisible) {
                if (0 != mRightRes) {
                    setRightIcon(mRightRes);
                }
                mViewHolder.tvRight.setBackgroundColor(Color.TRANSPARENT);
                UtilitySecurity.resetVisibility(mViewHolder.tvRight, View.VISIBLE);
            }
            //图标不可见文案可见
            else if (!isRightIconVisible) {
                mViewHolder.tvRight.setCompoundDrawables(null, null, null, null);
                UtilitySecurity.resetVisibility(mViewHolder.tvRight, View.VISIBLE);
            }
            //图标和文案都可见
            else {
                if (0 != mRightRes) {
                    setRightIcon(mRightRes);
                }
                mViewHolder.tvRight.setBackgroundColor(Color.TRANSPARENT);
                UtilitySecurity.resetVisibility(mViewHolder.tvRight, View.VISIBLE);
            }
        }
    }

    /**
     * 设置标题
     */
    public void setAppTitle(String title) {
        UtilitySecurity.setText(mViewHolder.tvCenterTitle, title);
    }

    /**
     * 设置标题
     */
    public void setAppTitle(int resID) {
        UtilitySecurity.setText(mViewHolder.tvCenterTitle, MyApplication.getAppContext().getString(resID));
    }

    /**
     * 设置左侧按钮内容
     */
    public void setLeftTitle(String text) {
        try {
            mViewHolder.tvLeft.setCompoundDrawables(null, null, null, null);
            UtilitySecurity.setText(mViewHolder.tvLeft, text);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 右侧按钮是否显示
     */
    public boolean isRightVisible() {
        try {
            return VISIBLE == mViewHolder.tvRight.getVisibility();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否展示底部的线
     */
    public void resetLineVisibility(boolean value) {
        UtilitySecurity.resetVisibility(mViewHolder.viewLineAt, value);
    }

    public void resetLeft() {
        try {
            UtilitySecurity.setText(mViewHolder.tvLeft, "");

            Drawable drawable = getResources().getDrawable(R.drawable.bg_title_back_selector);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mViewHolder.tvLeft.setCompoundDrawables(drawable, null, null, null);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 设置右侧按钮的内容
     */
    public void setRightTitle(String text) {
        if (!TextUtils.isEmpty(text)) {
            UtilitySecurity.setText(mViewHolder.tvRight, text);
        }
    }

    /**
     * 设置右侧按钮是否可用
     */
    public void setRightTitleEnable(boolean enable) {
        if (null != mViewHolder.tvRight) {
            mViewHolder.tvRight.setEnabled(enable);
        }
    }

    /**
     * 设置右侧按钮的字体颜色
     */
    public void setRightTitleTextColor(int colorResourceID) {

        if (colorResourceID == 0) {
            return;
        }

        UtilitySecurity.setTextColor(mViewHolder.tvRight, colorResourceID);
    }

    public void setLeftIcon(int sourceId) {
        mLeftRes = sourceId;
        setLeftDrawable();
    }

    public void setRightIcon(int sourceId) {
        mRightRes = sourceId;
        setRightDrawable();
    }

    private void setRightDrawable() {
        Drawable drawable = getResources().getDrawable(mRightRes);
        if (null != drawable) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mViewHolder.tvRight.setCompoundDrawables(drawable, null, null, null);
        }
    }

    private void setLeftDrawable() {

        Drawable drawable = getResources().getDrawable(mLeftRes);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mViewHolder.tvLeft.setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * 2019年11月13日 如果传递过来的数据是负数，零，或者任意不正确的图标，依然需要将当前图标设置为新样式(null)
     *
     * @param sourceId
     */
    public void setCenterTitleRightDraw(int sourceId) {
        Drawable drawable = null;
        if (sourceId <= 0) {
            mViewHolder.tvCenterTitle.setCompoundDrawables(null, null, drawable, null);
        }
        try {
            drawable = getResources().getDrawable(sourceId);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
        if (drawable != null) {
            drawable.setBounds(0, 4, drawable.getMinimumWidth(), drawable.getMinimumHeight() + 4);
            mViewHolder.tvCenterTitle.setIncludeFontPadding(false);
        }
        // 注意此处不管最后生成的Drawable是否为null，都要填充到布局中
        mViewHolder.tvCenterTitle.setCompoundDrawables(null, null, drawable, null);
    }

    public void setOnLeftButtonClickListener(OnLeftButtonClickListener listen) {
        mLeftButtonClickListener = listen;
    }

    public void setOnRightButtonClickListener(OnRightButtonClickListener listen) {
        mRightButtonClickListener = listen;
    }

    public interface OnLeftButtonClickListener {
        void onLeftButtonClick(View v);
    }

    public interface OnRightButtonClickListener {
        void OnRightButtonClick(View v);
    }

    private class MyViewHolder {
        TextView tvLeft;
        TextView tvCenterTitle;
        TextView tvRight;
        View viewLineAt;

        public MyViewHolder(View v) {
            tvLeft = v.findViewById(R.id.tvLeft);
            tvCenterTitle = v.findViewById(R.id.tvCenterTitle);
            tvRight = v.findViewById(R.id.tvRight);
            viewLineAt = v.findViewById(R.id.viewLineAt);
        }
    }
}