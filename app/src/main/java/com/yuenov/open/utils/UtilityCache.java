package com.yuenov.open.utils;

import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbCache;
import com.renrui.libraries.util.UtilitySecurity;

public class UtilityCache {

    /**
     * 发现页
     */
    public static final String FINDINDEX = "findIndex";
    /**
     * 书城
     */
    public static final String BOOKCITY_START = "bookCity_";

    public static void saveContent(String type, String content) {
        if (UtilitySecurity.isEmpty(type) || UtilitySecurity.isEmpty(content))
            return;

        try {
            TbCache tbCache = new TbCache();
            tbCache.cType = type;
            tbCache.cContent = content;
            AppDatabase.getInstance().CacheDao().addOrUpdate(tbCache);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static String getContent(String type) {

        String findIndex = "";
        try {
            TbCache tbCache = AppDatabase.getInstance().CacheDao().getEntity(type);
            if (tbCache != null && !UtilitySecurity.isEmpty(tbCache.cContent)) {
                findIndex = tbCache.cContent;
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return findIndex;
    }
}
