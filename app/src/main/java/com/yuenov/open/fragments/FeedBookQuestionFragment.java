package com.yuenov.open.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.fragments.baseInfo.BaseFragment;
import com.yuenov.open.model.httpModel.BookProblemHttpModel;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import butterknife.BindView;

/**
 * 帮助与反馈- 缺少书籍
 */
public class FeedBookQuestionFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    /**
     * 图书id
     */
    private static final String EXTRA_INT_BOOKID = "bookId";
    private int bookId;

    /**
     * 章节id
     */
    private static final String EXTRA_LONG_CHAPTERID = "chapterId";
    private long chapterId;

    public static FeedBookQuestionFragment getFragment(int bookId, long chapterId) {
        FeedBookQuestionFragment fragment = new FeedBookQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_INT_BOOKID, bookId);
        bundle.putLong(EXTRA_LONG_CHAPTERID, chapterId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @BindView(R.id.rgBrMenu)
    protected RadioGroup rgBrMenu;

    @BindView(R.id.tvBrSubmit)
    protected TextView tvBrSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bookquestion;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initExtra() {
        bookId = UtilitySecurity.getExtrasInt(getArguments(), EXTRA_INT_BOOKID);
        chapterId = UtilitySecurity.getExtrasLong(getArguments(), EXTRA_LONG_CHAPTERID);
    }

    @Override
    protected void initListener() {
        rgBrMenu.setOnCheckedChangeListener(this);
        UtilitySecurityListener.setOnClickListener(tvBrSubmit, this);
    }

    @Override
    protected void initData() {
    }

    private void resetButtonStyle() {
        try {
            if (rgBrMenu.getCheckedRadioButtonId() == -1) {
                UtilitySecurity.setTextColor(tvBrSubmit, R.color.gray_3333);
                UtilitySecurity.setBackgroundResource(tvBrSubmit, R.drawable.bg_productquestion_unsubmit);
                UtilitySecurity.setEnabled(tvBrSubmit, false);
            } else {
                UtilitySecurity.setTextColor(tvBrSubmit, R.color.white);
                UtilitySecurity.setBackgroundResource(tvBrSubmit, R.drawable.bg_productquestion_submit);
                UtilitySecurity.setEnabled(tvBrSubmit, true);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void submit() {
        BookProblemHttpModel httpModel = new BookProblemHttpModel();
        httpModel.bookId = bookId;
        httpModel.chapterId = chapterId;
        switch (rgBrMenu.getCheckedRadioButtonId()) {
            case R.id.rbBrMenu1:
                httpModel.correctType = "CHAPTER_ORDER_ERROR";
                break;
            case R.id.rbBrMenu2:
                httpModel.correctType = "LACK_CONTENT";
                break;
            case R.id.rbBrMenu3:
                httpModel.correctType = "NO_UPDATE";
                break;
            case R.id.rbBrMenu4:
                httpModel.correctType = "CODE_WORD_ERROR";
                break;
            default:
                return;
        }
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        resetButtonStyle();
    }

    @Override
    public void onClick(View v) {
        if (LibUtility.isFastDoubleClick())
            return;

        switch (v.getId()) {
            case R.id.tvBrSubmit:
                submit();
                break;
        }
    }
}
