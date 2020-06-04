package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.fragments.FeedBackLackBookFragment;
import com.yuenov.open.fragments.FeedBackProductQuestionFragment;
import com.yuenov.open.fragments.FeedBookQuestionFragment;
import com.yuenov.open.utils.UtilityException;
import com.google.android.material.tabs.TabLayout;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FeedBackActivity extends BaseActivity {

    // 类型
    private static final String EXTRA_STRING_TYPE = "type";
    // 2个type：产品问题，缺少书籍
    private static final String EXTRA_STRING_TYPE_MYINFO = "MyInfo";
    // 3个type：书籍纠错，产品问题，缺少书籍
    private static final String EXTRA_STRING_TYPE_BOOKQUESTION = "BookQuestion";
    // 2个type：产品问题，缺少书籍
    private static final String EXTRA_STRING_TYPE_OTHER = "other";
    private String type;

    // 图书id
    private static final String EXTRA_INT_BOOKID = "bookId";
    private int bookId;
    // 章节id
    private static final String EXTRA_LONG_CHAPTERID = "chapterId";
    private long chapterId;

    // 默认缺少的图书
    private static final String EXTRA_STRING_LARKBOOKTITLE = "larkBookTitle";
    private String larkBookTitle;
    // 默认页面索引
    private static final String EXTRA_INT_DEFAULTPOSITION = "defaultPosition";
    private int defaultPosition;

    public static Intent getIntentByLarkBook(Context context, String larkBookTitle) {
        Intent intent = new Intent(context, FeedBackActivity.class);
        intent.putExtra(EXTRA_STRING_TYPE, EXTRA_STRING_TYPE_OTHER);
        if (!UtilitySecurity.isEmpty(larkBookTitle)) {
            intent.putExtra(EXTRA_STRING_LARKBOOKTITLE, larkBookTitle);
        }
        intent.putExtra(EXTRA_INT_DEFAULTPOSITION, 1);
        return intent;
    }

    public static Intent getIntentByBookQuestion(Context context, int bookId, long chapterId) {
        Intent intent = new Intent(context, FeedBackActivity.class);
        intent.putExtra(EXTRA_STRING_TYPE, EXTRA_STRING_TYPE_BOOKQUESTION);
        intent.putExtra(EXTRA_INT_BOOKID, bookId);
        intent.putExtra(EXTRA_LONG_CHAPTERID, chapterId);
        return intent;
    }

    public static Intent getIntentByMyInfo(Context context) {
        Intent intent = new Intent(context, FeedBackActivity.class);
        intent.putExtra(EXTRA_STRING_TYPE, EXTRA_STRING_TYPE_MYINFO);
        return intent;
    }

    @BindView(R.id.tlFbMenu)
    protected TabLayout tlFbMenu;
    @BindView(R.id.vpFbContent)
    protected ViewPager vpFbContent;

    private String[] arrMenuTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initExtra() {
        type = UtilitySecurity.getExtrasString(getIntent(), EXTRA_STRING_TYPE);

        bookId = UtilitySecurity.getExtrasInt(getIntent(), EXTRA_INT_BOOKID);
        chapterId = UtilitySecurity.getExtrasLong(getIntent(), EXTRA_LONG_CHAPTERID);

        larkBookTitle = UtilitySecurity.getExtrasString(getIntent(), EXTRA_STRING_LARKBOOKTITLE);
        defaultPosition = UtilitySecurity.getExtrasInt(getIntent(), EXTRA_INT_DEFAULTPOSITION, 0);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        initMyAppTitle(R.string.FeedBackActivity_title);

        if (UtilitySecurity.equals(type, EXTRA_STRING_TYPE_MYINFO)) {
            arrMenuTitle = new String[]{
                    getString(R.string.feedback_title_productQuestion),
                    getString(R.string.feedback_title_lackBook)};
        } else if (UtilitySecurity.equals(type, EXTRA_STRING_TYPE_BOOKQUESTION)) {
            arrMenuTitle = new String[]{
                    getString(R.string.feedback_title_bookProblem),
                    getString(R.string.feedback_title_productQuestion),
                    getString(R.string.feedback_title_lackBook)};
        } else if (UtilitySecurity.equals(type, EXTRA_STRING_TYPE_OTHER)) {
            arrMenuTitle = new String[]{
                    getString(R.string.feedback_title_productQuestion),
                    getString(R.string.feedback_title_lackBook)};
        }

        initFragment();
        initMenu();

        // 默认选中页面
        vpFbContent.setCurrentItem(defaultPosition);
    }

    private void initMenu() {
        try {
            tlFbMenu.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View view = View.inflate(getApplicationContext(), R.layout.view_menu_tablayout_title, null);
                    TextView tvMttName = view.findViewById(R.id.tvMttName);
                    UtilitySecurity.setText(tvMttName, arrMenuTitle[tab.getPosition()]);
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

            tlFbMenu.setupWithViewPager(vpFbContent);

            // 设置title
            for (int i = 0; i < arrMenuTitle.length; i++) {
                tlFbMenu.getTabAt(i).setText(arrMenuTitle[i]);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void initFragment() {
        try {
            List<Fragment> fragments = new ArrayList<>();
            for (int i = 0; i < arrMenuTitle.length; i++) {

                if (UtilitySecurity.equalsIgnoreCase(getString(R.string.feedback_title_bookProblem), arrMenuTitle[i])) {
                    FeedBookQuestionFragment bookQuestionFragment = FeedBookQuestionFragment.getFragment(bookId, chapterId);
                    fragments.add(bookQuestionFragment);
                }
                // 产品问题
                else if (UtilitySecurity.equalsIgnoreCase(getString(R.string.feedback_title_productQuestion), arrMenuTitle[i])) {
                    FeedBackProductQuestionFragment productQuestionFragment = FeedBackProductQuestionFragment.getFragment();
                    fragments.add(productQuestionFragment);
                }
                // 缺少图书
                else if (UtilitySecurity.equalsIgnoreCase(getString(R.string.feedback_title_lackBook), arrMenuTitle[i])) {
                    FeedBackLackBookFragment lackBookFragment = FeedBackLackBookFragment.getFragment(larkBookTitle);
                    fragments.add(lackBookFragment);
                }
            }

            vpFbContent.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return fragments.get(position);
                }

                @Override
                public int getCount() {
                    return arrMenuTitle.length;
                }
            });
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
