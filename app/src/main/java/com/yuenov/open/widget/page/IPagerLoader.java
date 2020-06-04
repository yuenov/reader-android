package com.yuenov.open.widget.page;

import com.yuenov.open.database.tb.TbBookChapter;

public interface IPagerLoader {

    void onPreChapter(TbBookChapter newBookChapter);

    void onNextChapter(TbBookChapter newBookChapter);

    /**
     * 翻页
     */
    void onTurnPage();

    void showAd();
}
