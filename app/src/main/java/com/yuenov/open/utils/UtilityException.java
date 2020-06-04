package com.yuenov.open.utils;


/**
 * Created niupuyue
 * Date 2019/1/10
 * DES 全局异常捕获控制类，将错误信息记录
 */
public class UtilityException {

    /**
     * @param obj 当前页面的对象
     * @param e
     */
    public static void catchException(Object obj, Exception e) {

//        if (e == null)
//            return;
//
//        e.printStackTrace();
//        CrashReport.postCatchedException(e);
//
//        String classInfo;
//
//        try {
//            if (obj == null) {
//                classInfo = "type is null";
//            }
//            // activity
//            else if (obj instanceof String) {
//                classInfo = obj.toString();
//            }
//            // activity
//            else if (obj instanceof Activity) {
//                classInfo = ((Activity) obj).getLocalClassName();
//            }
//            // FragmentActivity
//            else if (obj instanceof FragmentActivity) {
//                classInfo = ((FragmentActivity) obj).getLocalClassName();
//            }
//            // android.app.Fragment
//            else if (obj instanceof android.app.Fragment) {
//                android.app.Fragment fragment = ((android.app.Fragment) obj);
//                if (fragment.getActivity() != null) {
//                    classInfo = fragment.getActivity().getLocalClassName();
//                } else {
//                    classInfo = "";
//                }
//            }
//            // android.support.v4.app.Fragment
//            else if (obj instanceof android.support.v4.app.Fragment) {
//                android.support.v4.app.Fragment fragment = ((android.support.v4.app.Fragment) obj);
//                if (fragment.getActivity() != null) {
//                    classInfo = fragment.getActivity().getLocalClassName();
//                } else {
//                    classInfo = "";
//                }
//            } else {
//                classInfo = "type is : "+ obj.getClass().getName();
//            }
//
//            classInfo += "\n\n";
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            classInfo="";
//        }

//        // debug，弹出展示展示Exception信息的页面
//        if (AppConstant.isDebug) {
//            try {
//                Intent intent = DebugExceptionActivity.getIntent(RRApplication.getAppContext(), classInfo, e);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                RRApplication.getAppContext().startActivity(intent);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                CrashReport.postCatchedException(ex);
//            }
//        }
    }

    public static void catchException(Exception e) {
        catchException("", e);
    }
}