package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.model.httpModel.PrivacyPolicyHttpModel;
import com.yuenov.open.model.httpModel.PrivacyUseragreementHttpModel;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import butterknife.BindView;

public class AboutMeActivity extends BaseActivity implements View.OnClickListener {

    public static Intent getIntent(Context context) {
        return new Intent(context, AboutMeActivity.class);
    }

    @BindView(R.id.rivAbmIcon)
    protected com.makeramen.roundedimageview.RoundedImageView rivAbmIcon;
    @BindView(R.id.tvAbmVersion)
    protected TextView tvAbmVersion;

    @BindView(R.id.rlAbmFwxy)
    protected RelativeLayout rlAbmFwxy;
    @BindView(R.id.tvAbmFwxy)
    protected TextView tvAbmFwxy;
    @BindView(R.id.ivAbmFwxy)
    protected ImageView ivAbmFwxy;

    @BindView(R.id.rlAbmYsxy)
    protected RelativeLayout rlAbmYsxy;
    @BindView(R.id.tvAbmYsxy)
    protected TextView tvAbmYsxy;
    @BindView(R.id.ivAbmYsxy)
    protected ImageView ivAbmYsxy;

    private long time = 0;
    private int clickCounts = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aboutme;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, rivAbmIcon, rlAbmFwxy, tvAbmFwxy, ivAbmFwxy);
        UtilitySecurityListener.setOnClickListener(this, rlAbmYsxy, tvAbmYsxy, ivAbmYsxy);
    }

    @Override
    protected void initData() {
        initMyAppTitle(R.string.AboutMeActivity_appTitle);

        initVersionCode();
    }

    private void initVersionCode() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            UtilitySecurity.setText(tvAbmVersion, pi.versionName);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void fwxy() {
        String url = new PrivacyUseragreementHttpModel().getUrl();
        startActivity(BrowserActivity.getIntent(this, url));
    }

    private void ysxy() {
        String url = new PrivacyPolicyHttpModel().getUrl();
        startActivity(BrowserActivity.getIntent(this, url));
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            case R.id.rlAbmFwxy:
            case R.id.tvAbmFwxy:
            case R.id.ivAbmFwxy:
                fwxy();
                break;

            case R.id.rlAbmYsxy:
            case R.id.tvAbmYsxy:
            case R.id.ivAbmYsxy:
                ysxy();
                break;
        }
    }
}
