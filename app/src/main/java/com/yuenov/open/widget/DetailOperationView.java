package com.yuenov.open.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuenov.open.R;
import com.yuenov.open.activitys.baseInfo.BaseActivity;
import com.yuenov.open.adapters.BookDetailBottomMenuListAdapter;
import com.yuenov.open.constant.ConstantSetting;
import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.database.tb.TbReadHistory;
import com.yuenov.open.model.standard.ReadSettingInfo;
import com.yuenov.open.utils.EditSharedPreferences;
import com.yuenov.open.utils.Utility;
import com.yuenov.open.utils.UtilityBrightness;
import com.yuenov.open.utils.UtilityException;
import com.yuenov.open.utils.UtilityMeasure;
import com.yuenov.open.utils.UtilityReadInfo;
import com.yuenov.open.widget.page.PageMode;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.Logger;
import com.renrui.libraries.util.UtilitySecurity;
import com.renrui.libraries.util.UtilitySecurityListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailOperationView extends LinearLayout implements View.OnClickListener, LightView.ILightViewListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {

    public interface IDetailOperationView {
        void onDetailOperationViewChange(ReadSettingInfo newData);

        /**
         * 选中某章
         */
        void onSelectChapter(TbBookChapter chapter);
    }

    private BaseActivity activity;

    @BindView(R.id.rlWgDpMenuList)
    protected RelativeLayout rlWgDpMenuList;
    @BindView(R.id.viewWgDpMenuListClose)
    protected View viewWgDpMenuListClose;
    @BindView(R.id.llWgDpMenuListData)
    protected LinearLayout llWgDpMenuListData;
    @BindView(R.id.tvWgDpMenuListTitle)
    protected TextView tvWgDpMenuListTitle;
    @BindView(R.id.rlWgDpMenuListOrder)
    protected RelativeLayout rlWgDpMenuListOrder;
    @BindView(R.id.ivWgDpMenuListOrder)
    protected ImageView ivWgDpMenuListOrder;
    @BindView(R.id.lvWgDpMenuList)
    protected MyListView lvWgDpMenuList;
    @BindView(R.id.llWgDpMenu)
    protected LinearLayout llWgDpMenu;
    @BindView(R.id.ivWgDpMenu)
    protected ImageView ivWgDpMenu;

    @BindView(R.id.llWgDpProcessContent)
    protected LinearLayout llWgDpProcessContent;
    @BindView(R.id.tvWgDpProcessTitle)
    protected TextView tvWgDpProcessTitle;
    @BindView(R.id.skWgDpProcess)
    protected SeekBar skWgDpProcess;
    @BindView(R.id.llWgDpProcess)
    protected LinearLayout llWgDpProcess;
    @BindView(R.id.ivWgDpProcess)
    protected ImageView ivWgDpProcess;

    @BindView(R.id.llWgDpLightContent)
    protected LinearLayout llWgDpLightContent;
    @BindView(R.id.skWgDpLight)
    protected SeekBar skWgDpLight;
    @BindView(R.id.lvWgDpLight1)
    protected LightView lvWgDpLight1;
    @BindView(R.id.lvWgDpLight2)
    protected LightView lvWgDpLight2;
    @BindView(R.id.lvWgDpLight3)
    protected LightView lvWgDpLight3;
    @BindView(R.id.lvWgDpLight4)
    protected LightView lvWgDpLight4;
    @BindView(R.id.lvWgDpLight5)
    protected LightView lvWgDpLight5;
    @BindView(R.id.llWgDpLight)
    protected LinearLayout llWgDpLight;
    @BindView(R.id.ivWgDpLight)
    protected ImageView ivWgDpLight;

    @BindView(R.id.llWgDpFrontContent)
    protected LinearLayout llWgDpFrontContent;
    @BindView(R.id.skWgDpFront)
    protected SeekBar skWgDpFront;
    @BindView(R.id.tvWgDpAnim1)
    protected TextView tvWgDpAnim1;
    @BindView(R.id.tvWgDpAnim2)
    protected TextView tvWgDpAnim2;
    @BindView(R.id.tvWgDpAnim3)
    protected TextView tvWgDpAnim3;
    @BindView(R.id.tvWgDpAnim4)
    protected TextView tvWgDpAnim4;
    @BindView(R.id.llWgDpFront)
    protected LinearLayout llWgDpFront;
    @BindView(R.id.ivWgDpFront)
    protected ImageView ivWgDpFront;

    private String title;
    private int bookId;

    private boolean menuListOrderAsc = true;

    private Thread thread;

    private IDetailOperationView listener;

    private List<TbBookChapter> lisMenuList;
    private BookDetailBottomMenuListAdapter menuListAdapter;


    private int hmWhat_loadMenuList = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == hmWhat_loadMenuList) {
                loadMenuList();
            }
        }
    };

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public DetailOperationView(Context context) {
        super(context);

        init();
    }

    public DetailOperationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DetailOperationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setData(String title, int bookId) {

        this.title = title;
        this.bookId = bookId;

        // 最后目录
        UtilitySecurity.setText(tvWgDpMenuListTitle, title);

        // 初始化目录列表
        initMenuList();

        resetReadInfo();

        initListener();
    }

    public void setListener(IDetailOperationView listener) {
        this.listener = listener;
    }

    private void init() {
        initLayout();

//        initListener();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View viewContent = inflater.inflate(R.layout.view_widget_detailoperation, null);

        ButterKnife.bind(this, viewContent);

        this.addView(viewContent, layoutParams);
    }

    private void initListener() {
        UtilitySecurityListener.setOnClickListener(this, viewWgDpMenuListClose, rlWgDpMenuListOrder);
        UtilitySecurityListener.setOnItemClickListener(lvWgDpMenuList, this);

        UtilitySecurityListener.setOnClickListener(this, llWgDpMenu, llWgDpProcess, llWgDpLight, llWgDpFront);

        skWgDpProcess.setOnSeekBarChangeListener(this);

        lvWgDpLight1.setListener(this);
        lvWgDpLight2.setListener(this);
        lvWgDpLight3.setListener(this);
        lvWgDpLight4.setListener(this);
        lvWgDpLight5.setListener(this);

        UtilitySecurityListener.setOnClickListener(this, tvWgDpAnim1, tvWgDpAnim2, tvWgDpAnim3, tvWgDpAnim4);

        // 加大进度条可点击区域
        Utility.addSeekBarTouchPoint(skWgDpLight);
        Utility.addSeekBarTouchPoint(skWgDpFront);
    }

    /**
     * 目录列表
     */
    public void initMenuList() {
        try {
            lvWgDpMenuList.setMaxHeight(LibUtility.getScreenHeight() - Utility.dip2px(165));

            if (thread != null && thread.getState() == Thread.State.RUNNABLE) {
                return;
            }

            if (lisMenuList != null)
                lisMenuList.clear();
            thread = new Thread() {
                @Override
                public void run() {
                    lisMenuList = AppDatabase.getInstance().ChapterDao().getChapterListByBookIdOrderByAsc(bookId);
                    mHandler.sendEmptyMessage(hmWhat_loadMenuList);
                }
            };
            thread.start();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private void loadMenuList() {
        if (UtilitySecurity.isEmpty(lisMenuList))
            return;

        try {
            menuListAdapter = new BookDetailBottomMenuListAdapter(lisMenuList);
            lvWgDpMenuList.setAdapter(menuListAdapter);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 排序目录列表
     */
    private void sortMenuList(boolean orderAsc) {
        UtilitySecurity.setImageResource(ivWgDpMenuListOrder, orderAsc ? R.mipmap.ic_book_down : R.mipmap.ic_book_up);
        menuListAdapter.setOrderByAes(orderAsc);
        UtilitySecurityListener.setOnClickListener(rlWgDpMenuListOrder, null);
        menuListAdapter.notifyDataSetChanged();
        lvWgDpMenuList.post(new Runnable() {
            @Override
            public void run() {
                UtilitySecurityListener.setOnClickListener(rlWgDpMenuListOrder, DetailOperationView.this);
                if (!UtilitySecurity.isEmpty(lisMenuList))
                    lvWgDpMenuList.setSelection(0);
            }
        });
    }

    private void resetReadInfo() {
        try {
            skWgDpProcess.setOnSeekBarChangeListener(null);
            skWgDpLight.setOnSeekBarChangeListener(null);
            skWgDpFront.setOnSeekBarChangeListener(null);

            // 由于本地章节列表可能会变更，所以阅读章节 放在点击目录的时候初始化
            // 亮度
            if (UtilityReadInfo.getReadSettingInfo().lightValue < 1) {
                int value = UtilityBrightness.getScreenBrightness(getContext());
                skWgDpLight.setProgress(value);
            } else {
                skWgDpLight.setProgress(UtilityReadInfo.getReadSettingInfo().lightValue);
            }
            switch (UtilityReadInfo.getReadSettingInfo().lightType) {
                case 1:
                    lvWgDpLight1.setSelect(true);
                    break;
                case 2:
                    lvWgDpLight2.setSelect(true);
                    break;
                case 3:
                    lvWgDpLight3.setSelect(true);
                    break;
                case 4:
                    lvWgDpLight4.setSelect(true);
                    break;
                case 5:
                    lvWgDpLight5.setSelect(true);
                    break;
            }
            // 字体
            skWgDpFront.setMax(ConstantSetting.MAX_FRONT);
            int defaultFrontSize = (int) (UtilityReadInfo.getReadSettingInfo().frontSize - ConstantSetting.frontSize) / 2;
            skWgDpFront.setProgress(defaultFrontSize);

            // 动画
            if (UtilityReadInfo.getReadSettingInfo().pageAnimType == PageMode.SIMULATION) {
                selectAnim1();
            } else if (UtilityReadInfo.getReadSettingInfo().pageAnimType == PageMode.COVER) {
                selectAnim2();
            } else if (UtilityReadInfo.getReadSettingInfo().pageAnimType == PageMode.SCROLL) {
                selectAnim3();
            } else if (UtilityReadInfo.getReadSettingInfo().pageAnimType == PageMode.NONE) {
                selectAnim4();
            }

            skWgDpProcess.setOnSeekBarChangeListener(this);
            skWgDpLight.setOnSeekBarChangeListener(this);
            skWgDpFront.setOnSeekBarChangeListener(this);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    private int getPositionByChapterId(long chapterId) {
        int position = 0;

        try {
            for (int i = 0; i < lisMenuList.size(); i++) {
                if (lisMenuList.get(i).chapterId == chapterId) {
                    position = i;
                    break;
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return position;
    }

    private void showMenu() {
        if (UtilitySecurity.isEmpty(lisMenuList))
            return;

        // 如果有阅读记录，定位到最后阅读记录
        TbReadHistory readHistory = AppDatabase.getInstance().ReadHistoryDao().getEntity(bookId);
        if (readHistory != null) {
            int lastReadChapterIdPosition = getPositionByChapterId(readHistory.chapterId);
            lastReadChapterIdPosition = getMenuListPosition(lastReadChapterIdPosition);
            lvWgDpMenuList.setSelection(lastReadChapterIdPosition);
        }

        UtilitySecurity.resetVisibility(llWgDpProcessContent, false);
        UtilitySecurity.setImageResource(ivWgDpProcess, R.mipmap.ic_book_progress_unselect);
        UtilitySecurity.resetVisibility(llWgDpLightContent, false);
        UtilitySecurity.setImageResource(ivWgDpLight, R.mipmap.ic_book_light_unselect);
        UtilitySecurity.resetVisibility(llWgDpFrontContent, false);
        UtilitySecurity.setImageResource(ivWgDpFront, R.mipmap.ic_book_set_unselect);

        UtilitySecurity.setImageResource(ivWgDpMenu, R.mipmap.ic_book_menu_select);
        showAnimation(R.anim.anim_fade_in, rlWgDpMenuList);
        showAnimation(R.anim.anim_widget_bookdetail_bottomshow, llWgDpMenuListData);
    }

    private void hideMenu() {
        UtilitySecurity.setImageResource(ivWgDpMenu, R.mipmap.ic_book_menu_unselect);
        hideAnimation(R.anim.anim_fade_out, rlWgDpMenuList);
        hideAnimation(R.anim.anim_widget_bookdetail_bottomhide, llWgDpMenuListData, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                UtilitySecurity.resetVisibility(llWgDpMenuListData, false);

                if (!menuListOrderAsc) {
                    menuListOrderAsc = true;
                    menuListAdapter.setOrderByAes(menuListOrderAsc);
                    menuListAdapter.notifyDataSetChanged();
                    UtilitySecurity.setImageResource(ivWgDpMenuListOrder, R.mipmap.ic_book_down);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

//        hideAnimation(R.anim.anim_fade_out, llWgDpMenuList);
    }

    private void showProcess() {
        if (UtilitySecurity.isEmpty(lisMenuList))
            return;

        try {
            skWgDpProcess.setMax(lisMenuList.size() - 1);

            // 定位到最后阅读章节
            TbReadHistory readHistory = AppDatabase.getInstance().ReadHistoryDao().getEntity(bookId);
            if (readHistory != null) {
                int lastReadChapterIdPosition = getPositionByChapterId(readHistory.chapterId);
                skWgDpProcess.setProgress(lastReadChapterIdPosition);
                UtilitySecurity.setText(tvWgDpProcessTitle, lisMenuList.get(lastReadChapterIdPosition).chapterName);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        UtilitySecurity.resetVisibility(rlWgDpMenuList, false);
        UtilitySecurity.setImageResource(ivWgDpMenu, R.mipmap.ic_book_menu_unselect);
        UtilitySecurity.resetVisibility(llWgDpProcessContent, false);
        UtilitySecurity.setImageResource(ivWgDpMenu, R.mipmap.ic_book_menu_unselect);
        UtilitySecurity.resetVisibility(llWgDpLightContent, false);
        UtilitySecurity.setImageResource(ivWgDpLight, R.mipmap.ic_book_light_unselect);
        UtilitySecurity.resetVisibility(llWgDpFrontContent, false);
        UtilitySecurity.setImageResource(ivWgDpFront, R.mipmap.ic_book_set_unselect);

        UtilitySecurity.setImageResource(ivWgDpProcess, R.mipmap.ic_book_progress_select);
        showAnimation(R.anim.anim_widget_bookdetail_bottomshow, llWgDpProcessContent);
//        showAnimation(R.anim.anim_fade_in, llWgDpProcessContent);
    }

    private void hideProcess() {
        UtilitySecurity.setImageResource(ivWgDpProcess, R.mipmap.ic_book_progress_unselect);
        hideAnimation(R.anim.anim_widget_bookdetail_bottomhide, llWgDpProcessContent);
//        hideAnimation(R.anim.anim_fade_out, llWgDpProcessContent);
    }

    private void showLight() {
        UtilitySecurity.resetVisibility(rlWgDpMenuList, false);
        UtilitySecurity.setImageResource(ivWgDpMenu, R.mipmap.ic_book_menu_unselect);
        UtilitySecurity.resetVisibility(llWgDpProcessContent, false);
        UtilitySecurity.setImageResource(ivWgDpProcess, R.mipmap.ic_book_progress_unselect);
        UtilitySecurity.resetVisibility(llWgDpFrontContent, false);
        UtilitySecurity.setImageResource(ivWgDpFront, R.mipmap.ic_book_set_unselect);

        UtilitySecurity.setImageResource(ivWgDpLight, R.mipmap.ic_book_light_select);
        showAnimation(R.anim.anim_widget_bookdetail_bottomshow, llWgDpLightContent);
//        showAnimation(R.anim.anim_fade_in, llWgDpLightContent);
    }

    private void hideLight() {
        UtilitySecurity.setImageResource(ivWgDpLight, R.mipmap.ic_book_light_unselect);
        hideAnimation(R.anim.anim_widget_bookdetail_bottomhide, llWgDpLightContent);
//        hideAnimation(R.anim.anim_fade_out, llWgDpLightContent);
    }

    private void showFront() {
        UtilitySecurity.resetVisibility(rlWgDpMenuList, false);
        UtilitySecurity.setImageResource(ivWgDpMenu, R.mipmap.ic_book_menu_unselect);
        UtilitySecurity.resetVisibility(llWgDpProcessContent, false);
        UtilitySecurity.setImageResource(ivWgDpProcess, R.mipmap.ic_book_progress_unselect);
        UtilitySecurity.resetVisibility(llWgDpLightContent, false);
        UtilitySecurity.setImageResource(ivWgDpLight, R.mipmap.ic_book_light_unselect);

        UtilitySecurity.setImageResource(ivWgDpFront, R.mipmap.ic_book_set_select);
        showAnimation(R.anim.anim_widget_bookdetail_bottomshow, llWgDpFrontContent);
//        showAnimation(R.anim.anim_fade_in, llWgDpFrontContent);
    }

    private void hideFront() {
        UtilitySecurity.setImageResource(ivWgDpFront, R.mipmap.ic_book_set_unselect);
        hideAnimation(R.anim.anim_widget_bookdetail_bottomhide, llWgDpFrontContent);
//        hideAnimation(R.anim.anim_fade_out, llWgDpFrontContent);
    }

    private void showAnimation(int animResourceId, View view) {
        UtilitySecurity.resetVisibility(view, true);

        Animation showAnimation = AnimationUtils.loadAnimation(getContext(), animResourceId);
        view.startAnimation(showAnimation);
    }

    private void hideAnimation(int animResourceId, View view, Animation.AnimationListener listener) {
        if (view.getVisibility() == View.GONE)
            return;

        Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), animResourceId);
        if (listener != null)
            hideAnimation.setAnimationListener(listener);
        else
            hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    UtilitySecurity.resetVisibility(view, false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        view.startAnimation(hideAnimation);
    }

    private void hideAnimation(int animResourceId, View view) {
        hideAnimation(animResourceId, view, null);
    }

    private void selectAnim1() {
        resetAnimButton();

        UtilitySecurity.setTextColor(tvWgDpAnim1, R.color.white);
        UtilitySecurity.setBackgroundResource(tvWgDpAnim1, R.drawable.bg_widget_bd_op_select);
    }

    private void selectAnim2() {
        resetAnimButton();

        UtilitySecurity.setTextColor(tvWgDpAnim2, R.color.white);
        UtilitySecurity.setBackgroundResource(tvWgDpAnim2, R.drawable.bg_widget_bd_op_select);
    }

    private void selectAnim3() {
        resetAnimButton();

        UtilitySecurity.setTextColor(tvWgDpAnim3, R.color.white);
        UtilitySecurity.setBackgroundResource(tvWgDpAnim3, R.drawable.bg_widget_bd_op_select);
    }

    private void selectAnim4() {
        resetAnimButton();

        UtilitySecurity.setTextColor(tvWgDpAnim4, R.color.white);
        UtilitySecurity.setBackgroundResource(tvWgDpAnim4, R.drawable.bg_widget_bd_op_select);
    }

    private void resetAnimButton() {
        try {
            UtilitySecurity.setTextColor(tvWgDpAnim1, R.color._5e60);
            UtilitySecurity.setBackgroundResource(tvWgDpAnim1, R.drawable.bg_widget_bd_op_unselect);

            UtilitySecurity.setTextColor(tvWgDpAnim2, R.color._5e60);
            UtilitySecurity.setBackgroundResource(tvWgDpAnim2, R.drawable.bg_widget_bd_op_unselect);

            UtilitySecurity.setTextColor(tvWgDpAnim3, R.color._5e60);
            UtilitySecurity.setBackgroundResource(tvWgDpAnim3, R.drawable.bg_widget_bd_op_unselect);

            UtilitySecurity.setTextColor(tvWgDpAnim4, R.color._5e60);
            UtilitySecurity.setBackgroundResource(tvWgDpAnim4, R.drawable.bg_widget_bd_op_unselect);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 100毫秒内只执行最后一次(防止快还未绘制完就速改变字体重新绘制)
     */
    private void onChange() {

        if (listener != null)
            listener.onDetailOperationViewChange(UtilityReadInfo.getReadSettingInfo());

        EditSharedPreferences.setReadSettingInfo(UtilityReadInfo.getReadSettingInfo());
    }

    private void resetLightStat() {
        lvWgDpLight1.setSelect(false);
        lvWgDpLight2.setSelect(false);
        lvWgDpLight3.setSelect(false);
        lvWgDpLight4.setSelect(false);
        lvWgDpLight5.setSelect(false);
    }

    /**
     * 是否有展示的menu content
     */
    public boolean isShowMenuContent() {
        return (rlWgDpMenuList.getVisibility() == View.VISIBLE)
                || (llWgDpProcessContent.getVisibility() == View.VISIBLE)
                || (llWgDpLightContent.getVisibility() == View.VISIBLE)
                || (llWgDpFrontContent.getVisibility() == View.VISIBLE);

    }

    /**
     * 隐藏所有menu content
     */
    public void hideAllMenuContent() {
        close();
        hideMenu();
        hideProcess();
        hideLight();
        hideFront();
    }

    public void close() {
        try {
            if (thread != null)
                thread.interrupt();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 亮度单选
     */
    @Override
    public void onStatChange(View view, boolean select) {

        if (select) {
            resetLightStat();

            switch (view.getId()) {
                case R.id.lvWgDpLight1:
                    UtilityReadInfo.getReadSettingInfo().lightType = ConstantSetting.LIGHTTYPE_1;
                    UtilityReadInfo.getReadSettingInfo().frontColor = R.color.black;
                    lvWgDpLight1.setSelect(true);
                    break;
                case R.id.lvWgDpLight2:
                    UtilityReadInfo.getReadSettingInfo().lightType = ConstantSetting.LIGHTTYPE_2;
                    UtilityReadInfo.getReadSettingInfo().frontColor = R.color.gray_2d2d;
                    lvWgDpLight2.setSelect(true);
                    break;
                case R.id.lvWgDpLight3:
                    UtilityReadInfo.getReadSettingInfo().lightType = ConstantSetting.LIGHTTYPE_3;
                    UtilityReadInfo.getReadSettingInfo().frontColor = R.color.gray_3f4c;
                    lvWgDpLight3.setSelect(true);
                    break;
                case R.id.lvWgDpLight4:
                    UtilityReadInfo.getReadSettingInfo().lightType = ConstantSetting.LIGHTTYPE_4;
                    UtilityReadInfo.getReadSettingInfo().frontColor = R.color.gray_442e;
                    lvWgDpLight4.setSelect(true);
                    break;
                case R.id.lvWgDpLight5:
                    UtilityReadInfo.getReadSettingInfo().lightType = ConstantSetting.LIGHTTYPE_5;
                    UtilityReadInfo.getReadSettingInfo().frontColor = R.color.gray_3333;
                    lvWgDpLight5.setSelect(true);
                    break;
            }
        } else {
            UtilityReadInfo.getReadSettingInfo().lightType = ConstantSetting.LIGHTTYPE_1;
        }

        onChange();
    }

    private int getMenuListPosition(int position) {
        if (UtilitySecurity.isEmpty(lisMenuList))
            return 0;
        else
            return menuListOrderAsc ? position : UtilitySecurity.size(lisMenuList) - 1 - position;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            if (listener != null)
                listener.onSelectChapter(lisMenuList.get(getMenuListPosition(position)));

            hideAllMenuContent();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int processValue, boolean b) {

        try {
            UtilitySecurity.setText(tvWgDpProcessTitle, lisMenuList.get(processValue).chapterName);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int processValue = seekBar.getProgress();

        Logger.firstE("onStopTrackingTouch:" + processValue);

        switch (seekBar.getId()) {
            // 进度
            case R.id.skWgDpProcess:
                TbBookChapter bookChapter = lisMenuList.get(processValue);
                UtilitySecurity.setText(tvWgDpProcessTitle, bookChapter.chapterName);
                hideAllMenuContent();

                if (listener != null)
                    listener.onSelectChapter(bookChapter);
                break;

            // 亮度
            case R.id.skWgDpLight:
                UtilityReadInfo.getReadSettingInfo().lightValue = (processValue);
                onChange();
                break;

            // 字体
            case R.id.skWgDpFront:
                float newFrontSize = ConstantSetting.frontSize + (processValue * 2);
                UtilityReadInfo.getReadSettingInfo().frontSize = newFrontSize;
                UtilityReadInfo.getReadSettingInfo().lineSpacingExtra = UtilityMeasure.getLineSpacingExtra(newFrontSize);
                onChange();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (LibUtility.isFastDoubleClick())
            return;

        if (activity.getStatusTip().isShowing())
            return;

        switch (view.getId()) {

            // 目录空白区域：关闭目录
            case R.id.viewWgDpMenuListClose:
                hideMenu();
                break;

            // 目录列表
            case R.id.llWgDpMenu:
                if (rlWgDpMenuList.getVisibility() == View.VISIBLE) {
                    hideMenu();
                } else {
                    showMenu();
                }
                break;

            // 正序或倒序 目录列表
            case R.id.rlWgDpMenuListOrder:
                menuListOrderAsc = !menuListOrderAsc;
                sortMenuList(menuListOrderAsc);
                break;

            // 进度
            case R.id.llWgDpProcess:
                if (llWgDpProcessContent.getVisibility() == View.VISIBLE)
                    hideProcess();
                else
                    showProcess();
                break;

            // 亮度
            case R.id.llWgDpLight:
                if (llWgDpLightContent.getVisibility() == View.VISIBLE)
                    hideLight();
                else
                    showLight();
                break;

            // 字体和翻页
            case R.id.llWgDpFront:
                if (llWgDpFrontContent.getVisibility() == View.VISIBLE)
                    hideFront();
                else
                    showFront();
                break;

            // 翻页动画
            case R.id.tvWgDpAnim1:
                selectAnim1();
                UtilityReadInfo.getReadSettingInfo().pageAnimType = PageMode.SIMULATION;
                hideAllMenuContent();
                onChange();
                break;
            case R.id.tvWgDpAnim2:
                selectAnim2();
                UtilityReadInfo.getReadSettingInfo().pageAnimType = PageMode.COVER;
                hideAllMenuContent();
                onChange();
                break;
            case R.id.tvWgDpAnim3:
                selectAnim3();
                UtilityReadInfo.getReadSettingInfo().pageAnimType = PageMode.SCROLL;
                hideAllMenuContent();
                onChange();
                break;
            case R.id.tvWgDpAnim4:
                selectAnim4();
                UtilityReadInfo.getReadSettingInfo().pageAnimType = PageMode.NONE;
                hideAllMenuContent();
                onChange();
                break;
        }
    }
}