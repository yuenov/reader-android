package com.yuenov.open.utils.dialogFragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.yuenov.open.constant.ConstantInterFace;
import com.renrui.libraries.interfaces.IOneButtonListener;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareAppSuccessDialogFragment extends BaseDialog implements View.OnClickListener {

    private static final String EXTRA_STRING_BOTTOMTEXT = "bottomText";
    private String bottomText;

    public static ShareAppSuccessDialogFragment getInstance(String bottomText) {
        ShareAppSuccessDialogFragment applySucceedDialog = new ShareAppSuccessDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_STRING_BOTTOMTEXT, bottomText);
        applySucceedDialog.setArguments(bundle);
        return applySucceedDialog;
    }

    private View viewContent;

    @BindView(R.id.tvPopSaUrl)
    protected TextView tvPopSaUrl;
    @BindView(R.id.tvPopSaSubmit)
    protected TextView tvPopSaSubmit;

    private IOneButtonListener listener;

    public void setListener(IOneButtonListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);

        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        // 居中显示
        setGravity(Gravity.CENTER);

        Bundle arguments = getArguments();
        if (null != arguments)
            bottomText = arguments.getString(EXTRA_STRING_BOTTOMTEXT);

        viewContent = View.inflate(getActivity(), R.layout.view_popwindow_shareapp_success, null);
        ButterKnife.bind(this, viewContent);

        UtilitySecurity.setText(tvPopSaUrl, ConstantInterFace.getUrlDomain());

        UtilitySecurity.setText(tvPopSaSubmit, bottomText);
        UtilitySecurityListener.setOnClickListener(this, tvPopSaSubmit);

        return viewContent;
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            case R.id.tvPopSaSubmit:

                if (this.listener != null)
                    this.listener.onButtonOnclick();

                dismiss();
                break;
        }
    }
}