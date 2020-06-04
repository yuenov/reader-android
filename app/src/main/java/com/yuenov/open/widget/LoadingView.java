package com.yuenov.open.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.model.eventBus.OnDownloadBackUpChangeEvent;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import org.greenrobot.eventbus.EventBus;

/**
 * 加载动画
 */
public class LoadingView {
    private BaseActivity mActivity;
    private LinearLayout llVlCenterArea;
    private View mStatusTipView;
    private ImageView ivVlLoading;
    private TextView tvVlText;
    private TextView tvVlBackUp;

    private Boolean isShowing = false;
    private static Animation AnimRotateLoading;

    public LoadingView(Context context) {
        this(context, true);
    }

    public LoadingView(Context context, boolean isAddToActivity) {
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;

            mStatusTipView = LayoutInflater.from(mActivity).inflate(R.layout.view_loading, null);
            UtilitySecurity.resetVisibility(mStatusTipView, View.GONE);

            llVlCenterArea = mStatusTipView.findViewById(R.id.llVlCenterArea);
            ivVlLoading = mStatusTipView.findViewById(R.id.ivVlLoading);
            tvVlText = mStatusTipView.findViewById(R.id.tvVlText);
            tvVlBackUp = mStatusTipView.findViewById(R.id.tvVlBackUp);

            // 点击后台下载，关闭进度条
            UtilitySecurityListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    OnDownloadBackUpChangeEvent eventModel = new OnDownloadBackUpChangeEvent();
                    EventBus.getDefault().post(eventModel);

                    hideProgress();
                }
            },tvVlBackUp);

            if (isAddToActivity) {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mActivity.addContentView(mStatusTipView, params);
            }
        }
    }

    public LoadingView(FragmentActivity context) {
        this(context, true);
    }

    public LoadingView(FragmentActivity context, boolean isAddToActivity) {
        if (context != null) {
            mStatusTipView = LayoutInflater.from(context).inflate(R.layout.view_loading, null);
            UtilitySecurity.resetVisibility(mStatusTipView, false);

            llVlCenterArea = mStatusTipView.findViewById(R.id.llVlCenterArea);
            ivVlLoading = mStatusTipView.findViewById(R.id.ivVlLoading);
            tvVlText = mStatusTipView.findViewById(R.id.tvVlText);
            tvVlBackUp= mStatusTipView.findViewById(R.id.tvVlBackUp);

            // 点击后台下载，关闭进度条
            UtilitySecurityListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    OnDownloadBackUpChangeEvent eventModel = new OnDownloadBackUpChangeEvent();
                    EventBus.getDefault().post(eventModel);

                    hideProgress();
                }
            },tvVlBackUp);

            if (isAddToActivity) {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                context.addContentView(mStatusTipView, params);
            }
        }
    }

    public Boolean isShowing() {
        return isShowing;
    }

    /**
     * 展示下载进度条
     */
    public void showProgress() {
        if (mStatusTipView != null && !isShowing) {
            isShowing = true;
            UtilitySecurity.setBackgroundResource(llVlCenterArea,R.drawable.bg_loading_1);

            UtilitySecurity.resetVisibility(mStatusTipView, true);
            UtilitySecurity.resetVisibility(ivVlLoading, true);
            UtilitySecurity.resetVisibility(tvVlText, false);
            UtilitySecurity.resetVisibility(tvVlBackUp, false);

            setLoading(ivVlLoading);

            mStatusTipView.bringToFront();
        }
    }

    /**
     * 展示下载进度条
     */
    public void showProgress(String loadText) {
        if (mStatusTipView != null && !isShowing) {
            isShowing = true;
            UtilitySecurity.setBackgroundResource(llVlCenterArea,R.drawable.bg_loading_1);

            UtilitySecurity.resetVisibility(mStatusTipView, true);
            UtilitySecurity.resetVisibility(ivVlLoading, true);
            UtilitySecurity.setTextEmptyIsGone(tvVlText, loadText);
            UtilitySecurity.resetVisibility(tvVlBackUp, false);

            setLoading(ivVlLoading);

            mStatusTipView.bringToFront();
        }
    }

    /**
     * 展示下载进度条
     */
    public void showDownloadProgress() {
        if (mStatusTipView != null && !isShowing) {
            isShowing = true;
            UtilitySecurity.setBackgroundResource(llVlCenterArea,R.drawable.bg_loading_1);

            UtilitySecurity.resetVisibility(mStatusTipView, true);
            UtilitySecurity.resetVisibility(ivVlLoading, true);
            UtilitySecurity.setText(tvVlText,"正在下载...");
            UtilitySecurity.resetVisibility(tvVlText, true);
            UtilitySecurity.resetVisibility(tvVlBackUp, true);

            setLoading(ivVlLoading);

            mStatusTipView.bringToFront();
        }
    }

    /**
     * 展示灰色加载进度条
     */
    public void showProgressStyle2() {

        if (mStatusTipView != null && !isShowing) {
            isShowing = true;
            UtilitySecurity.setBackgroundResource(llVlCenterArea,R.drawable.bg_loading_2);

            UtilitySecurity.resetVisibility(mStatusTipView, true);
            UtilitySecurity.resetVisibility(ivVlLoading, true);
            UtilitySecurity.resetVisibility(tvVlText, false);
            UtilitySecurity.resetVisibility(tvVlBackUp, false);

            setLoading(ivVlLoading);

            mStatusTipView.bringToFront();
        }
    }

    public void hideProgress() {
        isShowing = false;

        if (mStatusTipView != null) {
            UtilitySecurity.resetVisibility(mStatusTipView, false);
            ivVlLoading.clearAnimation();
        }
    }

    public void setProgressText(String text) {
        UtilitySecurity.setText(tvVlText, text);
    }

    public View getStatusTipView() {
        return mStatusTipView;
    }

    public void setLoading(final View ivLoading) {

        try {
            if (AnimRotateLoading == null) {
                AnimRotateLoading = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                AnimRotateLoading.setInterpolator(new LinearInterpolator());
                AnimRotateLoading.setRepeatCount(Animation.INFINITE);// 重复次数
                AnimRotateLoading.setRepeatMode(Animation.RESTART);
                AnimRotateLoading.setFillAfter(true);
                AnimRotateLoading.setDuration(900);
            }
            ivLoading.startAnimation(AnimRotateLoading);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}