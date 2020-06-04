package com.yuenov.open.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.constant.AboutChapterStatus;
import com.yuenov.open.constant.ConstantInterFace;
import com.google.gson.JsonParser;
import com.renrui.libraries.model.baseObject.BaseResponseModel;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilityTime;
import com.renrui.libraries.util.mHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据相关的公共方法
 */
public class UtilityData {

    /**
     * 检查返回json字符串是否合法
     *
     * @param content content
     */
    public static boolean CheckResponseString(String content) {
        return CheckResponseString(content, true);
    }

    /**
     * 检查返回json字符串是否合法
     *
     * @param content            content
     * @param isShowErrorMessage 是否提示错误信息
     * @return
     */
    public static boolean CheckResponseString(String content, boolean isShowErrorMessage) {

        BaseResponseModel baseResponseModel;
        try {
            if (TextUtils.isEmpty(content)) {
                if (isShowErrorMessage) {
                    UtilityToasty.error(R.string.info_json_error);
                }
                return false;
            }

            baseResponseModel = mHttpClient.GetGsonInstance().fromJson(content, BaseResponseModel.class);
            if (baseResponseModel == null || baseResponseModel.result == null) {
                if (isShowErrorMessage) {
                    UtilityToasty.error(R.string.info_json_error);
                }
                return false;
            }
            // 101 即返回uid
            else if (baseResponseModel.result.code == 101) {
                String uid = baseResponseModel.result.msg;
                if (!UtilitySecurity.isEmpty(uid)) {
                    EditSharedPreferences.writeStringToConfig(EditSharedPreferences.STRING_STRING_UID, uid);
//                    MyApplication.initCookie();
                }

                return true;
            } else if (baseResponseModel.result.code != 0) {
                if (isShowErrorMessage) {
                    UtilityToasty.error(baseResponseModel.result.msg);
                }
                return false;
            }
        } catch (Exception ex) {
            if (isShowErrorMessage) {
                UtilityToasty.error(R.string.info_json_error);
            }
            return false;
        }

        return true;
    }

