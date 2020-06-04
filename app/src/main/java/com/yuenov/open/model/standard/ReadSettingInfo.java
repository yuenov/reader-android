package com.yuenov.open.model.standard;

import com.yuenov.open.constant.ConstantPageInfo;
import com.yuenov.open.utils.UtilityMeasure;
import com.yuenov.open.widget.page.PageMode;

/**
 * 设计信息
 */
public class ReadSettingInfo {

    /**
     * 亮度背景
     */
    public int lightType = ConstantPageInfo.lightType;

    /**
     * 亮度值
     */
    public int lightValue;

    /**
     * 字体大小
     */
    public float frontSize = ConstantPageInfo.textSize;
    /**
     * 字体颜色
     */
    public int frontColor = ConstantPageInfo.textColor;
    /**
     * 字体上下行间距
     */
    public int lineSpacingExtra = UtilityMeasure.getLineSpacingExtra(frontSize);

    /**
     * 翻页动画类型
     */
    public PageMode pageAnimType = PageMode.SIMULATION;
}
