package com.yuenov.open.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.fragments.baseInfo.BaseFragment;
import com.yuenov.open.model.httpModel.SaveBookReportHttpModel;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityToasty;
import com.yuenov.open.widget.EditTextView;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import butterknife.BindView;

/**
 * 帮助与反馈- 缺少书籍
 */
public class FeedBackLackBookFragment extends BaseFragment {

    // 书籍名称
    private static final String EXTRA_STRING_BOOKTITLE = "bookTitle";
    private String bookTitle;

    public static FeedBackLackBookFragment getFragment(String bookTitle) {
        FeedBackLackBookFragment fragment = new FeedBackLackBookFragment();
        if (!UtilitySecurity.isEmpty(bookTitle)) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_STRING_BOOKTITLE, bookTitle);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @BindView(R.id.etvFlbTitle)
    protected EditTextView etvFlbTitle;
    @BindView(R.id.etvFlbAuthor)
    protected EditTextView etvFlbAuthor;
    @BindView(R.id.tvFlbSubmit)
    protected TextView tvFlbSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_lackbook;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initExtra() {
        bookTitle = UtilitySecurity.getExtrasString(getArguments(), EXTRA_STRING_BOOKTITLE, "");
    }

    @Override
    protected void initListener() {
        etvFlbTitle.setListener(new EditTextView.IEditTextViewListener() {
            @Override
            public void onTextChange(String value) {
                resetButtonStyle();
            }
        });
        etvFlbAuthor.setListener(new EditTextView.IEditTextViewListener() {
            @Override
            public void onTextChange(String value) {
                resetButtonStyle();
            }
        });

        UtilitySecurityListener.setOnClickListener(tvFlbSubmit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void initData() {
        etvFlbTitle.setValue(bookTitle);
    }

    private void resetButtonStyle() {
        if (UtilitySecurity.isEmpty(etvFlbTitle.getValue()) && UtilitySecurity.isEmpty(etvFlbAuthor.getValue())) {
            UtilitySecurity.setTextColor(tvFlbSubmit, R.color.gray_3333);
            UtilitySecurity.setBackgroundResource(tvFlbSubmit, R.drawable.bg_productquestion_unsubmit);
            UtilitySecurity.setEnabled(tvFlbSubmit, false);
        } else {
            UtilitySecurity.setTextColor(tvFlbSubmit, R.color.white);
            UtilitySecurity.setBackgroundResource(tvFlbSubmit, R.drawable.bg_productquestion_submit);
            UtilitySecurity.setEnabled(tvFlbSubmit, true);
        }
    }

    private void submit() {
        SaveBookReportHttpModel httpModel = new SaveBookReportHttpModel();
        httpModel.title = etvFlbTitle.getValue();
        httpModel.author = etvFlbAuthor.getValue();
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
