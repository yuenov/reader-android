package com.yuenov.open.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * 详情底部目录列表
 */
public class BookDetailBottomMenuListAdapter extends BaseAdapter {

    private List<TbBookChapter> list;
    private boolean orderByAes = true;

    public void setOrderByAes(boolean value) {
        this.orderByAes = value;
    }

    public BookDetailBottomMenuListAdapter(List<TbBookChapter> value) {
        if (UtilitySecurity.isEmpty(value))
            list = new ArrayList<>();
        else
            this.list = value;
    }

    @Override
    public int getCount() {
        return UtilitySecurity.size(list);
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(MyApplication.getAppContext(), R.layout.view_adapter_item_detailoperation_bottom_menu, null);
            viewHolder.tvAidobName = view.findViewById(R.id.tvAidobName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        try {
            TbBookChapter bookChapter = list.get(orderByAes ? i : UtilitySecurity.size(list)-1 - i);

            UtilitySecurity.setText(viewHolder.tvAidobName, bookChapter.chapterName);
            UtilitySecurity.setTextColor(viewHolder.tvAidobName, UtilitySecurity.isEmpty(bookChapter.content) ? R.color.gray_9999 : R.color.gray_3333);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return view;
    }

    class ViewHolder {
        TextView tvAidobName;
    }
}