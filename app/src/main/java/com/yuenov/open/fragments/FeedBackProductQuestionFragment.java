package com.yuenov.open.fragments;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.fragments.baseInfo.BaseFragment;
import com.yuenov.open.model.httpModel.SaveProductProblemHttpModel;
import com.yuenov.open.model.standard.SubmitSaveProductProblemInfo;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityToasty;
import com.yuenov.open.widget.EditTextView;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import butterknife.BindView;

/**
 * 帮助与反馈- 产品问题
 */
public class FeedBackProductQuestionFragment extends BaseFragment {

    public static FeedBackProductQuestionFragment getFragment() {
        return new FeedBackProductQuestionFragment();
    }

    @BindView(R.id.etFpqDesc)
    protected EditText etFpqDesc;
    @BindView(R.id.etvFpqContact)
    protected EditTextView etvFpqContact;
    @BindView(R.id.tvFpqSubmit)
    protected TextView tvFpqSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_productquestion;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.addTextChangedListener(etFpqDesc, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetButtonStyle();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etvFpqContact.setListener(new EditTextView.IEditTextViewListener() {
            @Override
            public void onTextChange(String value) {
                resetButtonStyle();
            }
        });

        UtilitySecurityListener.setOnClickListener(tvFpqSubmit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void resetButtonStyle() {
        if (UtilitySecurity.isEmpty(etFpqDesc)) {
            UtilitySecurity.setTextColor(tvFpqSubmit, R.color.gray_3333);
            UtilitySecurity.setBackgroundResource(tvFpqSubmit, R.drawable.bg_productquestion_unsubmit);
            UtilitySecurity.setEnabled(tvFpqSubmit, false);
        } else {
            UtilitySecurity.setTextColor(tvFpqSubmit, R.color.white);
            UtilitySecurity.setBackgroundResource(tvFpqSubmit, R.drawable.bg_productquestion_submit);
            UtilitySecurity.setEnabled(tvFpqSubmit, true);
        }
    }

    private void submit() {
        if (UtilitySecurity.isEmpty(etFpqDesc)) {
            UtilityToasty.warning("请输入内容！");
            return;
        }

        SubmitSaveProductProblemInfo info = new SubmitSaveProductProblemInfo();
        info.desc = UtilitySecurity.getText(etFpqDesc);
        info.contact = etvFpqContact.getValue();

        SaveProductProblemHttpModel httpModel = new SaveProductProblemHttpModel();
        httpModel.setIsPostJson(true);
        httpModel.setPostJsonText(mHttpClient.GetGsonInstance().toJson(info));
        mHttpClient.Request(getActivity(), httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    return;
                }

                UtilityToasty.success("问题提交成功！");
                getActivity().finish();
            }

            @Override
            public void onErrorResponse(String s) {
                UtilityToasty.error(s);
            }

            @Override
            public void onFinish() {

            }
        });
    }
}
