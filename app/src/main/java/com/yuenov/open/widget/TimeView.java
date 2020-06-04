package com.yuenov.open.widget;

import android.os.Handler;

public class TimeView {

    public interface ITime {
        void timeCycle(int counts);
    }

    /**
     * 递归间隔
     */
    private long interval = 1000;

    private ITime listener = null;

    /**
     * 递归次数
     */
    private int cycleCounts = 0;

    private Handler mhandle = new Handler();
    private boolean isActiveCycle = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isActiveCycle) {
                cycleCounts++;

                mhandle.postDelayed(this, interval);

                if (listener != null)
                    listener.timeCycle(cycleCounts);
            }
        }
    };

    /**
     * 设置间隔
     */
    public void setInterval(long value) {
        this.interval = value;
    }

    public void setListener(ITime listener) {
        this.listener = listener;
    }

    public void start() {
        try {
            // 已开始，不再重复
            if (isActiveCycle) {
                return;
            }

            isActiveCycle = true;

            // 首次也要等待 再回调
            mhandle.postDelayed(runnable, interval);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        isActiveCycle = false;
    }
}
