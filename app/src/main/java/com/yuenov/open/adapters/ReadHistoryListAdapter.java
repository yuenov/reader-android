package com.yuenov.open.adapters;

import android.view.View;

import androidx.annotation.NonNull;

import com.yuenov.open.R;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookShelf;
import com.yuenov.open.database.tb.TbReadHistory;
import com.yuenov.open.model.eventBus.OnBookShelfChangeEvent;
import com.yuenov.open.utils.images.UtilityImage;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilityTime;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 阅读记录
 */
public class ReadHistoryListAdapter extends BaseQuickAdapter<TbReadHistory, BaseViewHolder> {

    public ReadHistoryListAdapter(List<TbReadHistory> inviteList) {
        super(R.layout.view_adapter_readhistory_item, inviteList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TbReadHistory item) {

        UtilitySecurity.resetVisibility(helper.getView(R.id.viewLineRhi), helper.getAdapterPosition() > 0);

        com.makeramen.roundedimageview.RoundedImageView rivRhiCoverImg = helper.getView(R.id.rivRhiCoverImg);
        UtilityImage.setImage(rivRhiCoverImg, item.coverImg, R.mipmap.ic_book_list_default);

        helper.setText(R.id.tvRhiTitle, item.title);
        helper.setText(R.id.tvRhiAuthor, item.author);
        helper.setText(R.id.tvRhiLastTime, UtilityTime.sdf_14.format(item.lastReadTime));

        helper.setVisible(R.id.tvRhiAddShelf, !item.addBookShelf);
        helper.setVisible(R.id.tvRhiInShelf, item.addBookShelf);

        // 加入书架
        helper.getView(R.id.tvRhiAddShelf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 同步状态到数据库
                AppDatabase.getInstance().ReadHistoryDao().resetAddBookShelfStat(item.bookId, true);
                // 发送加书架通知
                OnBookShelfChangeEvent event = new OnBookShelfChangeEvent();
                TbBookShelf tbBookShelf = new TbBookShelf();
                tbBookShelf.bookId = item.bookId;
                tbBookShelf.coverImg = item.coverImg;
                tbBookShelf.title = item.title;
                tbBookShelf.author = item.author;
                tbBookShelf.addTime = System.currentTimeMillis();
                AppDatabase.getInstance().BookShelfDao().addOrUpdate(tbBookShelf);
                event.addTbBookShelf = tbBookShelf;
                EventBus.getDefault().post(event);

                item.addBookShelf = true;
                notifyDataSetChanged();
            }
        });

        // 点击事件
        helper.addOnClickListener(R.id.llRhiContent, R.id.rivRhiCoverImg, R.id.tvRhiTitle, R.id.tvRhiAuthor, R.id.tvRhiLastTime, R.id.tvRhiInShelf);
    }
}