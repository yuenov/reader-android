package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.model.httpModel.UserUpdateHttpModel;
import com.yuenov.open.model.standard.ReadingPreferencesModel;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.UtilityData;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.renrui.libraries.interfaces.IHttpRequestInterFace;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;
import com.renrui.libraries.util.mHttpClient;

import butterknife.BindView;

// 阅读偏好
public class ReadingPreferencesActivity extends BaseActivity implements View.OnClickListener {

    public static Intent getIntent(Context context) {
        return new Intent(context, ReadingPreferencesActivity.class);
    }

    @BindView(R.id.llRpMale)
    protected LinearLayout llRpMale;

    @BindView(R.id.llRpFeMale)
    protected LinearLayout llRpFeMale;

    // 当前保存的用户信息
    private ReadingPreferencesModel readingPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_readingpreferences;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {
        UtilitySecurityListener.setOnClickListener(this, llRpMale, llRpFeMale);
    }

    @Override
    protected void initData() {
        initMyAppTitle(R.string.ReadingPreferencesActivity_appTitle);

        readingPreferences = EditSharedPreferences.getReadingPreferences();

        // 女生
        if (UtilitySecurity.equalsIgnoreCase(readingPreferences.gender, getString(R.string.gender_value_female))) {
            UtilitySecurity.setBackgroundResource(llRpFeMale, R.drawable.bg_readingpreferences_select);
        }
        // 男生(默认)
        else {
            UtilitySecurity.setBackgroundResource(llRpMale, R.drawable.bg_readingpreferences_select);
        }
    }

    private void save(boolean isMale) {
        ReadingPreferencesModel model = new ReadingPreferencesModel();
        model.gender = getString(isMale ? R.string.gender_value_male : R.string.gender_value_female);

        UserUpdateHttpModel httpModel = new UserUpdateHttpModel();
        httpModel.setIsPostJson(true);
        httpModel.setPostJsonText(mHttpClient.GetGsonInstance().toJson(model));
        mHttpClient.Request(this, httpModel, new IHttpRequestInterFace() {
            @Override
            public void onStart() {
//                getStatusTip().showProgress();
                getPubLoadingView().show();
            }

            @Override
            public void onResponse(String s) {
                if (!UtilityData.CheckResponseString(s)) {
                    return;
                }

                try {
                    readingPreferences.gender = model.gender;
                    EditSharedPreferences.setReadingPreferences(readingPreferences);

                    UtilityToasty.success(R.string.info_save_success);
                    finish();
                } catch (Exception ex) {
                    UtilityException.catchException(ex);
                }
            }

            @Override
            public void onErrorResponse(String s) {
                UtilityToasty.error(s);
            }

            @Override
            public void onFinish() {
//                getStatusTip().hideProgress();
                getPubLoadingView().hide();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.llRpMale:
                save(true);
                break;

            case R.id.llRpFeMale:
                save(false);
                break;
        }
    }
}
