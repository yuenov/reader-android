package com.yuenov.open.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import androidx.fragment.app.FragmentActivity;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.renrui.libraries.util.UtilitySecurity;

/**
 * 加载动画
 */
public class PubLoadingView {

    private int animDuration = 777;

    private BaseActivity mActivity;
    private View mStatusTipView;
    private SpinKitView skvPubLoading;

    public PubLoadingView(Context context) {
        this(context, true);
    }

    public PubLoadingView(Context context, boolean isAddToActivity) {
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;

            mStatusTipView = LayoutInflater.from(mActivity).inflate(R.layout.view_pub_loading, null);
            UtilitySecurity.resetVisibility(mStatusTipView, false);

            skvPubLoading = mStatusTipView.findViewById(R.id.skvPubLoading);
            skvPubLoading.WaveDuration = animDuration;

            if (isAddToActivity) {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mActivity.addContentView(mStatusTipView, params);
            }
        }
    }

    public PubLoadingView(FragmentActivity context) {
        this(context, true);
    }

    public PubLoadingView(FragmentActivity context, boolean isAddToActivity) {
        if (context != null) {
            mStatusTipView = LayoutInflater.from(context).inflate(R.layout.view_pub_loading, null);
            UtilitySecurity.resetVisibility(mStatusTipView, false);

            skvPubLoading = mStatusTipView.findViewById(R.id.skvPubLoading);
            skvPubLoading.WaveDuration = animDuration;

            if (isAddToActivity) {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                context.addContentView(mStatusTipView, params);
            }
        }
    }

    public void show() {
        UtilitySecurity.resetVisibility(mStatusTipView, true);
        UtilitySecurity.resetVisibility(skvPubLoading, true);
    }

    public void hide() {
        UtilitySecurity.resetVisibility(mStatusTipView, false);
        UtilitySecurity.resetVisibility(skvPubLoading, false);
    }
}