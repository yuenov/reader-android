package com.yuenov.open.utils.pops;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuenov.open.R;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeleteBookShelfPop implements View.OnClickListener {


    public interface IDeleteBookShelfPop {
        void deleteToDetail(int position);

        void deleteToDelete(int position);

        void deleteToCancel();
    }

    private PopupWindow mPopupWindow;
    private DeleteBookShelfPop.IDeleteBookShelfPop listener;
    private int position;

    private Activity activity;

    private View viewContent;
    @BindView(R.id.viewPopDbsClose)
    protected View viewPopDbsClose;
    @BindView(R.id.tvPopDbsDetail)
    protected TextView tvPopDbsDetail;
    @BindView(R.id.tvPopDbsDelete)
    protected TextView tvPopDbsDelete;
    @BindView(R.id.tvPopDbsCancel)
    protected TextView tvPopDbsCancel;

    public void showPop(final Activity activity, final int position, final DeleteBookShelfPop.IDeleteBookShelfPop onListener) {
        if (activity == null || activity.isFinishing())
            return;

        this.activity = activity;
        this.position = position;
//        this.listener = onListener;

        initLayout();

        initListener();

        initPop();
    }

    private void initLayout() {
        viewContent = View.inflate(activity, R.layout.view_popwindow_deletebookshelf, null);

        ButterKnife.bind(this, viewContent);
    }

    private void initListener() {
        UtilitySecurityListener.setOnClickListener(this, viewPopDbsClose, tvPopDbsDetail, tvPopDbsDelete, tvPopDbsCancel);
    }

    private void initPop() {
        //设置布局为全屏 解决部分手机底部遮挡部分弹窗
        mPopupWindow = new PopupWindow(viewContent, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.FadeAnimationShort);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mPopupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            // 详情
            case R.id.tvPopDbsDetail:
                mPopupWindow.dismiss();
                if (listener != null)
                    listener.deleteToDetail(position);
                break;

            // 删除
            case R.id.tvPopDbsDelete:
                mPopupWindow.dismiss();
                if (listener != null)
                    listener.deleteToDelete(position);
                break;

            // 取消
            case R.id.viewPopDbsClose:
            case R.id.tvPopDbsCancel:
                mPopupWindow.dismiss();
                if (listener != null)
                    listener.deleteToCancel();
                break;
        }
    }
}
