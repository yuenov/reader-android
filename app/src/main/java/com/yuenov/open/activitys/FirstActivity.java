package com.yuenov.open.activitys;

import android.content.Intent;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.constant.ConstantSetting;
import com.yuenov.open.utils.UtilityAppConfig;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.widget.TimeView;
import com.renrui.libraries.util.LibUtility;

public class FirstActivity extends BaseActivity implements TimeView.ITime {

    private TimeView timeView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_first;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        timeView = new TimeView();
        timeView.setInterval(ConstantSetting.first_WaitTime);
        timeView.setListener(this);
        timeView.start();

        LibUtility.initStatusHeight(this);

        // 更新配置信息
        UtilityAppConfig.updateConfigInfo();
    }

    @Override
    public void timeCycle(int counts) {

        try {
            timeView.stop();
            finish();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
