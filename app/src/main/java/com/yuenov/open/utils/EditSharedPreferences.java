package com.yuenov.open.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yuenov.open.application.MyApplication;
import com.yuenov.open.model.standard.AppConfigInfo;
import com.yuenov.open.model.standard.BookBaseInfo;
import com.yuenov.open.model.standard.ReadSettingInfo;
import com.yuenov.open.model.standard.ReadingPreferencesModel;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.mHttpClient;

import java.util.ArrayList;
import java.util.List;

public class EditSharedPreferences {

    // 设置信息
    public static final String STRING_REDSETTINGINFO = "STRING_REDSETTINGINFO";
    public static final String STRING_CATEGORYINFO = "STRING_CATEGORYINFO";
    public static final String STRING_SEARCHHISTORY = "searchHistory";
    // 阅读偏好
    public static final String STRING_READINGPREFERENCES = "ReadingPreferences";
    //  代理ip
    public static final String STRING_STRING_PROXYIP = "proxyIp";
    public static final String STRING_STRING_UUID = "uuid";
    public static final String STRING_STRING_UID = "uid";
    // 接口端口
    public static final String INT_INTERFACEPORT = "interFacePort";
    // 正在阅读的图书
    public static final String STRING_NOWREADBOOKID = "nowRead";

    private static SharedPreferences mySharedPreferences = null;

    public static SharedPreferences getSharedPreferencesInstance() {
        if (mySharedPreferences == null && null != MyApplication.getAppContext()) {
            mySharedPreferences = MyApplication.getAppContext().getSharedPreferences("app_info", Activity.MODE_PRIVATE);
        }

        return mySharedPreferences;
    }

    public static void writeBooleanToConfig(String key, boolean value) {
        getSharedPreferencesInstance().edit().putBoolean(key, value).apply();
    }

