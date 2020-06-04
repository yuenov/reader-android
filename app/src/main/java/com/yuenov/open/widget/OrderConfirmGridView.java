package com.yuenov.open.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 解决嵌套中只显示一行的问题
 */
public class OrderConfirmGridView extends GridView {

    public OrderConfirmGridView(Context context) {
        super(context);
    }

    public OrderConfirmGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderConfirmGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 改写gridview高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}