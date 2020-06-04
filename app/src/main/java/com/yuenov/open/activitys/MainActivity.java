package com.yuenov.open.activitys;

import android.Manifest;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.yuenov.open.R;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.RequestPermissions;
import com.renrui.libraries.util.UtilityPermission;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.widget.CustomRadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends ActivityGroup implements View.OnClickListener, CustomRadioGroup.OnCheckedChangeListener {

    @BindView(R.id.rgMaGroup)
    protected CustomRadioGroup rgMaGroup;

    @BindView(R.id.rlMaBookShelf)
    protected RelativeLayout rlMaBookShelf;
    @BindView(R.id.rbMaBookShelf)
    protected RadioButton rbMaBookShelf;
    @BindView(R.id.tvMaBookShelf)
    protected TextView tvMaBookShelf;

    @BindView(R.id.rlMaFind)
    protected RelativeLayout rlMaFind;
    @BindView(R.id.rbMaFind)
    protected RadioButton rbMaFind;
    @BindView(R.id.tvMaFind)
    protected TextView tvMaFind;

    @BindView(R.id.rlMaBookCity)
    protected RelativeLayout rlMaBookCity;
    @BindView(R.id.rbMaBookCity)
    protected RadioButton rbMaBookCity;
    @BindView(R.id.tvMaBookCity)
    protected TextView tvMaBookCity;

    @BindView(R.id.htMaTabHost)
    protected TabHost htMaTabHost;

    public final static String TAB_BookShelf = "BookShelf";
    public final static String TAB_Find = "Find";
    public final static String TAB_BookCity = "BookCity";

    private Intent intentA = null;
    private Intent intentB = null;
    private Intent intentC = null;

    private boolean mIsPrepareLogout = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {

//        outState.putString();

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.anim_alpha_show, R.anim.anim_alpha_hide);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initListener();

        initTabIntent();

        initTabHost();

        initData();
    }

    protected void initListener() {
        rgMaGroup.setOnCheckedChangeListener(this);

        UtilitySecurityListener.setOnClickListener(rlMaBookShelf, this);
        UtilitySecurityListener.setOnClickListener(rlMaFind, this);
        UtilitySecurityListener.setOnClickListener(rlMaBookCity, this);
    }

    private void initTabIntent() {
        intentA = new Intent(this, BookShelfActivity.class);
        intentB = new Intent(this, FindActivity.class);
        intentC = new Intent(this, BookCityActivity.class);
    }

    private void initTabHost() {

        htMaTabHost.setup(getLocalActivityManager());
        htMaTabHost.addTab(htMaTabHost.newTabSpec(TAB_BookShelf).setIndicator(TAB_BookShelf).setContent(intentA));
        htMaTabHost.addTab(htMaTabHost.newTabSpec(TAB_Find).setIndicator(TAB_Find).setContent(intentB));
        htMaTabHost.addTab(htMaTabHost.newTabSpec(TAB_BookCity).setIndicator(TAB_BookCity).setContent(intentC));
    }

    private void initData() {
        setCurrentTab(TAB_BookShelf);

        requestPermission();
    }

    private void setCurrentTab(String jumpTab) {
        try {
            htMaTabHost.setCurrentTabByTag(jumpTab);
            switch (jumpTab) {
                case TAB_BookShelf:
                    UtilitySecurity.setChecked(rbMaBookShelf, true);
                    break;
                case TAB_Find:
                    UtilitySecurity.setChecked(rbMaFind, true);
                    break;
                case TAB_BookCity:
                    UtilitySecurity.setChecked(rbMaBookCity, true);
                    break;
            }

        } catch (Exception ex) {
        }
    }

    /**
     * 去书城
     */
    public void toBookBusiness()
    {
        setCurrentTab(TAB_BookCity);
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final String[] arrRequestPermission = UtilityPermission.getRequestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, arrRequestPermission, RequestPermissions.REQUEST_CODE_Write);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onCheckedChanged(CustomRadioGroup customRadioGroup, int viewID) {
        switch (viewID) {
            case R.id.rbMaBookShelf:
                UtilitySecurity.setTextColor(tvMaBookShelf, R.color.blue_b383);
                UtilitySecurity.setTextColor(tvMaFind, R.color.gary_c5c5);
                UtilitySecurity.setTextColor(tvMaBookCity, R.color.gary_c5c5);
                break;
            case R.id.rbMaFind:
                UtilitySecurity.setTextColor(tvMaBookShelf, R.color.gary_c5c5);
                UtilitySecurity.setTextColor(tvMaFind, R.color.blue_b383);
                UtilitySecurity.setTextColor(tvMaBookCity, R.color.gary_c5c5);
                break;
            case R.id.rbMaBookCity:
                UtilitySecurity.setTextColor(tvMaBookShelf, R.color.gary_c5c5);
                UtilitySecurity.setTextColor(tvMaFind, R.color.gary_c5c5);
                UtilitySecurity.setTextColor(tvMaBookCity, R.color.blue_b383);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsPrepareLogout) {
            super.onBackPressed();
        } else {
            UtilityToasty.warning(R.string.news_exit_twice_string);
            mIsPrepareLogout = true;
        }
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (view.getId()) {
            case R.id.rlMaBookShelf:
                setCurrentTab(TAB_BookShelf);
                break;

            case R.id.rlMaFind:
                setCurrentTab(TAB_Find);
                break;

            case R.id.rlMaBookCity:
                setCurrentTab(TAB_BookCity);
                break;
        }
    }
}