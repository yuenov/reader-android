package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.fragments.CategoryChannelFragment;
import com.yuenov.open.model.httpModel.GetCategoryChannelHttpModel;
import com.yuenov.open.model.responseModel.CategoryChannelListResponse;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.google.android.material.tabs.TabLayout;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 分类首页
 */
public class CategoryChannelActivity extends BaseActivity implements View.OnClickListener {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CategoryChannelActivity.class);
        return intent;
    }

    @BindView(R.id.rlCcBack)
    protected RelativeLayout rlCcBack;
    @BindView(R.id.tlCcMenu)
    protected TabLayout tlCcMenu;
    @BindView(R.id.vpCcContent)
    protected ViewPager vpCcContent;

    protected CategoryChannelListResponse res;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_categorychannel;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, rlCcBack);
    }

    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {
        GetCategoryChannelHttpModel httpModel = new GetCategoryChannelHttpModel();
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
                getPubLoadingView().show();
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    return;
                }

                try {
                    setResponse(s);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {
                UtilityToasty.error(s);
            }

            @Override
            public void onFinish() {
                getPubLoadingView().hide();
            }
        });
    }

    private void setResponse(String s) {
        try {
            res = mHttpClient.fromDataJson(s, CategoryChannelListResponse.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            res = null;
        }

        if (res == null || UtilitySecurity.isEmpty(res.channels)) {
            UtilityToasty.error(R.string.info_loaddata_error);
            return;
        }

        initFragment();
        initMenu();
    }

    private void initMenu() {
        try {
            tlCcMenu.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View view = View.inflate(getApplicationContext(), R.layout.view_menu_tablayout_title, null);
                    TextView tvMttName = view.findViewById(R.id.tvMttName);
                    UtilitySecurity.setText(tvMttName, res.channels.get(tab.getPosition()).channelName);
                    tab.setCustomView(view);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.setCustomView(null);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            tlCcMenu.setupWithViewPager(vpCcContent);

            // 设置title
            for (int i = 0; i < res.channels.size(); i++) {
                tlCcMenu.getTabAt(i).setText(res.channels.get(i).channelName);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void initFragment() {
        try {
            List<CategoryChannelFragment> fragments = new ArrayList<>();
            CategoryChannelFragment fragment;
            for (int i = 0; i < res.channels.size(); i++) {
                fragment = CategoryChannelFragment.getFragment(res.channels.get(i).channelId, res.channels.get(i).categories);
                fragments.add(fragment);
            }

            vpCcContent.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return fragments.get(position);
                }

                @Override
                public int getCount() {
                    return res.channels.size();
                }
            });
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (view.getId()) {
            case R.id.rlCcBack:
                onBackPressed();
                break;
        }
    }
}
