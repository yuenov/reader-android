package com.yuenov.open.utils.images;

import android.content.Context;
import android.widget.ImageView;

import com.yuenov.open.R;
import com.yuenov.open.application.MyApplication;
import com.yuenov.open.model.ImageModel;
import com.yuenov.open.utils.UtilityException;
import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * banner
 */
public class BannerGlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        try {
            ImageModel itemModel = (ImageModel) path;

            Glide.with(MyApplication.getAppContext())
                    .load(itemModel.image)
                    .apply(UtilityImage.getFilletGildeOptions(R.mipmap.detailinfo_default_bg, 10, true, true, true, true))
                    .into(imageView);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}