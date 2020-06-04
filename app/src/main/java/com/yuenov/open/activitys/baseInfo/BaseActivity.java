package com.yuenov.open.activitys.baseInfo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yuenov.open.R;
import com.yuenov.open.activitys.FirstActivity;
import com.yuenov.open.model.eventBus.OnAppActiveChangeEvent;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.widget.MyAppTitle;
import com.yuenov.open.widget.LoadingView;
import com.yuenov.open.widget.PubLoadingView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    // app是否在前台
    private static boolean isActive = false;

    private LoadingView mLoadingView = null;
    private PubLoadingView pubLoadingView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isActive = true;

        if (getLayoutId() > 0)
            setContentView(getLayoutId());

//        getSupportActionBar().hide();

        ButterKnife.bind(this);

        // 注册监听
        registerEventBus();

        initExtra();

        initListener();

        initData();
    }

    protected abstract int getLayoutId();

    protected abstract void initExtra();

    protected abstract void initListener();

    protected abstract void initData();

    /**
     * 获取加载动画对象
     */
    public LoadingView getStatusTip() {
        if (mLoadingView == null) {
            mLoadingView = new LoadingView(this);
        }

        return mLoadingView;
    }

    /**
     * 获取加载动画对象
     */
    public PubLoadingView getPubLoadingView() {
        if (pubLoadingView == null) {
            pubLoadingView = new PubLoadingView(this);
        }

        return pubLoadingView;
    }

    /**
     * 初始化title
     */
    public void initMyAppTitle(int titleSourceID) {
        initMyAppTitle(getString(titleSourceID));
    }

    /**
     * 初始化title
     */
    public void initMyAppTitle(String titleName) {
        MyAppTitle myAppTitle;

        try {
            myAppTitle = this.findViewById(R.id.myAppTitle);
            if (myAppTitle == null) {
                return;
            }

            myAppTitle.initViewsVisible(true, true, false, false);
            myAppTitle.setAppTitle(titleName);
            myAppTitle.setOnLeftButtonClickListener(new MyAppTitle.OnLeftButtonClickListener() {
                @Override
                public void onLeftButtonClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 检查类中有是否有onEvent方法
     */
    protected boolean hasMethodOnEvent() {
        boolean value = false;

        try {
            Method[] methods = this.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase("onEvent")) {
                    value = true;
                    break;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(this, ex);
        }

        return value;
    }

    private void registerEventBus() {
        try {
            if (!EventBus.getDefault().isRegistered(this) && hasMethodOnEvent()) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception ex) {
            UtilityException.catchException(this, ex);
        }
    }

    private void unRegisterEventBus() {
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception ex) {
            UtilityException.catchException(this, ex);
        }
    }

    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //app 从后台唤醒，进入前台
        if (!isActive) {
            isActive = true;

            OnAppActiveChangeEvent event = new OnAppActiveChangeEvent();
            EventBus.getDefault().post(event);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //app 进入后台
        if (!isAppOnForeground()
                && !this.getClass().getName().equalsIgnoreCase(FirstActivity.class.getName())) {
            isActive = false;
        }
    }

    @Override
    protected void onDestroy() {

        try {
            unRegisterEventBus();
        } catch (Exception ex) {
            UtilityException.catchException(this, ex);
        }

        super.onDestroy();
    }
}
