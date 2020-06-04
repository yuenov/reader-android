package com.yuenov.open.activitys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.UtilityAppConfig;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityToasty;
import com.renrui.libraries.interfaces.ITextviewClickable;
import com.renrui.libraries.util.UtilityControl;
import com.renrui.libraries.util.UtilitySecurity;

import butterknife.BindView;

// 常见问题
public class CommonProblemActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, CommonProblemActivity.class);
    }

    @BindView(R.id.tvCpContent1)
    protected TextView tvCpContent1;
    @BindView(R.id.tvCpContent2)
    protected TextView tvCpContent2;
    @BindView(R.id.tvCpContent3)
    protected TextView tvCpContent3;
    @BindView(R.id.tvCpContent4)
    protected TextView tvCpContent4;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_commonproblem;
    }

    @Override
    protected void initExtra() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        initMyAppTitle(R.string.CommonProblemActivity_title);

        try {
            // Q1
            String q1HotWord = getString(R.string.CommonProblemActivity_content_titl_desc2);
            String q1Content = getString(R.string.CommonProblemActivity_content_titl_desc1)
                    + q1HotWord
                    + getString(R.string.CommonProblemActivity_content_titl_desc3);
            UtilityControl.setHotWordsText(tvCpContent1, q1Content, q1HotWord, R.color._7eca, new ITextviewClickable() {
                @Override
                public void onSpanClick(int i) {
                    startActivity(FeedBackActivity.getIntentByLarkBook(CommonProblemActivity.this, ""));
                }
            });

            // Q2
            String q2HotWord = getString(R.string.CommonProblemActivity_content_titl2_desc2);
            String q2Content = getString(R.string.CommonProblemActivity_content_titl2_desc1)
                    + q2HotWord
                    + getString(R.string.CommonProblemActivity_content_titl2_desc3);
            UtilityControl.setHotWordsText(tvCpContent2, q2Content, q2HotWord, R.color._7eca, new ITextviewClickable() {
                @Override
                public void onSpanClick(int i) {
                    replacePort();
                }
            });

            // Q3
            final String q3HotWord = getString(R.string.CommonProblemActivity_content_titl3_desc3);
            String q3Content = getString(R.string.CommonProblemActivity_content_titl3_desc2)
                    + "\n"
                    + q3HotWord;
            UtilityControl.setHotWordsText(tvCpContent3, q3Content, q3HotWord, R.color._7eca, new ITextviewClickable() {
                @Override
                public void onSpanClick(int i) {
                    Uri uri = Uri.parse(ConstantInterFace.getUrlDomain());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            // Q4
            final String q4HotWord = getString(R.string.CommonProblemActivity_content_titl4_desc3);
            String q4Content = getString(R.string.CommonProblemActivity_content_titl4_desc1)
                    + "\n"
                    + getString(R.string.CommonProblemActivity_content_titl4_desc2)
                    + q4HotWord;
            UtilityControl.setHotWordsText(tvCpContent4, q4Content, q4HotWord, R.color._7eca, new ITextviewClickable() {
                @Override
                public void onSpanClick(int i) {
                    Uri uri = Uri.parse(q4HotWord);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 切换端口
     */
    private void replacePort() {
        try {
            if (UtilitySecurity.isEmpty(UtilityAppConfig.getInstant().ports))
                return;

            int thisPort = ConstantInterFace.getDomainPort();
            int newPort = 0;

            for (int i = 0; i < UtilityAppConfig.getInstant().ports.size(); i++) {
                // 获取下一个端口
                if (UtilityAppConfig.getInstant().ports.get(i) == thisPort) {
                    if(i==UtilityAppConfig.getInstant().ports.size()-1)
                        newPort = UtilityAppConfig.getInstant().ports.get(0);
                    else
                        newPort = UtilityAppConfig.getInstant().ports.get(i+1);
                    break;
                }
            }

            if (newPort == 0)
                return;

            ConstantInterFace.setDomainPort(newPort);
            EditSharedPreferences.writeIntToConfig(EditSharedPreferences.INT_INTERFACEPORT, newPort);
            finish();
            UtilityToasty.success(R.string.info_switchNetWork_success);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}