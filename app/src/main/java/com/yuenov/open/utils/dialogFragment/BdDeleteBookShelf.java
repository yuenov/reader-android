package com.yuenov.open.utils.dialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BdDeleteBookShelf extends BaseDialog implements View.OnClickListener{

    public interface IDeleteBookShelfPop {
        void toPreviewDetail(int position);

        void toDelete(int position);

        void toCancel();
    }

    private static final String EXTRA_INT_POSITION = "position";
    private int position;

    private View viewContent;
    @BindView(R.id.viewPopDbsClose)
    protected View viewPopDbsClose;
    @BindView(R.id.tvPopDbsDetail)
    protected TextView tvPopDbsDetail;
    @BindView(R.id.tvPopDbsDelete)
    protected TextView tvPopDbsDelete;
    @BindView(R.id.tvPopDbsCancel)
    protected TextView tvPopDbsCancel;

    private IDeleteBookShelfPop listener;

    public void setListener(IDeleteBookShelfPop listener)
    {
        this.listener = listener;
    }

    public static BdDeleteBookShelf getInstance(int position) {
        BdDeleteBookShelf applySucceedDialog = new BdDeleteBookShelf();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_INT_POSITION, position);
        applySucceedDialog.setArguments(bundle);
        return applySucceedDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        if(getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        Bundle arguments = getArguments();
        if (null != arguments) {
            position = arguments.getInt(EXTRA_INT_POSITION);
        }

        viewContent = View.inflate(getActivity(), R.layout.view_popwindow_deletebookshelf, null);
        ButterKnife.bind(this, viewContent);

        UtilitySecurityListener.setOnClickListener(this, viewPopDbsClose, tvPopDbsDetail, tvPopDbsDelete, tvPopDbsCancel);

        return viewContent;
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            // 详情
            case R.id.tvPopDbsDetail:
                dismiss();
                if (listener != null)
                    listener.toPreviewDetail(position);
                break;

            // 删除
            case R.id.tvPopDbsDelete:
                dismiss();
                if (listener != null)
                    listener.toDelete(position);
                break;

            // 取消
            case R.id.viewPopDbsClose:
            case R.id.tvPopDbsCancel:
                dismiss();
                if (listener != null)
                    listener.toCancel();
                break;
        }
    }
}
