package com.yuenov.open.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ListView;

import com.yuenov.open.R;

public class MyListView extends ListView {
    private float mMaxHeight = 100;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyListView, 0, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int type = array.getIndex(i);
            if (type == R.styleable.MyListView_maxHeight) {
                //获得布局中限制的最大高度
                mMaxHeight = array.getDimension(type, -1);
            }
        }
        array.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取lv本身高度
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        //限制高度小于lv高度,设置为限制高度
        if (mMaxHeight <= specSize && mMaxHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Float.valueOf(mMaxHeight).intValue(),
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeight(int maxHeight)
    {
        this.mMaxHeight = maxHeight;
    }
}
