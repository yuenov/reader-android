package com.yuenov.open.utils;

import com.yuenov.open.model.ImageModel;
import com.yuenov.open.utils.images.BannerGlideImageLoader;
import com.renrui.libraries.util.LibUtility;
import com.renrui.libraries.util.UtilitySecurity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class UtilityControl {


    public static void bindBannerData(Banner banner, final List<ImageModel> lisData) {

        if (banner == null || lisData == null || lisData.isEmpty()) {
            UtilitySecurity.resetVisibility(banner, false);
            return;
        }

        try {
            banner.setImageLoader(new BannerGlideImageLoader());
            //设置图片集合
            banner.setImages(lisData);
            //设置banner动画效果
            banner.setBannerAnimation(Transformer.Accordion);
            //设置标题集合（当banner样式有显示title时）
            banner.setBannerTitles(new ArrayList<String>());
            //设置自动轮播，默认为true
            banner.isAutoPlay(true);
            //设置轮播时间
            banner.setDelayTime(4000);
            //设置指示器位置（当banner模式中有指示器时）
            banner.setIndicatorGravity(BannerConfig.CENTER);
            banner.setBannerAnimation(Transformer.Default);

            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    if (LibUtility.isFastDoubleClick()) {
                        return;
                    }

                }
            });

            banner.start();

            UtilitySecurity.resetVisibility(banner, true);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}
