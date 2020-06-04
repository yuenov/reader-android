package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.ReadHistoryListAdapter;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbReadHistory;
import com.yuenov.open.model.eventBus.OnBookShelfChangeEvent;
import com.yuenov.open.utils.Utility;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.widget.MyAppTitle;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.renrui.libraries.interfaces.ITwoButtonListener;
import com.renrui.libraries.util.AdPub;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

public class ReadHistoryListActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {

    public static Intent getIntent(Context context) {
        return new Intent(context, ReadHistoryListActivity.class);
    }

    @BindView(R.id.myAppTitle)
    protected MyAppTitle myAppTitle;
    @BindView(R.id.rvRhList)
    protected RecyclerView rvRhList;

    private List<TbReadHistory> list;
    private ReadHistoryListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_readhistory;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(myAppTitle.getRightTextView(), ReadHistoryListActivity.this);
    }

    @Override
    protected void initData() {
        initMyAppTitle(R.string.ReadHistoryListActivity_appTitle);
        myAppTitle.initViewsVisible(true, true, false, true);
        UtilitySecurity.setTextColor(myAppTitle.getRightTextView(), R.color.blue_21b482);
        UtilitySecurity.setText(myAppTitle.getRightTextView(), "清空");

        loadList();
    }

    private void loadList() {
        try {
            list = AppDatabase.getInstance().ReadHistoryDao().getAllList();
            adapter = new ReadHistoryListAdapter(list);
            rvRhList.setAdapter(adapter);
            adapter.setOnItemChildClickListener(ReadHistoryListActivity.this);
            rvRhList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void clear() {

        if (UtilitySecurity.isEmpty(list))
            return;

        AdPub.showViewTwoButton(this, "确定清空浏览记录吗？", "取消", "确定", new ITwoButtonListener() {
            @Override
            public void onLeftButtonOnclick() {

            }

            @Override
            public void onRightButtonOnclick() {
                if (!UtilitySecurity.isEmpty(list)) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                    AppDatabase.getInstance().ReadHistoryDao().clear();
                }
            }
        });
    }

    /**
     * 添加或移除书架
     */
    @Subscribe
    public void onEvent(OnBookShelfChangeEvent event) {

        if (UtilitySecurity.isEmpty(list))
            return;

        try {
            if (event.removeBookId > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).bookId == event.removeBookId) {
                        list.get(i).addBookShelf = false;
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if (event.addTbBookShelf != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).bookId == event.addTbBookShelf.bookId) {
                        list.get(i).addBookShelf = true;
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        try {
            Utility.openPreviewBook(this, list.get(position).bookId);
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
            case R.id.tvRight:
                clear();
                break;
        }
    }
}
