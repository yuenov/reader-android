package com.yuenov.open.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载列表
 */
public class BookSelectDownloadMenuListAdapter extends BaseAdapter {

    private List<String> list;

    public BookSelectDownloadMenuListAdapter(List<String> value) {
        if (UtilitySecurity.isEmpty(value))
            list = new ArrayList<>();
        else
            this.list = value;
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
            view = View.inflate(MyApplication.getAppContext(), R.layout.view_adapter_item_selectdownloadmenu, null);
            viewHolder.tvAisdName = view.findViewById(R.id.tvAisdName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        UtilitySecurity.setText(viewHolder.tvAisdName, list.get(i));

        return view;
    }

    class ViewHolder {
        TextView tvAisdName;
    }
}
