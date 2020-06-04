package com.yuenov.open.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.yuenov.open.constant.ConstantPageInfo;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.model.PageInfoModel;
import com.yuenov.open.model.TextModel;
import com.yuenov.open.model.standard.ReadSettingInfo;
import com.yuenov.open.widget.ContentTextView;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * 测量相关
 */
public class UtilityMeasure {

    public static final String testWord = "中";
    //  一行最多可以展示多少个"中"
    private static int pubLineShowWords;

    private static Rect testWordRect = null;


    /**
     * 获取测试字再控件中的高度
     *
     * @return
     */
    private static int getTestWordHeight(TextModel textModel) {

        if (testWordRect == null) {
            Paint testWordPaint = new Paint();
            testWordPaint.setTextSize(Utility.dip2px(textModel.textSize));
            testWordPaint.setAntiAlias(true);
            testWordRect = new Rect();
            testWordPaint.getTextBounds(UtilityMeasure.testWord, 0, UtilityMeasure.testWord.length(), testWordRect);
        }

        return testWordRect.height();
    }

    /**
     * 获取行间距 (字体大小减8)
     *
     * @param textSize
     * @return
     */
    public static int getLineSpacingExtra(float textSize) {
        int lineSpacingExtra = 0;

        try {
            testWordRect = null;
            TextModel textModel = new TextModel();
            textModel.textSize = textSize - 8;
            int contentHeight = getTestWordHeight(textModel);
            lineSpacingExtra = contentHeight;
//            lineSpacingExtra = (int) (contentHeight * 0.5);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return lineSpacingExtra;
    }

    /**
     * 测量展示页数信息
     *
     * @param bookChapter
     * @param ctContent
     * @return
     */
    public static List<PageInfoModel> getPageInfos(TbBookChapter bookChapter, ReadSettingInfo settingInfo, View ctContent) {

        List<PageInfoModel> lisPages = new ArrayList<>();

        if (bookChapter == null
                || UtilitySecurity.isEmpty(bookChapter.content)
                || ctContent == null
                || settingInfo == null
                || ctContent == null)
            return lisPages;

        float chapterTextSize = ConstantPageInfo.tipTextSize;
        float titleTextSize = settingInfo.frontSize * 1.3f;
        float contentTextSize = settingInfo.frontSize;

        List<TextModel> lisText = new ArrayList<>();
        TextModel textModel;
        int start;

        // 添加标题 (标题字体大小是普通文字的1.3倍)
        start = 0;
        textModel = new TextModel();
        textModel.textSize = contentTextSize;
        textModel.height = getTestWordHeight(textModel);
        lisText.add(textModel);

        while (true) {
            testWordRect = null;
            textModel = getShowLines(bookChapter.chapterName, titleTextSize, true, start, settingInfo, ctContent);
            if (textModel == null || textModel.textLength <= 0) {
                break;
            }

            textModel.isTitle = true;
            textModel.textSize = titleTextSize;
            textModel.fakeBoldText = true;
            lisText.add(textModel);
            start += textModel.textLength;
        }

        // 添加内容
        testWordRect = null;
        final String[] arrWrapContent = bookChapter.content.split("\n");
        for (int i = 0; i < arrWrapContent.length; i++) {

            start = 0;
            for (int linePosition = 0; ; linePosition++) {
                // 换行
                if (UtilitySecurity.isEmpty(arrWrapContent[i])) {
                    textModel = new TextModel();
                    textModel.textSize = contentTextSize;
                    textModel.height = getTestWordHeight(textModel);

                    lisText.add(textModel);
                    break;
                }

                // 正常数据
                textModel = getShowLines(arrWrapContent[i], contentTextSize, false, start, settingInfo, ctContent);
                if (textModel == null || textModel.textLength <= 0) {
                    break;
                }

                textModel.partFirstLine = (linePosition == 0);
                textModel.isTitle = false;
                textModel.fakeBoldText = false;
                lisText.add(textModel);
                start += textModel.textLength;
            }
        }

        // 分页
        PageInfoModel pageInfo = new PageInfoModel();
        //  当前页高度
        int pageTextHeight = 0;
        // 可用的高度
        int ctContentWordsHeight = ctContent.getMeasuredHeight() - ctContent.getPaddingTop() - ctContent.getPaddingBottom();

        // 异常，无法获取控件高度
        if (ctContentWordsHeight <= 0)
            return lisPages;

        try {
            for (int i = 0; i < lisText.size(); i++) {
                // 首页添加标题头
                if (UtilitySecurity.isEmpty(pageInfo.lisText)) {
                    testWordRect = null;

                    textModel = new TextModel();
                    textModel.textSize = contentTextSize;
                    textModel.height = settingInfo.lineSpacingExtra;
                    pageInfo.lisText.add(textModel);
                    pageTextHeight += textModel.height;

                    textModel = new TextModel();
                    textModel.isChapter = true;
                    textModel.textSize = UtilityData.isHongMiNote7() ? chapterTextSize * 0.7f : chapterTextSize;
                    textModel.text = bookChapter.chapterName;
                    textModel.height = getTestWordHeight(textModel);
                    pageInfo.lisText.add(textModel);
                    pageTextHeight += textModel.height;

                    textModel = new TextModel();
                    textModel.textSize = contentTextSize;
                    textModel.height = (int) (settingInfo.lineSpacingExtra * 1.3f);
                    pageInfo.lisText.add(textModel);
                    pageTextHeight += textModel.height;
                }
                // 第一行以后  添加上面间距
                else {
                    // 上一行是title：加一空行
                    if (lisText.get(i - 1).isTitle) {
                        testWordRect = null;
                        textModel = new TextModel();
                        textModel.textSize = titleTextSize;
                        textModel.height = getTestWordHeight(textModel);
                        pageInfo.lisText.add(textModel);
                    }
                    // 上一行是普通文字：加上下间距
                    else {
                        textModel = new TextModel();
                        textModel.textSize = contentTextSize;
                        // 段落换行间隔大2倍
                        if (lisText.get(i).partFirstLine)
                            textModel.height = (settingInfo.lineSpacingExtra * 2);
                        else
                            textModel.height = settingInfo.lineSpacingExtra;
                        pageInfo.lisText.add(textModel);
                    }

                    pageTextHeight += textModel.height;
                }

                pageInfo.lisText.add(lisText.get(i));

                // 累加行高
                pageTextHeight += lisText.get(i).height;

                // 一页放不下时 移除最后一行，并另启一页
                if (pageTextHeight > ctContentWordsHeight) {
                    pageInfo.lisText.remove(pageInfo.lisText.size() - 1);
                    pageInfo.pages = lisPages.size() + 1;
                    pageInfo.title = bookChapter.chapterName;
                    lisPages.add(pageInfo);

                    pageInfo = new PageInfoModel();
                    pageTextHeight = 0;
                    i--;
                }
                // 最后一行，添加到最后一页
                else if (i == lisText.size() - 1) {
                    lisPages.add(pageInfo);
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        try {
            // 删掉空白页面
            boolean isEmpty;
            for (int i = lisPages.size() - 1; i >= 0; i--) {
                isEmpty = true;
                for (int j = 0; j < lisPages.get(i).lisText.size(); j++) {
                    textModel = lisPages.get(i).lisText.get(j);
                    if (!textModel.isChapter && !textModel.isTitle && !UtilitySecurity.isEmpty(textModel.text)) {
                        isEmpty = false;
                        break;
                    }
                }

                if (isEmpty)
                    lisPages.remove(i);
                else
                    break;
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return lisPages;
    }

    /**
     * 测量展示页数信息
     *
     * @param bookChapter
     * @param ctContent
     * @return
     */
    public static List<PageInfoModel> getPageInfos(TbBookChapter bookChapter, ContentTextView ctContent) {
        return getPageInfos(bookChapter, UtilityReadInfo.getReadSettingInfo(), ctContent);
    }

    /**
     * @return 获取通常情况下一行可以展示多少个"中"字
     */
    public static int getPubShowLineWords(ReadSettingInfo settingInfo, View ctContent) {

        if (pubLineShowWords == 0) {

            int showWidth = ctContent.getMeasuredWidth() - ctContent.getPaddingLeft() - ctContent.getPaddingRight();

            Paint paint = new Paint();
            paint.setTextSize(Utility.dip2px(settingInfo.frontSize));

            // 测量用的最大宽度
            int maxWidth = showWidth * 10;
            // 测量结果
            float[] measuredWidth = new float[1];

            StringBuffer sbWords = new StringBuffer(testWord);
            int i;
            for (i = 0; ; i++) {

                paint.breakText(sbWords.toString(), true, maxWidth, measuredWidth);

                if (measuredWidth[0] < showWidth) {
                    sbWords.append(testWord);
                } else {
                    pubLineShowWords = i;
                    break;
                }
            }
        }

        return pubLineShowWords;
    }

    /**
     * @param content   内容
     * @param start
     * @param fakeBold  是否加粗
     * @param ctContent
     * @return
     */
    private static TextModel getShowLines(String content, float textSize, boolean fakeBold, int start, ReadSettingInfo settingInfo, View ctContent) {

        TextModel textModel = new TextModel();

        if (content.length() <= start)
            return textModel;

        int showWidth = ctContent.getMeasuredWidth() - ctContent.getPaddingLeft() - ctContent.getPaddingRight();

        Paint paint = new Paint();
        paint.setTextSize(Utility.dip2px(textSize));
        paint.setFakeBoldText(fakeBold);
        paint.setAntiAlias(true);

        // 通用展示字符
        int pubWords = getPubShowLineWords(settingInfo, ctContent);
        // 测量结果
        float[] measuredWidth = new float[1];

        // 可测量的字符小于通用字符数: 测量剩余所有的字符
        // 否则测量通用字符数
        StringBuffer sb = new StringBuffer();
        if ((content.length() - start) <= pubWords) {
            sb.append(content.substring(start));
        } else {
            sb.append(content.substring(start, start + pubWords));
        }

        int maxWidth = showWidth * 10;

        // 获取超出一行可展示的字符（可能多N个字符）
        // 如果可展示的字符小于一行，累加1个字符(如果后面没有字符了，则退出)
        int beginIndex;
        while (true) {
            paint.breakText(sb.toString(), true, maxWidth, measuredWidth);
            if (measuredWidth[0] < showWidth) {

                beginIndex = start + sb.length();

                if (content.length() <= beginIndex) {
                    break;
                } else {
                    sb.append(content.substring(beginIndex, beginIndex + 1));
                }
            } else {
                break;
            }
        }

        // 获取一行可展示的字符
        // 如果可展示的字符大于一行，累减1个字符(如果没有字符了，则退出)
        while (true) {
            paint.breakText(sb.toString(), true, maxWidth, measuredWidth);
            if (measuredWidth[0] > showWidth) {
                if (sb.length() <= 1)
                    break;
                else
                    sb.deleteCharAt(sb.length() - 1);
            } else {
                break;
            }
        }

        textModel.text = sb.toString();
        textModel.textLength = textModel.text.length();
        textModel.textSize = textSize;

        Rect rect = new Rect();
        paint.getTextBounds(textModel.text, 0, textModel.textLength, rect);
        if (rect.height() < getTestWordHeight(textModel))
            textModel.height = getTestWordHeight(textModel);
        else
            textModel.height = rect.height();

        return textModel;
    }
}
