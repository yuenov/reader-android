package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.yuenov.open.utils.WebviewSettingProxy;
import com.yuenov.open.widget.MyX5WebView;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
//import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;

public class BrowserActivity extends BaseActivity implements MyX5WebView.IMyX5WebView, View.OnClickListener {

    private static final String EXTRA_STRING_URL = "url";
    private String url;

    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, BrowserActivity.class);
        if (!UtilitySecurity.isEmpty(url))
            intent.putExtra(EXTRA_STRING_URL, url);
        return intent;
    }

    @BindView(R.id.ivBsBack)
    protected ImageView ivBsBack;
    @BindView(R.id.ivBsClose)
    protected ImageView ivBsClose;
    @BindView(R.id.ivBsReLoad)
    protected ImageView ivBsReLoad;
    @BindView(R.id.tvBsTitle)
    protected TextView tvBsTitle;
    @BindView(R.id.pbBsProcess)
    protected ProgressBar pbBsProcess;
    @BindView(R.id.rlBsContent)
    protected RelativeLayout rlBsContent;

    private MyX5WebView x5Webview;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected void initExtra() {
        url = UtilitySecurity.getExtrasString(getIntent(), EXTRA_STRING_URL);

        if (UtilitySecurity.isEmpty(url)) {
            UtilityToasty.error(R.string.info_loaddata_error);
            finish();
        }
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, ivBsBack, ivBsClose, ivBsReLoad);
    }

    @Override
    protected void initData() {

        try {
            x5Webview = new MyX5WebView(this);
            x5Webview.setActivity(this);
            x5Webview.setListener(this);

            // 设置代理
            String proxyIp = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.STRING_STRING_PROXYIP);
            if (!UtilitySecurity.isEmpty(proxyIp))
                WebviewSettingProxy.setProxy(x5Webview, proxyIp, 8888, MyApplication.getAppContext().getClass().getName());

            rlBsContent.addView(x5Webview, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            x5Webview.loadUrl(url);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void reload() {
        try {
            x5Webview.reload();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onPageStarted() {

    }

    @Override
    public void onMyReceivedTitle(String title) {
        UtilitySecurity.setText(tvBsTitle, title);
    }

    @Override
    public void onMyPageFinish(View view) {
        UtilitySecurity.resetVisibility(ivBsClose, true);
        UtilitySecurity.resetVisibility(ivBsReLoad, true);
        UtilitySecurity.resetVisibility(tvBsTitle, true);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onCloseWindows() {
        finish();
    }

    @Override
    public void onMyProgressChanged(int process) {
        UtilitySecurity.setProgress(pbBsProcess, process);
        UtilitySecurity.resetVisibility((process >= pbBsProcess.getMax()) ? View.INVISIBLE : View.VISIBLE, pbBsProcess);
    }

    @Override
    public void onPause() {

        try {
            x5Webview.onPause();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        super.onPause();
    }

    @Override
    public void onResume() {

        try {
            x5Webview.onResume();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            if (x5Webview.getParent() != null) {
                rlBsContent.removeView(x5Webview);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        try {
            if (x5Webview.canGoBack()) {
                x5Webview.goBack();
            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            case R.id.ivBsBack:
                onBackPressed();
                break;

            case R.id.ivBsClose:
                finish();
                break;

            case R.id.ivBsReLoad:
                reload();
                break;
        }
    }
}
