package com.yuenov.open.activitys;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.fragments.BookCityFragment;
import com.yuenov.open.utils.UtilityAppConfig;
import com.yuenov.open.utils.UtilityException;
import com.google.android.material.tabs.TabLayout;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookCityActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rlBcCategoryChannel)
    protected RelativeLayout rlBcCategoryChannel;
    @BindView(R.id.rlBcBcSearch)
    protected RelativeLayout rlBcBcSearch;
    @BindView(R.id.tlBcMenu)
    protected TabLayout tlBcMenu;
    @BindView(R.id.vpBcContent)
    protected ViewPager vpBcContent;

    private String[] arrCategoryName;
    private int[] arrCategoryID;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bookcity;
    }

    @Override
    protected void initExtra() {
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this,rlBcCategoryChannel,rlBcBcSearch);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        initMenuData();

        initFragment();
    }

    private void initMenuData() {
        try {
            arrCategoryName = new String[UtilityAppConfig.getInstant().categories.size()];
            arrCategoryID = new int[UtilityAppConfig.getInstant().categories.size()];
            for (int i = 0; i < UtilityAppConfig.getInstant().categories.size(); i++) {
                arrCategoryName[i] = UtilityAppConfig.getInstant().categories.get(i).categoryName;
                arrCategoryID[i] = UtilityAppConfig.getInstant().categories.get(i).categoryId;
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void initFragment() {
        try {
            List<BookCityFragment> fragments = new ArrayList<>();
            BookCityFragment bookCityFragment;
            for (int i = 0; i < arrCategoryName.length; i++) {
                bookCityFragment = BookCityFragment.getFragment(arrCategoryName[i], arrCategoryID[i]);
                fragments.add(bookCityFragment);
            }

            vpBcContent.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return fragments.get(position);
                }

                @Override
                public int getCount() {
                    return arrCategoryName.length;
                }
            });

            tlBcMenu.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
                View view;
                TextView tvMbtName;

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    view = View.inflate(getApplicationContext(), R.layout.view_menu_bookcity_tablayout_title, null);
                    tvMbtName = view.findViewById(R.id.tvMbtName);
                    UtilitySecurity.setText(tvMbtName, arrCategoryName[tab.getPosition()]);
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

            tlBcMenu.setupWithViewPager(vpBcContent);

            // 设置title
            for (int i = 0; i < arrCategoryName.length; i++) {
                tlBcMenu.getTabAt(i).setText(arrCategoryName[i]);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void toCategoryChannel() {
        Intent intent = CategoryChannelActivity.getIntent(this);
        startActivity(intent);
    }

    private void toSearch() {
        Intent intent = SearchActivity.getIntent(this);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (getParent() instanceof MainActivity) {
            getParent().onBackPressed();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick())
            return;

        if (getStatusTip().isShowing())
            return;

        switch (view.getId()) {
            case R.id.rlBcCategoryChannel:
                toCategoryChannel();
                break;

            case R.id.rlBcBcSearch:
                toSearch();
                break;
        }
    }
}
