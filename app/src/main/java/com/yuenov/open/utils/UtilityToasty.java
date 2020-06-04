package com.yuenov.open.utils;

import android.widget.Toast;

import com.yuenov.open.application.MyApplication;
import com.renrui.libraries.util.UtilitySecurity;

import es.dmoral.toasty.Toasty;

public class UtilityToasty {

    public static void success(String content, boolean showIcon) {
        if (UtilitySecurity.isEmpty(content))
            return;

        Toasty.success(MyApplication.getAppContext(), content, Toast.LENGTH_SHORT, showIcon).show();
    }

    public static void success(String content) {
        success(content, true);
    }

    public static void success(int strSourceId) {
        success(MyApplication.getAppContext().getString(strSourceId), true);
    }

    public static void warning(String content, boolean showIcon) {
        if (UtilitySecurity.isEmpty(content))
            return;

        Toasty.warning(MyApplication.getAppContext(), content, Toast.LENGTH_SHORT, showIcon).show();
    }

    public static void warning(String content) {
        warning(content, true);
    }

    public static void warning(int strSourceId) {
        warning(MyApplication.getAppContext().getString(strSourceId), true);
    }

    public static void error(String content, boolean showIcon) {
        if (UtilitySecurity.isEmpty(content))
            return;

        Toasty.error(MyApplication.getAppContext(), content, Toast.LENGTH_SHORT, showIcon).show();
    }

    public static void error(String content) {
        error(content, true);
    }

    public static void error(int strSourceId) {
        error(MyApplication.getAppContext().getString(strSourceId), true);
    }
}
