package com.yuenov.open.utils.pops;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuenov.open.R;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareAppPop implements View.OnClickListener {

    public interface IShareAppPopPop {
        void shareWeChat();

        void shareQQ();

        void copyClipboard();

        void shareCancel();
    }

    private PopupWindow mPopupWindow;
    private ShareAppPop.IShareAppPopPop listener;

    private Activity activity;

    private View viewContent;

    @BindView(R.id.viewPopSaClose)
    protected View viewPopSaClose;

    @BindView(R.id.llPopSaWx)
    protected LinearLayout llPopSaWx;
    @BindView(R.id.ivPopSaWx)
    protected ImageView ivPopSaWx;
    @BindView(R.id.tvPopSaWx)
    protected TextView tvPopSaWx;

    @BindView(R.id.llPopSaQQ)
    protected LinearLayout llPopSaQQ;
    @BindView(R.id.ivPopSaQQ)
    protected ImageView ivPopSaQQ;
    @BindView(R.id.tvPopSaQQ)
    protected TextView tvPopSaQQ;

    @BindView(R.id.llPopSaCopy)
    protected LinearLayout llPopSaCopy;
    @BindView(R.id.ivPopSaCopy)
    protected ImageView ivPopSaCopy;
    @BindView(R.id.tvPopSaCopy)
    protected TextView tvPopSaCopy;

    @BindView(R.id.tvPopSaCancel)
    protected TextView tvPopSaCancel;

    public void showPop(final Activity activity, final ShareAppPop.IShareAppPopPop listener) {
        if (activity == null || activity.isFinishing())
            return;

        this.activity = activity;
        this.listener = listener;

        initLayout();

        initListener();

        initPop();
    }

    private void initLayout() {
        viewContent = View.inflate(activity, R.layout.view_popwindow_shareapp, null);

        ButterKnife.bind(this, viewContent);
    }

    private void initListener() {
        UtilitySecurityListener.setOnClickListener(this, viewPopSaClose);
        UtilitySecurityListener.setOnClickListener(this, llPopSaWx, ivPopSaWx, tvPopSaWx);
        UtilitySecurityListener.setOnClickListener(this, llPopSaQQ, ivPopSaQQ, tvPopSaQQ);
        UtilitySecurityListener.setOnClickListener(this, llPopSaCopy, ivPopSaCopy, tvPopSaCopy);

        UtilitySecurityListener.setOnClickListener(this, tvPopSaCancel);
    }

    private void initPop() {
        //设置布局为全屏 解决部分手机底部遮挡部分弹窗
        mPopupWindow = new PopupWindow(viewContent, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.FadeAnimationShort);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mPopupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            // 分享微信
            case R.id.llPopSaWx:
            case R.id.ivPopSaWx:
            case R.id.tvPopSaWx:
                mPopupWindow.dismiss();
                if (listener != null)
                    listener.shareWeChat();
                break;

            // 分享微信
            case R.id.llPopSaQQ:
            case R.id.ivPopSaQQ:
            case R.id.tvPopSaQQ:
                mPopupWindow.dismiss();
                if (listener != null)
                    listener.shareQQ();
                break;

            // 复制链接
            case R.id.llPopSaCopy:
            case R.id.ivPopSaCopy:
            case R.id.tvPopSaCopy:
                mPopupWindow.dismiss();
                if (listener != null)
                    listener.copyClipboard();
                break;

            // 取消
            case R.id.viewPopSaClose:
            case R.id.tvPopSaCancel:
                mPopupWindow.dismiss();
                if (listener != null)
                    listener.shareCancel();
                break;
        }
    }
}
