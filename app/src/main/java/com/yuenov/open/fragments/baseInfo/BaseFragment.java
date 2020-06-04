package com.yuenov.open.fragments.baseInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    public View viewContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (viewContent == null) {
            if (getLayoutId() > 0)
                viewContent = View.inflate(getContext(), getLayoutId(), null);

            ButterKnife.bind(this,viewContent);

            initExtra();

            initLayout();

            initListener();

            initData();
        }

        return viewContent;
    }

    protected abstract int getLayoutId();

    protected abstract void initLayout();

    protected abstract void initExtra();

    protected abstract void initListener();

    protected abstract void initData();
}
