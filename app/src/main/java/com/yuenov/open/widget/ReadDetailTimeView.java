package com.yuenov.open.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilityTime;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * time
 */
public class ReadDetailTimeView extends LinearLayout implements TimeView.ITime {

    @BindView(R.id.tvWgRdTime)
    protected TextView tvWgRdTime;

    private TimeView timeView;

    private Calendar calendar;
    private ContentResolver mResolver;
    private String timeFormat;
    private int hour;
    private String allText;

    public ReadDetailTimeView(Context context) {
        super(context);
        init();
    }

    public ReadDetailTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReadDetailTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initLayout();

        initTimeView();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View viewContent = inflater.inflate(R.layout.view_widget_readdetail_timeview, null);

        ButterKnife.bind(this, viewContent);

        this.addView(viewContent, layoutParams);
    }

    private void initTimeView() {
        try {
            timeView = new TimeView();
            timeView.setListener(this);
            // 10秒刷新一次
            timeView.setInterval(UtilityTime.lSecondTimes * 10);

            timeView.start();
            timeCycle(0);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public void destroy() {
        if (timeView != null)
            timeView.stop();
    }

    public void setTextColor(int colorResourceId) {
        UtilitySecurity.setTextColor(tvWgRdTime, colorResourceId);
    }

    private String getShowText() {
        allText = UtilityTime.sdf_4.format(System.currentTimeMillis());

        try {
            calendar = Calendar.getInstance();
            mResolver = getContext().getContentResolver();
            timeFormat = android.provider.Settings.System.getString(mResolver, android.provider.Settings.System.TIME_12_24);
            if (UtilitySecurity.equals(timeFormat, "12")) {
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour >= 0 && hour <= 6) {
                    allText = "凌晨 " + allText;
                } else if (hour >= 7 && hour <= 11) {
                    allText = "上午 " + allText;
                } else if (hour >= 12 && hour <= 20) {
                    allText = "下午 " + allText;
                } else {
                    allText = "晚上 " + allText;
                }
            }

        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
        return allText;
    }

    @Override
    public void timeCycle(int counts) {
        UtilitySecurity.setText(tvWgRdTime, getShowText());
    }
}