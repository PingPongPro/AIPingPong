package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DensityUtil {
    public static final int PX=0;
    public static final int SP=1;
    public static final int DP=2;
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density; 
        return  (dpValue * scale + 0.5f);
    }
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (pxValue / scale + 0.5f);
    }
    public static float px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale + 0.5f);
    }
    public static float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return  (spValue * fontScale + 0.5f);
    }
    public static Drawable scaleImage(Context context,int id,float height,float width,int mode)
    {
        Resources resources=context.getResources();
        float imageWidth=width;
        float imageHeight=height;
        switch (mode)
        {
            case SP:
                imageWidth=sp2px(context,imageWidth);
                imageHeight=sp2px(context,imageHeight);
                break;
            case DP:
                imageHeight=dip2px(context,imageHeight);
                imageWidth=dip2px(context,imageWidth);
                break;
        }
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeResource(resources,id);
        int bitmapHeight=bitmap.getHeight();
        int bitmapWidth=bitmap.getWidth();
        float numx = imageWidth / (float)bitmapWidth;
        float numy = imageHeight / (float)bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(numx, numy);
        // 缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth,
                bitmapHeight, matrix, true);
        //System.out.println(px2dip(context,resizeBitmap.getHeight())+" "+px2dip(context,resizeBitmap.getWidth()));
        Drawable drawable = new BitmapDrawable(resizeBitmap);
        return drawable;
    }
}  