package cn.tiancaifan.enjoy_music.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.concurrent.ExecutionException;

/**
 * @ClassName: ImageUtils
 * @Description: 图片工具类
 * @Date: 2022/4/8 17:14
 * @Author: fanxiaofan
 */
public class ImageUtils {

    public static Drawable getDrawableGlide(String url, Context mContext) {
        try {
            return Glide.with(mContext)
                    .load(url)
                    .submit()
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这是一个耗时的操作需要异步处理
     *
     * @param url 通过URL得到 Bitmap
     * @return
     */
    public static Bitmap getBitmapGlide(String url, Context mContext) {
        try {
            return Glide.with(mContext)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这是一个耗时的操作需要异步处理
     *
     * @param url 通过URL得到 模糊Bitmap
     * @return
     */
    public static Bitmap getBlurBitmapGlide(String url,int radius, int sampling, Context mContext) {
        try {
            return Glide.with(mContext)
                    .asBitmap()
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(radius,sampling)))
                    .load(url)
                    .submit()
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap, Context mContext){
        Drawable drawable = new BitmapDrawable(mContext.getResources(),bitmap);
        return drawable;
    }


    /**
     * 裁剪出最小边的正方形
     * @param bm 输入图片
     */
    public static Bitmap cutBitmap(Bitmap bm) {
        Bitmap bitmap = bm;
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width>height){
            bitmap = Bitmap.createBitmap(bm,0,(width-height)/2,height,height);
        }else if(width>height){
            bitmap = Bitmap.createBitmap(bm,(height-width)/2,0,width,width);
        }
        return bitmap;

    }

}
