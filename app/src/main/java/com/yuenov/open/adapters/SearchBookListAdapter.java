package com.yuenov.open.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.constant.AboutChapterStatus;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookShelf;
import com.yuenov.open.model.eventBus.OnBookShelfChangeEvent;
import com.yuenov.open.model.standard.CategoriesListItem;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.images.UtilityImage;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.renrui.libraries.util.UtilityControl;
import com.renrui.libraries.util.UtilitySecurity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索图书页 搜索列表
 */
public class SearchBookListAdapter extends BaseQuickAdapter<CategoriesListItem, BaseViewHolder> {

    public interface ISearchBookListAdapterListener {
        void onBookAddBookShelfClick(CategoriesListItem item);

        void onBookReadClick(CategoriesListItem item);
    }

    private String howWords;

    public void setHotWordsText(String value) {
        this.howWords = value;
    }

    private List<String> lisTag = new ArrayList<>();
    private View viewTag;
    private TextView tvIsltlName;

    private ISearchBookListAdapterListener listener;

    public void setListener(ISearchBookListAdapterListener listener) {
        this.listener = listener;
    }

    public SearchBookListAdapter(List<CategoriesListItem> inviteList) {
        super(R.layout.view_adapter_searchlist_book, inviteList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CategoriesListItem item) {

        helper.setVisible(R.id.viewAdSlLine, helper.getAdapterPosition() > 0);

        com.makeramen.roundedimageview.RoundedImageView rivAdSlCoverImg = helper.getView(R.id.rivAdSlCoverImg);
        UtilityImage.setImage(rivAdSlCoverImg, item.coverImg, R.mipmap.ic_book_list_default);

        // 标题热词标红
        TextView tvAdSlTitle = helper.getView(R.id.tvAdSlTitle);
        UtilitySecurity.setText(tvAdSlTitle, item.title);
        if (!UtilitySecurity.isEmpty(this.howWords))
            UtilityControl.setHotWordsText(tvAdSlTitle, item.title, this.howWords, R.color.red_4d52);
        helper.setText(R.id.tvAdSlAuthor, item.author);
        helper.setText(R.id.tvAdSlDesc, item.desc);

        lisTag.clear();

        if (UtilitySecurity.equalsIgnoreCase(item.chapterStatus, AboutChapterStatus.SERIALIZE)) {
            lisTag.add(MyApplication.getAppContext().getString(R.string.AboutChapterStatus_serialize));
        } else {
            lisTag.add(MyApplication.getAppContext().getString(R.string.AboutChapterStatus_end));
        }
        if (!UtilitySecurity.isEmpty(item.word))
            lisTag.add(item.word);
        if (!UtilitySecurity.isEmpty(item.categoryName))
            lisTag.add(item.categoryName);
        TagFlowLayout tagFlowLayout = helper.getView(R.id.tflAdSlHistory);
        tagFlowLayout.setAdapter(new TagAdapter(lisTag) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                try {
                    viewTag = View.inflate(MyApplication.getAppContext(), R.layout.view_item_search_list_tag, null);
                    tvIsltlName = viewTag.findViewById(R.id.tvIsltlName);
                    UtilitySecurity.setText(tvIsltlName, lisTag.get(position));
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
                return viewTag;
            }
        });

        UtilitySecurity.resetVisibility(helper.getView(R.id.llAdSlOpreation), helper.getAdapterPosition() == 0);
        if (helper.getAdapterPosition() == 0) {
            TextView tvAdSlAdd = helper.getView(R.id.tvAdSlAdd);
            boolean bookShelfExists = AppDatabase.getInstance().BookShelfDao().exists(item.bookId);
            setAddBookShelfStyle(tvAdSlAdd, bookShelfExists);
        }

        // 图书点击事件
        helper.addOnClickListener(
                R.id.rlAdSlContent, R.id.rivAdSlCoverImg,
                R.id.llAdSlContent, R.id.tvAdSlTitle, R.id.tvAdSlAuthor,
                R.id.tvAdSlDesc, R.id.tflAdSlHistory);

        // 加入书架
        helper.getView(R.id.tvAdSlAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvAdSlAdd = helper.getView(R.id.tvAdSlAdd);
                addBookShelf(tvAdSlAdd, item);
            }
        });
        // 开始阅读
        helper.getView(R.id.tvAdSlStartRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onBookReadClick(item);
            }
        });
    }

    private void setAddBookShelfStyle(TextView tvAdSlAdd, boolean isAdd) {
        if (isAdd) {
            UtilitySecurity.setText(tvAdSlAdd, MyApplication.getAppContext().getString(R.string.SearchBookListAdapter_InBookShelf));
            UtilitySecurity.setTextColor(tvAdSlAdd, R.color.gray_9a9a);
            UtilitySecurity.setEnabled(tvAdSlAdd, false);
        } else {
            UtilitySecurity.setText(tvAdSlAdd, MyApplication.getAppContext().getString(R.string.SearchBookListAdapter_addBookShelf));
            UtilitySecurity.setTextColor(tvAdSlAdd, R.color._b383);
            UtilitySecurity.setEnabled(tvAdSlAdd, true);
        }
    }

    private void addBookShelf(TextView tvAdSlAdd, CategoriesListItem item) {
        try {
            // 添加到数据库
            TbBookShelf tbBookShelf = new TbBookShelf();
            tbBookShelf.bookId = item.bookId;
            tbBookShelf.title = item.title;
            tbBookShelf.author = item.author;
            tbBookShelf.coverImg = item.coverImg;
            tbBookShelf.addTime = System.currentTimeMillis();
            AppDatabase.getInstance().BookShelfDao().addOrUpdate(tbBookShelf);

            // 通知相关区域
            OnBookShelfChangeEvent eventModel = new OnBookShelfChangeEvent();
            eventModel.addTbBookShelf = tbBookShelf;
            EventBus.getDefault().post(eventModel);

            // 修改按钮样式
            setAddBookShelfStyle(tvAdSlAdd, true);

            if (listener != null)
                listener.onBookAddBookShelfClick(item);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
