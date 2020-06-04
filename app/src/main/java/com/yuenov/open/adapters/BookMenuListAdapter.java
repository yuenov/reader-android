package com.yuenov.open.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.database.tb.TbBookChapter;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录列表
 */
public class BookMenuListAdapter extends BaseAdapter {

    private List<TbBookChapter> list;

    public BookMenuListAdapter(List<TbBookChapter> lisValue) {
        if (UtilitySecurity.isEmpty(lisValue))
            list = new ArrayList<>();
        else
            this.list = lisValue;
    }

    @Override
    public int getCount() {
        return list.size();
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
            view = View.inflate(MyApplication.getAppContext(), R.layout.view_adapter_item_bookmenu, null);
            viewHolder.tvAibmName = view.findViewById(R.id.tvAibmName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        UtilitySecurity.setText(viewHolder.tvAibmName, list.get(i).chapterName);
        UtilitySecurity.setTextColor(viewHolder.tvAibmName, UtilitySecurity.isEmpty(list.get(i).content) ? R.color.gray_9999 : R.color.gray_3333);

        return view;
    }

    class ViewHolder {
        TextView tvAibmName;
    }
}
