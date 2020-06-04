package com.yuenov.open.utils.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.yuenov.open.application.MyApplication;
import com.yuenov.open.constant.ConstantInterFace;
import com.yuenov.open.utils.Utility;
import com.yuenov.open.utils.UtilityException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.renrui.libraries.util.Logger;
import com.renrui.libraries.util.UtilitySecurity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片相关的公共方法
 */
public class UtilityImage {

    private static RequestOptions rq;

    public static RequestOptions getGildeOptions(int defaultResourcesID, boolean isCentercrop) {

        rq = new RequestOptions();
        if (isCentercrop) {
            rq = rq.centerCrop();
        }

        rq.placeholder(defaultResourcesID)
                .skipMemoryCache(false)
                .error(defaultResourcesID)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        return rq;
    }

    public static RequestOptions getGildeOptions(int defaultResourcesID) {
        rq = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(false)
                .placeholder(defaultResourcesID)
                .error(defaultResourcesID)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        return rq;
    }

    public static RequestOptions getGildeOptions(Bitmap bm) {
        rq = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(false)
                .placeholder(new BitmapDrawable(bm))
                .error(new BitmapDrawable(bm))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        return rq;
    }

    /**
     * 圆角
     *
     * @param defaultResourcesID
     * @param radius             dp
     * @param leftTop            是否圆角
     * @param rightTop           是否圆角
     * @param leftBottom         是否圆角
     * @param rightBottom        是否圆角
     * @return
     */
    public static RequestOptions getFilletGildeOptions(int defaultResourcesID, int radius, boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        rq = new RequestOptions()
                .skipMemoryCache(false)
                .placeholder(defaultResourcesID)
                .error(defaultResourcesID)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);


        CornerTransform transformation = new CornerTransform(MyApplication.getAppContext(), Utility.dip2px(radius));
        transformation.setExceptCorner(!leftTop, !rightTop, !leftBottom, !rightBottom);
        rq.transform(transformation);

