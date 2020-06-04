package com.yuenov.open.utils;

import com.yuenov.open.model.standard.ReadSettingInfo;

public class UtilityReadInfo {

    private static ReadSettingInfo readSettingInfo;

    public static ReadSettingInfo getReadSettingInfo() {
        if (readSettingInfo == null)
            readSettingInfo = EditSharedPreferences.getReadSettingInfo();

        return readSettingInfo;
    }
}
