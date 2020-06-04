package com.yuenov.open.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.yuenov.open.application.MyApplication;
import com.yuenov.open.constant.ConstantPageInfo;
import com.yuenov.open.model.ContentTextViewAttributeSet;
import com.yuenov.open.model.TextModel;
import com.yuenov.open.utils.Utility;
import com.yuenov.open.utils.UtilityMeasure;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

public class ContentTextView extends View {

    private List<TextModel> lisText = new ArrayList<>();

    // 绘制属性
    private ContentTextViewAttributeSet attributeSet = new ContentTextViewAttributeSet();

    // 画笔
    private Paint paint = new Paint();
    // 通用字符串大小
    private Rect pubRect = new Rect();

    // x轴基点
    private int x;
    // y轴基点
    private int y;

    public ContentTextViewAttributeSet getAttributeSet() {
        return attributeSet;
    }

    public ContentTextView(Context context) {
        super(context);
        init();
    }

    public ContentTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContentTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        attributeSet = new ContentTextViewAttributeSet();
        attributeSet.textSize = ConstantPageInfo.textSize;
    }

    /**
     * @param value text
     */
    public void setText(List<TextModel> value) {
        this.lisText = value;
        invalidate();
    }

    public void setAttributeSet(ContentTextViewAttributeSet value) {
        if (value == null)
            return;

        this.attributeSet = value;

        // 字体颜色
        if (this.attributeSet.textColor > 0)
            paint.setColor(MyApplication.getAppContext().getResources().getColor(this.attributeSet.textColor));

        // 测量通用字符串大小
        paint.getTextBounds(UtilityMeasure.testWord, 0, 1, pubRect);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (UtilitySecurity.isEmpty(lisText))
            return;

        for (int i = 0; i < lisText.size(); i++) {

            paint.setTextSize(Utility.dip2px(lisText.get(i).textSize));
            paint.setFakeBoldText(lisText.get(i).fakeBoldText);

            // 第一行：paddingTop + 文字高度
            if (i == 0) {
                x = getPaddingLeft();
                y = getPaddingTop() + lisText.get(i).height;
            }
            // 其他行：文字高度
            else {
                y += lisText.get(i).height;
            }

            // 绘制
            if (!UtilitySecurity.isEmpty(lisText.get(i).text))
                canvas.drawText(lisText.get(i).text, x, y, paint);
        }
    }
}