        return rq;
    }

    public static RequestOptions getImGildeOptions(int defaultResourcesID) {
        rq = new RequestOptions()
                .skipMemoryCache(false)
                .placeholder(defaultResourcesID)
                .error(defaultResourcesID)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        return rq;
    }

    public static void setLocalImage(ImageView iv, String filePath) {

        if (iv == null || TextUtils.isEmpty(filePath)) {
            return;
        }

        try {
            File file = new File(filePath);
            Glide.with(MyApplication.getAppContext()).load(file).into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static void setImage(ImageView iv, String iconUrl, Bitmap defaultBitmap, int width, int height) {

        if (iv == null || null == defaultBitmap) {
            return;
        }

        try {
            Glide.with(MyApplication.getAppContext())
                    .load(getImageUrl(iconUrl))
                    .apply(UtilityImage.getGildeOptions(defaultBitmap))
                    .into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static void setImage(ImageView iv, String iconUrl, int defaultResourcesID, int width, int height, RequestListener listener) {

        try {
            Glide.with(MyApplication.getAppContext())
                    .load(getImageUrl(iconUrl))
                    .apply(UtilityImage.getGildeOptions(defaultResourcesID))
                    .listener(listener)
                    .into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 设置图片
     */
    public static void setImage(ImageView iv, String iconUrl, int defaultResourcesID) {
        try {
            Glide.with(MyApplication.getAppContext())
                    .load(getImageUrl(iconUrl))
                    .apply(UtilityImage.getImGildeOptions(defaultResourcesID))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 设置图片
     */
    public static void setImage(ImageView iv, String iconUrl, int defaultResourcesID, boolean isCentercrop) {
        try {
            Glide.with(MyApplication.getAppContext())
                    .load(getImageUrl(iconUrl))
                    .apply(UtilityImage.getGildeOptions(defaultResourcesID, isCentercrop))
                    .into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 设置图片
     */
    public static void setImage(ImageView iv, String iconUrl, int defaultResourcesID, RequestListener listener) {
        try {
            Glide.with(MyApplication.getAppContext())
                    .load(getImageUrl(iconUrl))
                    .apply(UtilityImage.getGildeOptions(defaultResourcesID))
                    .listener(listener)
                    .into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 网络加载图片
     */
    public static void setImage(ImageView iv, String iconUrl) {
        try {
            Glide.with(MyApplication.getAppContext())
                    .load(getImageUrl(iconUrl))
                    .into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 设置图片
     */
    public static void setImImage(ImageView iv, String iconUrl, int defaultResourcesID) {
        try {
            Glide.with(MyApplication.getAppContext())
                    .load(getImageUrl(iconUrl))
                    .apply(UtilityImage.getGildeOptions(defaultResourcesID).fitCenter())
                    .into(iv);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static String getImageUrl(String url) {
        if (!UtilitySecurity.isEmpty(url) && url.startsWith("/")) {
            return ConstantInterFace.getImageDomain() + url;
        }
        else {
            return url;
        }
    }

    /**
     * 获取图片的旋转度
     *
     * @param path 图片的路径
     * @return 返回图片的旋转度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
        return degree;
    }

    /**
     * 将图片旋转一定的角度
     *
     * @param bm     需要旋转的图片
     * @param degree 图片旋转的角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        //根据旋转角度生成对应的旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        if (returnBm == null) {
            returnBm = bm;
        }

        if (bm != returnBm) {
            bm.recycle();
        }

        return returnBm;
    }

    /**
     * 删除之前的图片
     */
    public static void deleteFile(String filePath) {
        try {
            if (TextUtils.isEmpty(filePath)) {
                return;
            }

            File imageFile = new File(filePath);

            if (imageFile.exists()) {
                imageFile.delete();
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius) {
        try {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            /**
             * 权重为0时，原样输出。
             * 源码原来的判断值时radius<1，这里改为以下的radius<0
             * 主要是为了让权重为0时，可输出
             */
            if (radius < 0) {
                return (null);
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);

            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;

            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];

            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }

            yw = yi = 0;

            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;

            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;

                for (x = 0; x < w; x++) {

                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];

                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    sir = stack[i + radius];

                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];

                    rbs = r1 - Math.abs(i);

                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                            | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];

                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi += w;
                }
            }

            bitmap.setPixels(pix, 0, w, 0, 0, w, h);

            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存图片
     *
     * @param binaryData 图片流
     * @param savePath   保存的文件路径
     */
    public static boolean saveImage(byte[] binaryData, String savePath, String parentPath) {
        if (binaryData == null || binaryData.length == 0 || TextUtils.isEmpty(savePath)) {
            return false;
        }

        boolean saveResult;

        // 压缩比例
        int quality = 100;
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);

            // 压缩格式
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;

            if (!UtilitySecurity.isEmpty(parentPath)) {
                File filePath = new File(parentPath);
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
            }

            File file = new File(savePath);

            // 如果文件的目录不存在，创建目录
            File filePath = new File(file.getParent());
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            // 如果文件已存在，删除文件
            if (file.exists()) {
                file.delete();
            }

            // 创建空文件
            file.createNewFile();

            // 压缩输出文件
            OutputStream stream = new FileOutputStream(file);
            bmp.compress(format, quality, stream);
            stream.close();
            saveResult = true;

            Logger.e("file 保存成功");
        } catch (Exception e) {
            UtilityException.catchException(e);
            saveResult = false;
            Logger.e("file 保存失败：" + e.getMessage());
        }

        return saveResult;
    }

    public static boolean saveImage(byte[] binaryData, String savePath) {
        return saveImage(binaryData, savePath, "");
    }

    public static byte[] getByte(InputStream is) {
        byte[] in2b = new byte[]{};

        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }

            in2b = swapStream.toByteArray();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return in2b;
    }

    /**
     * 保存图片 (保存之前先压缩,最大为1.5MB, 保存图片类型是png)
     */
    public static void saveImageNoY(Bitmap bm, String fileAllName) {

        int options = 100;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        File f = new File(fileAllName);
        if (f.exists()) {
            f.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, options, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            UtilityException.catchException(e);
        }
    }

    /**
     * 截取scrollview的屏幕
     *
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        Bitmap bitmap = null;

        try {
            int h = 0;
            // 获取scrollview实际高度
            for (int i = 0; i < scrollView.getChildCount(); i++) {
                h += scrollView.getChildAt(i).getHeight();
            }
            // 创建对应大小的bitmap
            bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            scrollView.draw(canvas);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        Bitmap bitmap = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            // 循环判断如果压缩后图片是否大于1mb,大于继续压缩
            while (baos.toByteArray().length / 1024 * 1024 > 1 && options > 10) {
                // 重置baos
                baos.reset();
                // 这里压缩options%，把压缩后的数据存放到baos中
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);

                // 每次都减少10
                options -= 10;
            }
            // 把压缩后的数据baos存放到ByteArrayInputStream中
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            // 把ByteArrayInputStream数据生成图片
            bitmap = BitmapFactory.decodeStream(isBm, null, null);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    public static byte[] compressImageToByte(Bitmap image) {
        byte[] by = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            // 循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            while (baos.toByteArray().length / 1024f > 1000) {
                // 重置baos
                baos.reset();
                // 这里压缩options%，把压缩后的数据存放到baos中
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);

                if (options <= 10) {
                    break;
                }

                // 每次都减少10
                options -= 10;
            }
            by = baos.toByteArray();
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return by;
    }

    /**
     * 获取sd卡中的图片
     *
     * @param pathString 路径
     */
    public static Bitmap getDiskBitmap(String pathString) {

        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            UtilityException.catchException(e);
        }

        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImageToBitmap(Bitmap image) {

        try {
            return Bytes2Bitmap(compressImageToByte(image));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
            return null;
        }
    }

    // byte[]转换成Bitmap
    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    /**
     * 获取图片的宽度
     */
    public static int getImageWidth(String path) {
        if (UtilitySecurity.isEmpty(path)) return 0;
        try {
            //获取Options对象
            BitmapFactory.Options options = new BitmapFactory.Options();
            //仅做解码处理，不加载到内存
            options.inJustDecodeBounds = true;
            //解析文件
            BitmapFactory.decodeFile(path, options);
            //获取宽高
            return options.outWidth;
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
        return 0;
    }

    /**
     * 获取图片高度
     */
    public static int getImageHeight(String path) {
        if (UtilitySecurity.isEmpty(path)) return 0;
        try {
//获取Options对象
            BitmapFactory.Options options = new BitmapFactory.Options();
            //仅做解码处理，不加载到内存
            options.inJustDecodeBounds = true;
            //解析文件
            BitmapFactory.decodeFile(path, options);
            //获取宽高
            return options.outHeight;
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
        return 0;
    }
}