package cn.tiancaifan.enjoy_music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.DrawableTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * @ClassName: BlurTransformation
 * @Description: Glide 高斯模糊
 * @Date: 2022/4/8 16:49
 * @Author: fanxiaofan
 */
public class BlurTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID = "BlurTransformation." + VERSION;

    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private int radius;
    private int sampling;
    private Context context;

    public BlurTransformation() {
        this(MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius) {
        this(radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius, int sampling) {
        this.radius = radius;
        this.sampling = sampling;
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;

        Bitmap bitmap = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform, 0, 0, paint);
        bitmap = FastBlur.doBlur(bitmap, radius, true);

        return bitmap;
    }

    @Override public String toString() {
        return "BlurTransformation(radius=" + radius + ", sampling=" + sampling + ")";
    }

    @Override public boolean equals(Object o) {
        return o instanceof BlurTransformation &&
                ((BlurTransformation) o).radius == radius &&
                ((BlurTransformation) o).sampling == sampling;
    }

    @Override public int hashCode() {
        return ID.hashCode() + radius * 1000 + sampling * 10;
    }

    @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + radius + sampling).getBytes(CHARSET));
    }
}

