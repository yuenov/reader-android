package com.yuenov.open.utils.dialogFragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.yuenov.open.utils.UtilityException;

/**
 * 弹窗基类
 */
public class BaseDialog extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();

        try {
            // 设置宽度为屏宽, 靠近屏幕底部。
            Window win = getDialog().getWindow();
            // 一定要设置Background，如果不设置，window属性设置无效
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            WindowManager.LayoutParams params = win.getAttributes();
            params.gravity = Gravity.CENTER;
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            win.setAttributes(params);

            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public void setGravity(int gravity) {
        try {
            Window win = getDialog().getWindow();

            WindowManager.LayoutParams params = win.getAttributes();
            params.gravity = gravity;
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            win.setAttributes(params);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            manager.executePendingTransactions();
            if (!isAdded()) {
                super.show(manager, tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnPositiveClickListener {
        void onClick();
    }
}