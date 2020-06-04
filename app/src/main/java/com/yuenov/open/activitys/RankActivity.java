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
import com.yuenov.open.fragments.RankListFragment;
import com.yuenov.open.model.httpModel.GetRankListHttpModel;
import com.yuenov.open.model.responseModel.RankItemResponse;
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
 * 榜单
 */
public class RankActivity extends BaseActivity implements View.OnClickListener {

    public static Intent getIntent(Context context) {
        return new Intent(context, RankActivity.class);
    }

    @BindView(R.id.rlRkBack)
    protected RelativeLayout rlRkBack;
    @BindView(R.id.tlRkMenu)
    protected TabLayout tlRkMenu;
    @BindView(R.id.vpRkContent)
    protected ViewPager vpRkContent;

    protected RankItemResponse res;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rank;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, rlRkBack);
    }

    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {
        GetRankListHttpModel httpModel = new GetRankListHttpModel();
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
//                getStatusTip().showProgress();
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
//                getStatusTip().hideProgress();
                getPubLoadingView().hide();
            }
        });
    }

    private void setResponse(String s) {
        try {
            res = mHttpClient.fromDataJson(s, RankItemResponse.class);
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
            tlRkMenu.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
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

            tlRkMenu.setupWithViewPager(vpRkContent);

            // 设置title
            for (int i = 0; i < res.channels.size(); i++) {
                tlRkMenu.getTabAt(i).setText(res.channels.get(i).channelName);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void initFragment() {
        try {
            List<RankListFragment> fragments = new ArrayList<>();
            RankListFragment fragment;
            for (int i = 0; i < res.channels.size(); i++) {
                fragment = RankListFragment.getFragment(res.channels.get(i));
                fragments.add(fragment);
            }

            vpRkContent.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
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
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            case R.id.rlRkBack:
                onBackPressed();
                break;
        }
    }
}
