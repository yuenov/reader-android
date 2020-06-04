package com.yuenov.open.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import butterknife.BindView;
import butterknife.ButterKnife;

//view_widget_edittextview
public class EditTextView extends LinearLayout {

    public interface IEditTextViewListener {
        void onTextChange(String value);
    }

    @BindView(R.id.etWgEtContent)
    protected EditText etWgEtContent;
    @BindView(R.id.ivWgEtClear)
    protected ImageView ivWgEtClear;

    private IEditTextViewListener listener;

    public void setListener(IEditTextViewListener listener) {
        this.listener = listener;
    }

    public EditTextView(Context context) {
        super(context);
    }

    public EditTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public EditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initLayout();

        initAttrs(attrs);

        initListener();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditTextView);
        if (a != null) {
            String hintText = a.getString(R.styleable.EditTextView_hintText);
            UtilitySecurity.setHint(etWgEtContent, hintText);
        }
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View viewContent = inflater.inflate(R.layout.view_widget_edittextview, null);

        ButterKnife.bind(this, viewContent);

        this.addView(viewContent, layoutParams);
    }

    private void initListener() {

        UtilitySecurityListener.addTextChangedListener(etWgEtContent, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                UtilitySecurity.resetVisibility(ivWgEtClear, !UtilitySecurity.isEmpty(etWgEtContent));

                if (listener != null)
                    listener.onTextChange(getValue());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        UtilitySecurityListener.setOnClickListener(ivWgEtClear, new OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilitySecurity.clearText(etWgEtContent);
            }
        });
    }

    public void setValue(String value) {
        UtilitySecurity.setText(etWgEtContent, value);
        UtilitySecurity.setLastSelection(etWgEtContent);
    }

    public String getValue() {
        return UtilitySecurity.getText(etWgEtContent);
    }
}
