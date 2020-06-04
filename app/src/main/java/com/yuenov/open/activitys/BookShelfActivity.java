package com.yuenov.open.activitys;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.IndexBookShelfListAdapter;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.database.tb.TbBookShelf;
import com.yuenov.open.model.eventBus.OnAppActiveChangeEvent;
import com.yuenov.open.model.httpModel.BookCheckUpdateHttpModel;
import com.yuenov.open.model.responseModel.BookCheckUpdateResponse;
import com.yuenov.open.model.standard.CheckUpdateItemInfo;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.model.standard.RequestBookCheckUpdateInfo;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.UtilityBusiness;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.dialogFragment.BdDeleteBookShelf;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookShelfActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.srlIbsList)
    protected SwipeRefreshLayout srlIbsList;
    @BindView(R.id.llIbsSearch)
    protected LinearLayout llIbsSearch;
    @BindView(R.id.ivIbsSearch)
    protected ImageView ivIbsSearch;
    @BindView(R.id.tvIbsSearch)
    protected TextView tvIbsSearch;
    @BindView(R.id.gvIbsList)
    protected GridView gvIbsList;

    private List<TbBookShelf> listBookShelf;
    private IndexBookShelfListAdapter adapter;

    private View viewEmpty;
    private TextView tvBseFind;

    private boolean isFirst = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bookshelf;
    }

    @Override
    protected void initExtra() {
    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnRefreshListener(srlIbsList, this);
        UtilitySecurityListener.setOnClickListener(this, llIbsSearch, ivIbsSearch, tvIbsSearch);
        UtilitySecurityListener.setOnItemClickListener(gvIbsList, this);

        gvIbsList.setOnItemLongClickListener(this);
    }

    @Override
    protected void initData() {

        viewEmpty = View.inflate(MyApplication.getAppContext(), R.layout.view_bookshelf_empty, null);
        tvBseFind = viewEmpty.findViewById(R.id.tvBseFind);
        ((ViewGroup) gvIbsList.getParent()).addView(viewEmpty);
        UtilitySecurityListener.setOnClickListener(tvBseFind, this);

        // 加载书架图书列表，并获取更新标识
        loadBookShelfList();

        // 检查更新
        checkBookShelfUpdate();

        openLastReadBook();
    }

    private void loadBookShelfList() {
        try {
            listBookShelf = AppDatabase.getInstance().BookShelfDao().getAllList();

            if (listBookShelf == null)
                listBookShelf = new ArrayList<>();

            adapter = new IndexBookShelfListAdapter(listBookShelf);
            gvIbsList.setEmptyView(viewEmpty);
            gvIbsList.setAdapter(adapter);

            srlIbsList.setEnabled(!UtilitySecurity.isEmpty(listBookShelf));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 打开非正常退出时 最后阅读的书
     */
    private void openLastReadBook() {
        BookBaseInfo bookBaseInfo = EditSharedPreferences.getNowReadBook();
        if (bookBaseInfo != null && bookBaseInfo.bookId > 0) {
            UtilityBusiness.toRead(this, bookBaseInfo);
        }
    }

    /**
     * 删除书架中的图书
     */
    private void deleteBookShelf(int position) {
        try {
            int bookId = listBookShelf.get(position).bookId;
            AppDatabase.getInstance().BookShelfDao().deleteByBookId(bookId);
            // 同步浏览记录
            AppDatabase.getInstance().ReadHistoryDao().resetAddBookShelfStat(bookId, false);

            listBookShelf.remove(position);
            adapter.notifyDataSetChanged();

            srlIbsList.setEnabled(!UtilitySecurity.isEmpty(listBookShelf));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void toBookList() {
        try {
            MainActivity mainActivity = (MainActivity) getParent();
            mainActivity.toBookBusiness();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void checkBookShelfUpdate() {

        List<TbBookChapter> lisUpdateInfo = AppDatabase.getInstance().ChapterDao().getShelfUpdateInfo();
        if (UtilitySecurity.isEmpty(lisUpdateInfo)) {
            UtilitySecurity.setRefreshing(srlIbsList, false);
            return;
        }

        RequestBookCheckUpdateInfo model = new RequestBookCheckUpdateInfo();
        model.books = new ArrayList<>();
        CheckUpdateItemInfo modelItem;
        for (int i = 0; i < lisUpdateInfo.size(); i++) {
            if (lisUpdateInfo.get(i).bookId > 0 && lisUpdateInfo.get(i).chapterId > 0) {
                modelItem = new CheckUpdateItemInfo();
                modelItem.bookId = lisUpdateInfo.get(i).bookId;
                modelItem.chapterId = lisUpdateInfo.get(i).chapterId;
                model.books.add(modelItem);
            }
        }

        BookCheckUpdateHttpModel httpModel = new BookCheckUpdateHttpModel();
        httpModel.setIsPostJson(true);
        httpModel.setPostJsonText(mHttpClient.GetGsonInstance().toJson(model));
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s, false)) {
                    return;
                }

                if (UtilitySecurity.isEmpty(listBookShelf))
                    return;

                try {
                    BookCheckUpdateResponse res = mHttpClient.fromDataJson(s, BookCheckUpdateResponse.class);
                    if (res == null || UtilitySecurity.isEmpty(res.updateList))
                        return;

                    boolean hasUpdate = false;
                    int bookId;
                    for (int i = 0; i < listBookShelf.size(); i++) {
                        for (int j = 0; j < res.updateList.size(); j++) {
                            if (listBookShelf.get(i).bookId == res.updateList.get(j).bookId
                                    && !listBookShelf.get(i).hasUpdate) {
                                listBookShelf.get(i).hasUpdate = true;
                                bookId = listBookShelf.get(i).bookId;
                                AppDatabase.getInstance().BookShelfDao().updateHasUpdate(bookId, true, System.currentTimeMillis());
                                hasUpdate = true;
                                continue;
                            }
                        }
                    }

                    if (hasUpdate)
                        adapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {
            }

            @Override
            public void onFinish() {
                UtilitySecurity.setRefreshing(srlIbsList, false);
            }
        });
    }

    /**
     * 后台切换到前台
     */
    @Subscribe
    public void onEvent(OnAppActiveChangeEvent event) {
        checkBookShelfUpdate();
    }

    @Override
    public void onRefresh() {
        checkBookShelfUpdate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            BookBaseInfo bookBaseInfo = new BookBaseInfo();
            bookBaseInfo.bookId = listBookShelf.get(position).bookId;
            bookBaseInfo.title = listBookShelf.get(position).title;
            bookBaseInfo.author = listBookShelf.get(position).author;
            bookBaseInfo.coverImg = listBookShelf.get(position).coverImg;
            UtilityBusiness.toRead(this, bookBaseInfo);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BdDeleteBookShelf dialog = BdDeleteBookShelf.getInstance(position);
        dialog.setListener(new BdDeleteBookShelf.IDeleteBookShelfPop() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void toPreviewDetail(int position) {
                try {
                    Intent intent = PreviewDetailActivity.getIntent(BookShelfActivity.this, listBookShelf.get(position).bookId);
                    startActivity(intent);
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void toDelete(int position) {
                deleteBookShelf(position);
            }

            @Override
            public void toCancel() {
            }
        });
        dialog.show(getSupportFragmentManager(), BookShelfActivity.class.getSimpleName());

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFirst) {
            loadBookShelfList();
        }

        isFirst = false;
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
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        Intent intent = null;

        switch (v.getId()) {
            case R.id.llIbsSearch:
            case R.id.ivIbsSearch:
            case R.id.tvIbsSearch:
                intent = SearchActivity.getIntent(this);
                break;

            // 去找书
            case R.id.tvBseFind:
                toBookList();
                break;
        }

        if (intent != null)
            startActivity(intent);
    }
}