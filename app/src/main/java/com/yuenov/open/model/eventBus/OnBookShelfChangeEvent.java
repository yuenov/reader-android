package com.yuenov.open.model.eventBus;

import com.yuenov.open.database.tb.TbBookShelf;

/**
 * 添加或移除书架
 */
public class OnBookShelfChangeEvent {

    public TbBookShelf addTbBookShelf;

    public int removeBookId;
}