    public static boolean readBooleanFromConfig(String key, boolean defaultBoolean) {
        boolean defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getBoolean(key, defaultBoolean);
        } catch (Exception e) {
            UtilityException.catchException(e);
            defaultValue = false;
        }
        return defaultValue;
    }

    public static void writeStringToConfig(String key, String value) {
        getSharedPreferencesInstance().edit().putString(key, value).apply();
    }

    public static String readStringFromConfig(String key) {
        String defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getString(key, "");
        } catch (Exception e) {
            UtilityException.catchException(e);
            defaultValue = "";
        }
        return defaultValue;
    }

    public static void writeLongToConfig(String key, long value) {
        getSharedPreferencesInstance().edit().putLong(key, value).apply();
    }

    public static long readLongFromConfig(String key) {
        long defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getLong(key, 0);
        } catch (Exception e) {
            UtilityException.catchException(e);
            defaultValue = 0;
        }
        return defaultValue;
    }

    public static void writeIntToConfig(String key, int value) {
        getSharedPreferencesInstance().edit().putInt(key, value).apply();
    }

    public static int readIntFromConfig(String key) {
        return readIntFromConfig(key, 0);
    }

    public static int readIntFromConfig(String key, int defaultInt) {
        int defaultValue;
        try {
            defaultValue = getSharedPreferencesInstance().getInt(key, defaultInt);
        } catch (Exception e) {
            UtilityException.catchException(e);
            defaultValue = 0;
        }
        return defaultValue;
    }

    /**
     * 设置：设置信息
     */
    public static void setReadSettingInfo(ReadSettingInfo readInfo) {
        try {
            SharedPreferences.Editor editor = getSharedPreferencesInstance().edit();
            editor.putString(STRING_REDSETTINGINFO, mHttpClient.GetGsonInstance().toJson(readInfo));
            editor.apply();
        } catch (Exception e) {
            UtilityException.catchException(e);
        }
    }

    /**
     * 获取：设置信息
     */
    public static ReadSettingInfo getReadSettingInfo() {
        ReadSettingInfo settingInfo = null;
        String strObj = getSharedPreferencesInstance().getString(STRING_REDSETTINGINFO, "");

        try {
            if (!TextUtils.isEmpty(strObj))
                settingInfo = (ReadSettingInfo) LibUtility.deSerializationToObject(strObj);
        } catch (Exception e) {
            UtilityException.catchException(e);
        }

        try {
            if (settingInfo == null)
                settingInfo = mHttpClient.GetGsonInstance().fromJson(strObj, ReadSettingInfo.class);
        } catch (Exception e) {
            UtilityException.catchException(e);
        }

        if (settingInfo == null) {
            settingInfo = new ReadSettingInfo();
        }

        return settingInfo;
    }

    public static void saveConfigInfo(Object obj) {
        try {
            SharedPreferences.Editor editor = getSharedPreferencesInstance().edit();
            editor.putString(STRING_CATEGORYINFO, mHttpClient.GetGsonInstance().toJson(obj));
            editor.apply();
        } catch (Exception e) {
            UtilityException.catchException(e);
        }
    }

    public static AppConfigInfo getConfigInfo() {
        AppConfigInfo settingInfo = null;
        String strObj = getSharedPreferencesInstance().getString(STRING_CATEGORYINFO, "");

        try {
            if (!TextUtils.isEmpty(strObj))
                settingInfo = (AppConfigInfo) LibUtility.deSerializationToObject(strObj);
        } catch (Exception e) {
            UtilityException.catchException(e);
        }

        try {
            if (settingInfo == null)
                settingInfo = mHttpClient.GetGsonInstance().fromJson(strObj, AppConfigInfo.class);
        } catch (Exception e) {
            UtilityException.catchException(e);
        }

        if (settingInfo == null) {
            settingInfo = new AppConfigInfo();
        }

        return settingInfo;
    }

    public static void addSearchHistory(String hotword) {
        try {
            List<String> lisHostory = getSearchHistory();
            if (lisHostory.size() > 10) {
                lisHostory.remove(lisHostory.size() - 1);
            }

            lisHostory.add(0, hotword);
            setSearchHistory(lisHostory);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static List<String> getSearchHistory() {
        List<String> list = new ArrayList<>();
        try {
            String strObj = getSharedPreferencesInstance().getString(STRING_SEARCHHISTORY, "");
            if (!UtilitySecurity.isEmpty(strObj))
                list = mHttpClient.GetGsonInstance().fromJson(strObj, list.getClass());
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            list = new ArrayList<>();
        }

        return list;
    }

    public static void setSearchHistory(List<String> list) {
        try {
            SharedPreferences.Editor editor = getSharedPreferencesInstance().edit();
            editor.putString(STRING_SEARCHHISTORY, mHttpClient.GetGsonInstance().toJson(list));
            editor.apply();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static ReadingPreferencesModel getReadingPreferences() {
        ReadingPreferencesModel model = null;
        try {
            model = mHttpClient.GetGsonInstance().fromJson(readStringFromConfig(STRING_READINGPREFERENCES), ReadingPreferencesModel.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        if (model == null)
            model = new ReadingPreferencesModel();

        return model;
    }

    public static void setReadingPreferences(ReadingPreferencesModel model) {
        try {
            if (model != null)
                writeStringToConfig(STRING_READINGPREFERENCES, mHttpClient.GetGsonInstance().toJson(model));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static void setNowReadBook(BookBaseInfo bookBaseInfo) {
        try {
            EditSharedPreferences.writeStringToConfig(STRING_NOWREADBOOKID, mHttpClient.GetGsonInstance().toJson(bookBaseInfo));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static void clearNowReadBook() {
        try {
            EditSharedPreferences.writeStringToConfig(STRING_NOWREADBOOKID, "");
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static BookBaseInfo getNowReadBook() {
        BookBaseInfo bookBaseInfo = null;

        try {
            String str = EditSharedPreferences.readStringFromConfig(STRING_NOWREADBOOKID);
            bookBaseInfo = mHttpClient.GetGsonInstance().fromJson(str, BookBaseInfo.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        if (bookBaseInfo == null)
            bookBaseInfo = new BookBaseInfo();

        return bookBaseInfo;
    }
}