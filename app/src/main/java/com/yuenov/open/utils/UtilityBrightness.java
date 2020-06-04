package com.yuenov.open.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class UtilityBrightness {

    /**
     * 获取系统默认屏幕亮度值 屏幕亮度值范围（0-255）
     **/
    public static int getScreenBrightness(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int defVal = 50;
        return Settings.System.getInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, defVal);
    }

    /**
     * 设置 APP界面屏幕亮度值方法
     **/
    public static void setAppScreenBrightness(Activity activity, int birghtessValue) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.screenBrightness = birghtessValue / 255f;
            window.setAttributes(lp);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