    public static BaseResponseModel getBaseResponseModel(String content) {
        BaseResponseModel baseResponseModel = null;

        try {
            baseResponseModel = mHttpClient.GetGsonInstance().fromJson(content, BaseResponseModel.class);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        if (baseResponseModel == null) {
            baseResponseModel = new BaseResponseModel();
        }

        return baseResponseModel;
    }

    /**
     * 获取逗号拼接的字符串
     *
     * @param lis      字符串对象
     * @param strSplit 分隔符
     */
    public static String getStrByList(List<String> lis, String strSplit) {
        String str = "";

        try {
            if (lis != null && lis.size() > 0) {
                for (int i = 0; i < lis.size(); i++) {
                    str += ((i == 0) ? "" : strSplit) + lis.get(i);
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return str;
    }

    /**
     * 获取逗号拼接的字符串
     *
     * @param lis      字符串对象
     * @param strSplit 分隔符
     */
    public static String getStrByListInteger(List<Integer> lis, String strSplit) {
        String str = "";

        try {
            if (lis != null && lis.size() > 0) {
                for (int i = 0; i < lis.size(); i++) {
                    str += ((i == 0) ? "" : strSplit) + lis.get(i) + "";
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return str;
    }

    /**
     * 获取逗号拼接的字符串
     */
    public static String getStrByList(List<String> lis) {
        return getStrByList(lis, ",");
    }

    public static ArrayList<String> getArraryBySourceID(int sourceID) {
        ArrayList<String> lis = new ArrayList<>();

        try {
            final String[] arr = MyApplication.getAppContext().getResources().getStringArray(sourceID);
            for (String str : arr) {
                lis.add(str);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return lis;
    }

    /**
     * @param phone 手机号
     * @return 将手机号的4-7位替换成星号
     */
    public static String getForMatPhone(String phone) {
        String strPhone = "";

        if (TextUtils.isEmpty(phone) || phone.trim().length() != 11) {
            return strPhone;
        }

        try {
            strPhone = phone.substring(0, 3) + " **** " + phone.substring(7, 11);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return strPhone;
    }

    /**
     * 获取一级域名
     */
    private static String getLevelHost(String host) {
        String value = "";

        try {
            String regStr = "[0-9a-zA-Z]+((\\.com)|(\\.cn)|(\\.org)|(\\.net)|(\\.edu)|(\\.com.cn)|(\\.xyz)|(\\.xin)|(\\.club)|(\\.shop)|(\\.site)|(\\.wang)" +
                    "|(\\.top)|(\\.win)|(\\.online)|(\\.tech)|(\\.store)|(\\.bid)|(\\.cc)|(\\.ren)|(\\.lol)|(\\.pro)|(\\.red)|(\\.kim)|(\\.space)|(\\.link)|(\\.click)|(\\.news)|(\\.news)|(\\.ltd)|(\\.website)" +
                    "|(\\.biz)|(\\.help)|(\\.mom)|(\\.work)|(\\.date)|(\\.loan)|(\\.mobi)|(\\.live)|(\\.studio)|(\\.info)|(\\.pics)|(\\.photo)|(\\.trade)|(\\.vc)|(\\.party)|(\\.game)|(\\.rocks)|(\\.band)" +
                    "|(\\.gift)|(\\.wiki)|(\\.design)|(\\.software)|(\\.social)|(\\.lawyer)|(\\.engineer)|(\\.org)|(\\.net.cn)|(\\.org.cn)|(\\.gov.cn)|(\\.name)|(\\.tv)|(\\.me)|(\\.asia)|(\\.co)|(\\.press)|(\\.video)|(\\.market)" +
                    "|(\\.games)|(\\.science)|(\\.中国)|(\\.公司)|(\\.网络)|(\\.pub)" +
                    "|(\\.la)|(\\.auction)|(\\.email)|(\\.sex)|(\\.sexy)|(\\.one)|(\\.host)|(\\.rent)|(\\.fans)|(\\.cn.com)|(\\.life)|(\\.cool)|(\\.run)" +
                    "|(\\.gold)|(\\.rip)|(\\.ceo)|(\\.sale)|(\\.hk)|(\\.io)|(\\.gg)|(\\.tm)|(\\.com.hk)|(\\.gs)|(\\.us))";
            Pattern p = Pattern.compile(regStr);
            Matcher m = p.matcher(host);
            //获取一级域名
            while (m.find()) {
                value = m.group();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return value;
    }

    /**
     * 两个对象是否内容完全相同
     */
    public static boolean isSame(Object A, Object B) {
        boolean value = false;

        if (A == null || B == null) {
            return false;
        }

        try {
            value = LibUtility.serializeToString(A).equals(LibUtility.serializeToString(B));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return value;
    }

    private static Object dealWithEmpty(Object obj, boolean isRecursive) {
        try {
            // 获取所有字段
            java.lang.reflect.Field[] fields = obj.getClass().getFields();

            int fieldCounts = fields.length;
            List<Object> listObj;
            Object otherField;
            for (int i = 0; i < fieldCounts; i++) {

                // 如果字段是String
                if (fields[i].getType().getName().equalsIgnoreCase("java.lang.String")) {
                    // 并且内容为null 或 空字符串
                    // 将内容置为null
                    if (fields[i].get(obj) == null || TextUtils.isEmpty(fields[i].get(obj).toString())) {
                        fields[i].set(obj, null);
                    }
                }
                // 如果字段是集合类型
                else if (fields[i].getType().getName().equalsIgnoreCase("java.util.List")
                        || fields[i].getType().getName().equalsIgnoreCase("java.util.ArrayList")) {
                    try {
                        // 如果集合为空，也置为null
                        listObj = (List<Object>) fields[i].get(obj);
                        if (listObj == null || listObj.isEmpty()) {
                            fields[i].set(obj, null);
                        }
                    } catch (Exception ex) {
                        UtilityException.catchException(ex);
                    }
                } else if (isRecursive) {
                    otherField = dealWithEmpty(fields[i], false);
                    if (otherField != null && !((java.lang.reflect.Field) otherField).getName().equals("serialVersionUID")) {
                        fields[i].set(obj, ((java.lang.reflect.Field) otherField).get(obj));
                    }
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return obj;
    }

    /**
     * 获取关键字在字符串的indexOf集合 (忽略大小写)
     *
     * @param str     字符串
     * @param hotword 关键字
     * @return
     */
    public static List<Integer> getStrIndex(String str, String hotword) {

        List<Integer> lis = new ArrayList<>();

        // 忽略大小写
        final String tempLowerCaseStr = str.toLowerCase();
        final String tempLowerHotWord = hotword.toLowerCase();

        int indexOf = tempLowerCaseStr.indexOf(tempLowerHotWord);
        if (indexOf != -1) {
            lis.add(indexOf);
        }

        while (indexOf != -1) {

            indexOf = tempLowerCaseStr.indexOf(tempLowerHotWord, indexOf + 1);

            if (indexOf != -1) {
                lis.add(indexOf);
            }
        }

        return lis;
    }

    /**
     * 数字转汉字
     */
    public static String toChinese(int value) {

        final String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final String[] s2 = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};

        String result = "";

        try {
            int n = String.valueOf(value).length();
            for (int i = 0; i < n; i++) {

                int num = String.valueOf(value).charAt(i) - '0';

                if (i != n - 1 && num != 0) {
                    result += s1[num] + s2[n - 2 - i];
                } else {
                    result += s1[num];
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return result;
    }

    /**
     * 获取可展示的安全姓名（第一个字符展示真实的，其他的用星号代替）
     */
    public static String getSecurityRealName(String realName) {
        String value = "";

        try {
            char[] arr = realName.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                value += (i == 0 ? arr[i] : "*");
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            value = "";
        }

        return value;
    }

    /**
     * 获取可展示的安全身份证号码（前6位，后2位展示真实的，其他的用星号代替）
     */
    public static String getSecurityRealNumber(String realNumber) {
        String value = "";

        try {
            String strStart = realNumber.substring(0, 6);
            String strEnd = realNumber.substring(realNumber.length() - 2);

            int charSecurityCounts = realNumber.length() - strStart.length() - strEnd.length();

            for (int i = 0; i < charSecurityCounts; i++) {
                value += "*";
            }

            value = strStart + value + strEnd;
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            value = "";
        }

        return value;
    }

    public static boolean isJson(String content) {
        try {
            JsonParser parser = new JsonParser();
            parser.parse(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除开头和结尾的换行符
     *
     * @param s
     * @return
     */
    public static String deleteStartAndEndNewLine(String s) {
        if (UtilitySecurity.isEmpty(s))
            return "";

        try {
            if (s.indexOf("\n") == 0) {
                s = s.substring(1);
            }

            if (s.lastIndexOf("\n") == (s.length() - 1)) {
                s = s.substring(0, s.length() - 1);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return s;
    }

    /**
     * 读取assets目录下文件
     */
    public static String readFromAssets(Context context, String fileName) {

        try {
            //得到资源中的asset数据流
            InputStream in = context.getResources().getAssets().open(fileName);

            final int length = in.available();
            byte[] buffer = new byte[length];

            in.read(buffer);
            in.close();

            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            UtilityException.catchException(e);
        }

        return "";
    }

    public static void convertEntity(Cursor cursor, Class tClass) {
        try {
            String columnName;
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                columnName = cursor.getColumnName(i);
            }

        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static String getStitching(List<String> list, String sti) {
        StringBuffer sb = new StringBuffer();

        try {
            if (list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    sb.append(i == 0 ? (String) list.get(i) : sti + (String) list.get(i));
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return sb.toString();
    }

    public static String getStitchingInteger(List<Integer> list, String sti) {
        StringBuffer sb = new StringBuffer();

        try {
            if (list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    if (i == 0)
                        sb.append(list.get(i));
                    else
                        sb.append(sti + list.get(i));
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return sb.toString();
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    // 默认男
    public static String getGenderValueByText(String text) {
        if (UtilitySecurity.equalsIgnoreCase(text, MyApplication.getAppContext().getString(R.string.gender_text_male))) {
            return MyApplication.getAppContext().getString(R.string.gender_value_male);
        } else if (UtilitySecurity.equalsIgnoreCase(text, MyApplication.getAppContext().getString(R.string.gender_text_female))) {
            return MyApplication.getAppContext().getString(R.string.gender_value_female);
        } else {
            return MyApplication.getAppContext().getString(R.string.gender_value_male);
        }
    }

    // 默认男
    public static String getGenderTextByValue(String value) {
        if (UtilitySecurity.equalsIgnoreCase(value, MyApplication.getAppContext().getString(R.string.gender_value_male))) {
            return MyApplication.getAppContext().getString(R.string.gender_text_male);
        } else if (UtilitySecurity.equalsIgnoreCase(value, MyApplication.getAppContext().getString(R.string.gender_value_female))) {
            return MyApplication.getAppContext().getString(R.string.gender_text_female);
        } else {
            return MyApplication.getAppContext().getString(R.string.gender_text_male);
        }
    }

    // 是否连载中
    public static boolean isSerialize(String value) {
        return (UtilitySecurity.equalsIgnoreCase(value, AboutChapterStatus.SERIALIZE));
    }

    public static boolean isServerInterFace(String url) {
        boolean value;

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        try {
            final Uri loadUri = Uri.parse(url.trim());

            if (UtilitySecurity.isEmpty(loadUri.getHost())) {
                value = false;
            } else {
                final String loadProtocol = getLevelHost(loadUri.getHost().toLowerCase().trim());
                final String onLineProtocol = getLevelHost(ConstantInterFace.getInterfaceDomain().toLowerCase().trim());

                // 兼容香草旧域名
                value = loadProtocol.equalsIgnoreCase(onLineProtocol);
            }

        } catch (Exception ex) {
            UtilityException.catchException(ex);
            value = false;
        }

        return value;
    }

    public static String getPercent(int diliverNum, int queryMailNum) {
        String result = "";
        try {
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(1);
            result = numberFormat.format((float) diliverNum / (float) queryMailNum * 100);

            if (UtilitySecurity.equals(result, "0")
                    || UtilitySecurity.equals(result, "0.0"))
                result = "0.1";
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return result;
    }

    private static Calendar calendar;
    private static ContentResolver mResolver;
    private static String timeFormat;
    private static int hour;
    private static String allText;

    public static String getShowTimeText() {
        allText = UtilityTime.sdf_4.format(System.currentTimeMillis());

        try {
            calendar = Calendar.getInstance();
            mResolver = MyApplication.getAppContext().getContentResolver();
            timeFormat = android.provider.Settings.System.getString(mResolver, android.provider.Settings.System.TIME_12_24);
            if (UtilitySecurity.equals(timeFormat, "12")) {
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour >= 0 && hour <= 6) {
                    allText = "凌晨 " + allText;
                } else if (hour >= 7 && hour <= 11) {
                    allText = "上午 " + allText;
                } else if (hour >= 12 && hour <= 20) {
                    allText = "下午 " + allText;
                } else {
                    allText = "晚上 " + allText;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
        return allText;
    }

    /**
     * 获取时间差方法
     */
    public static String getDiffTimeText(long firstTime) {
        String diffTime = "";

        try {
            Calendar currentTimes = Calendar.getInstance();//当前系统时间转Calendar类型
            Calendar firstTimes = Calendar.getInstance();//查询的数据时间转Calendar类型
            firstTimes.setTimeInMillis(firstTime);

            long diff = currentTimes.getTimeInMillis() - firstTimes.getTimeInMillis();
            if (diff < 1)
                return diffTime;

            int year = currentTimes.get(Calendar.YEAR) - firstTimes.get(Calendar.YEAR);//获取年
            int month = currentTimes.get(Calendar.MONTH) - firstTimes.get(Calendar.MONTH);
            int day = currentTimes.get(Calendar.DAY_OF_MONTH) - firstTimes.get(Calendar.DAY_OF_MONTH);
            if (day < 0) {
                month -= 1;
                currentTimes.add(Calendar.MONTH, -1);
            }
            if (month < 0) {
                month = (month + 12) % 12;//获取月
                year--;
            }
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60); //获取时 
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);//获取分钟
            long s = (diff / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60);//获取秒

            if (year > 0) {
                diffTime = year + "年前";
            } else if (month > 0) {
                diffTime = month + "月前";
            } else if (days > 0) {
                diffTime = days + "天前";
            } else if (hours > 0) {
                diffTime = hours + "小时前";
            } else if (minutes > 0) {
                diffTime = minutes + "分钟前";
            } else if (s > 0) {
                diffTime = s + "秒前";
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            diffTime = "";
        }

        return diffTime;
    }

    public static String getManifestMateLabel(String name) {
        String value = "";
        try {
            ApplicationInfo appInfo = MyApplication.getAppContext().getPackageManager().getApplicationInfo(MyApplication.getAppContext().getPackageName(),
                    PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            value = "";
        }

        return value;
    }

    public static boolean isHongMiNote7() {
        boolean value = false;

        try {
            value = UtilitySecurity.equalsIgnoreCase(Build.MODEL, "Redmi Note 7");
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return value;
    }
}