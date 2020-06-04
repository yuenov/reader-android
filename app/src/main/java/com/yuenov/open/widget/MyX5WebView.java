package com.yuenov.open.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.AdPub;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.mHttpClient;

/**
 * 自定义X5
 * 带cookie
 * 点击网页图片跳转到原声图片集
 */
public class MyX5WebView extends WebView {

    public interface IMyX5WebView {

        /**
         * 开始加载页面
         */
        void onPageStarted();

        /**
         * 加载完标题
         */
        void onMyReceivedTitle(String title);

        /**
         * 加载完页面
         */
        void onMyPageFinish(View view);

        /**
         * 加载错误
         */
        void onError();

        /**
         * 关闭页面
         */
        void onCloseWindows();

        void onMyProgressChanged(int process);
    }

    private BaseActivity activity = null;
    private Context mContext = null;
    private IMyX5WebView iMyX5WebView = null;
    private boolean isSetting = false;
    private String defaultUserAgent = "";

    /**
     * 是否打开新页面
     */
    private boolean isOpenNew = false;

    /**
     * 延时初始化配置
     */
    private int delayMillisSetting = 20;
    private int handler_SettingX5 = 1;
    private int handler_LoadUrl = 2;

    private boolean isSetCookit = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 初始化配置
            if (msg.what == handler_SettingX5) {
                initSetting();

                initListener();

                isSetting = true;
            }
            // 打开Url
            else if (msg.what == handler_LoadUrl) {
                loadUrl(msg.obj.toString());
            }
        }
    };

    public MyX5WebView(Context context) {
        super(context);

        this.mContext = context;
        init();
    }

    public MyX5WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);

        this.mContext = context;
        init();
    }

    public MyX5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        this.mContext = context;
        init();
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public void setListener(IMyX5WebView iMyX5WebView) {
        this.iMyX5WebView = iMyX5WebView;
    }

    /**
     * 执行js代码
     */
    public void executeJavaScript(String jsCode) {
        if (!TextUtils.isEmpty(jsCode)) {
            loadUrl(jsCode);
        }
    }

    private void init() {

        // 官方demo推荐延时初始化参数
        mHandler.sendEmptyMessageAtTime(handler_SettingX5, delayMillisSetting);

//        // 全屏播放
//        try {
//            Bundle params = new Bundle();
//            params.putBoolean("supportLiteWnd", false);
//            params.putInt("DefaultVideoScreen", 2);
//            if (getX5WebViewExtension() != null) {
//                getX5WebViewExtension().invokeMiscMethod("setVideoParams", params);
//            }
//        } catch (Exception e) {
//        }
    }

    private void initSetting() {
        try {
            WebSettings webSetting = getSettings();
            webSetting.setAllowFileAccess(true);
            webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSetting.setSupportZoom(true);
            webSetting.setBuiltInZoomControls(true);
            webSetting.setSupportMultipleWindows(false);
            webSetting.setAppCacheEnabled(true);
            webSetting.setDomStorageEnabled(true);
            webSetting.setJavaScriptEnabled(true);
            webSetting.setGeolocationEnabled(true);
            webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
            webSetting.setAppCachePath(activity.getDir("appcache", 0).getPath());
            webSetting.setDatabasePath(activity.getDir("databases", 0).getPath());
            webSetting.setGeolocationDatabasePath(activity.getDir("geolocation", 0).getPath());
            webSetting.setUseWideViewPort(true);
            webSetting.setLoadWithOverviewMode(true);
            webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSetting.setMixedContentMode(0);
            }

            defaultUserAgent = getSettings().getUserAgentString();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void initListener() {
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                initUserAgent(url);

                try {
                    if (isOpenNew) {
                        return true;
                    } else {
                        Uri uri = Uri.parse(url.trim());
                        final String protocol = uri.getScheme().toLowerCase().trim();
                        if (!UtilitySecurity.isEmpty(url) && url.startsWith("tel")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            activity.startActivity(intent);
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }
                } catch (Exception ex) {
                    UtilityException.catchException(ex);

                    try {
                        return super.shouldOverrideUrlLoading(view, url);
                    } catch (Exception exex) {
                        UtilityException.catchException(exex);
                        return true;
                    }
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (iMyX5WebView != null) {
                    iMyX5WebView.onPageStarted();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (iMyX5WebView != null) {
                    iMyX5WebView.onMyReceivedTitle(view.getTitle());
                }

                if (iMyX5WebView != null) {
                    iMyX5WebView.onMyPageFinish(view);
                }
            }

            public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                           SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                String message = "SSL Certificate error.";
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                    case SslError.SSL_DATE_INVALID:
                        message = "The date of the certificate is invalid";
                        break;
                    case SslError.SSL_INVALID:
                    default:
                        message = "A generic error occurred";
                        break;
                }
                message += " Do you want to continue anyway?";

                builder.setTitle("SSL Certificate Error");
                builder.setMessage(message);

                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }

//            @Override
//            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
//                sslErrorHandler.proceed();
////                super.onReceivedSslError(webView, sslErrorHandler, sslError);
//            }
        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                MyApplication.getAppContext().startActivity(intent);
            }
        });

        // 重写此方法会导致默认展示全屏按钮,点击按钮后会有空白的bug,所以不再重写该方法
        setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                if (!TextUtils.isEmpty(s1)) {
                    AdPub.showViewOneButton(activity, s1, "确定", null);
                    jsResult.cancel();
                }

                return true;
            }

            @Override
            public void onCloseWindow(WebView webView) {
                super.onCloseWindow(webView);

                if (iMyX5WebView != null) {
                    iMyX5WebView.onCloseWindows();
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);

                if (iMyX5WebView != null) {
                    iMyX5WebView.onMyReceivedTitle(s);
                }
            }

            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);

                if (iMyX5WebView != null) {
                    iMyX5WebView.onMyProgressChanged(i);
                }
            }
        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }
        });
    }

    @Override
    public void loadUrl(String url) {

        if (TextUtils.isEmpty(url))
            return;

        if (!url.toLowerCase().trim().startsWith("http")) {
            url = "http://" + url;
        }

        // 配置过参数，直接打开
        if (isSetting) {
            initCookies(url);
            initUserAgent(url);
            super.loadUrl(url);
        }
        // 如果还没配置过参数，等待延时时间后，再打开
        else {
            Message msg = Message.obtain();
            msg.what = handler_LoadUrl;
            msg.obj = url;
            mHandler.sendMessageDelayed(msg, delayMillisSetting);
        }
    }

    /**
     * 初始化UserAgent
     */
    private void initUserAgent(String loadUrl) {

        try {
            // 阅小说的url才设置ua
            if (UtilityData.isServerInterFace(loadUrl))
                getSettings().setUserAgentString(mHttpClient.getUserAgent("yue"));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 同步cookie
     */
    public void initCookies(String url) {

        // 非服务器域名，不设cookie
        if (!UtilityData.isServerInterFace(url)) {
            return;
        }

        try {
            if (!isSetCookit) {
                final Uri uri = Uri.parse(url);
                final String domain = uri.getHost();

                CookieSyncManager.createInstance(mContext);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();

                String localUuid = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.STRING_STRING_UUID);
                if (!UtilitySecurity.isEmpty(localUuid)) {
                    cookieManager.setCookie(domain, "uuid=" + localUuid);
                }
                String localUid = EditSharedPreferences.readStringFromConfig(EditSharedPreferences.STRING_STRING_UID);
                if (!UtilitySecurity.isEmpty(localUid)) {
                    cookieManager.setCookie(domain, "uid=" + localUid);
                }
                CookieSyncManager.getInstance().sync();

                isSetCookit = true;
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}