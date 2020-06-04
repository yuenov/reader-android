package com.yuenov.open.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zhouwei.blurlibrary.EasyBlur;

public class UtilityBlur {
    /**
     * 使用 资源图片 模糊
     *
     * @param imageID 资源图片 id
     */
    public static void blur(ImageView imageView, int imageID) {
        try {
            Bitmap overlay = BitmapFactory.decodeResource(imageView.getContext().getResources(), imageID);
            Bitmap finalBitmap = EasyBlur.with(imageView.getContext())
                    .bitmap(overlay) //要模糊的图片
                    .radius(3)//模糊半径
                    .blur();
            imageView.setImageBitmap(finalBitmap);
        } catch (Exception e) {
            UtilityException.catchException(e);
        }
    }

    /**
     * 使用 网络图片 模糊
     *
     * @param imgUrl 图片地址
     */
    public static void blur(final ImageView imageView, String imgUrl) {
        Glide.with(imageView.getContext()).asBitmap().load(imgUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                try {
//                    Bitmap finalBitmap = EasyBlur.with(MyApplication.getAppContext())
//                            .bitmap(bitmap) //要模糊的图片
//                            .radius(5)//模糊半径
//                            .blur();

                    Bitmap finalBitmap = EasyBlur.with(imageView.getContext())
                            .bitmap(bitmap) //要模糊的图片
                            .radius(10)//模糊半径
                            .scale(9)//指定模糊前缩小的倍数
                            .policy(EasyBlur.BlurPolicy.FAST_BLUR)//使用fastBlur
                            .blur();

                    imageView.setImageBitmap(finalBitmap);
                } catch (Exception e) {
                    UtilityException.catchException(e);
                }
            }
        });
    }
}
