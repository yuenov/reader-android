package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.BookMenuListAdapter;
import com.yuenov.open.constant.AboutChapterStatus;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * 目录列表
 */
public class ChapterMenuListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String EXTRA_STRING_BOOKBASEINFO = "BookBaseInfo";
    private BookBaseInfo bookBaseInfo;

    public static Intent getIntent(Context context, BookBaseInfo bookBaseInfo) {
        Intent intent = new Intent(context, ChapterMenuListActivity.class);
        if (bookBaseInfo != null)
            intent.putExtra(EXTRA_STRING_BOOKBASEINFO, bookBaseInfo);
        return intent;
    }

    @BindView(R.id.tvBmTitle)
    protected TextView tvBmTitle;
    @BindView(R.id.ivBmOrder)
    protected ImageView ivBmOrder;

    @BindView(R.id.lvBmList)
    protected ListView lvBmList;

    private List<TbBookChapter> lisChapter;
    private BookMenuListAdapter adapter;
    private boolean orderDesc = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bookmenulist;
    }

    @Override
    protected void initExtra() {
        bookBaseInfo = UtilitySecurity.getExtrasSerializable(getIntent(), EXTRA_STRING_BOOKBASEINFO);

        if (bookBaseInfo == null || bookBaseInfo.bookId < 1) {
            UtilityToasty.error(R.string.Utility_unknown);
            finish();
        }
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, ivBmOrder);
        UtilitySecurityListener.setOnItemClickListener(lvBmList, this);
    }

    @Override
    protected void initData() {
        if (bookBaseInfo != null)
            initMyAppTitle(bookBaseInfo.title);

        loadData();
    }

    private void loadData() {

        try {
            lisChapter = AppDatabase.getInstance().ChapterDao().getListByBookIdOrderByAsc(bookBaseInfo.bookId);

            // 标题
            String status = getString(UtilitySecurity.equals(bookBaseInfo.chapterStatus, AboutChapterStatus.END) ? R.string.info_chapterStatus_end : R.string.info_chapterStatus_serialize);
            UtilitySecurity.setText(tvBmTitle, getString(R.string.BookMenuListActivity_Chapter, status, UtilitySecurity.size(lisChapter)));
            UtilitySecurity.resetVisibility(ivBmOrder, true);

            adapter = new BookMenuListAdapter(lisChapter);
            lvBmList.setAdapter(adapter);
        }
        catch (Exception ex)
        {
            UtilityException.catchException(ex);
        }
    }


    /**
     * 排序目录列表
     */
    private void sortMenuList() {
        if (adapter == null)
            return;

        orderDesc = !orderDesc;

        UtilitySecurity.setImageResource(ivBmOrder, orderDesc ? R.mipmap.ic_book_down : R.mipmap.ic_book_up);

        Collections.sort(lisChapter, new Comparator<TbBookChapter>() {
            @Override
            public int compare(TbBookChapter t1, TbBookChapter t2) {
                if (orderDesc)
                    return (t1.chapterId > t2.chapterId) ? 1 : -1;
                else
                    return (t1.chapterId < t2.chapterId) ? 1 : -1;
            }
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            UtilityBusiness.toRead(this, bookBaseInfo, lisChapter.get(position).chapterId);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onBackPressed() {

        if (getStatusTip().isShowing()) {
            getStatusTip().hideProgress();
            mHttpClient.cancelRequests(this);
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            case R.id.ivBmOrder:
                sortMenuList();
                break;
        }
    }
}