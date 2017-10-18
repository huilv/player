package com.music.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by liuyun on 15/9/6.
 */
public class SizeUtils {
    /**
     * @param context
     * @param dp
     * @return
     */
    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * @param context
     * @param px
     * @return
     */
    public static int px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param *       （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 得到文字的长度
     *
     * @param textView
     * @param text
     * @return
     */
    public static float getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        float textLength = paint.measureText(text);
        return textLength;
    }

    /**
     *  long long 的除法运算
     * @param currentProgress
     * @param totalProgress
     * @return
     */

    public static float div(long currentProgress, long totalProgress)  {
        totalProgress=(totalProgress==0?1:totalProgress);
        BigDecimal x = BigDecimal.valueOf(totalProgress);
        BigDecimal d = BigDecimal.valueOf(currentProgress).divide(x, new MathContext(5));
//        L.i("SizeUtils","currentProgress / totalProgress = " + d.floatValue());
        return (d.floatValue()<0?0:d.floatValue());
    }
    public static String formatFileSize(long length) {
        String result = null;
        int sub_string = 0;
        // 如果文件长度大于1GB
        if (length >= 1073741824) {
            sub_string = String.valueOf((float) length / 1073741824).indexOf(
                    ".");
            result = ((float) length / 1073741824 + "000").substring(0,
                    sub_string + 3) + "GB";
        } else if (length >= 1048576) {
            // 如果文件长度大于1MB且小于1GB,substring(int beginIndex, int endIndex)
            sub_string = String.valueOf((float) length / 1048576).indexOf(".");
            result = ((float) length / 1048576 + "000").substring(0,
                    sub_string + 3) + "MB";
        } else if (length >= 1024) {
            // 如果文件长度大于1KB且小于1MB
            sub_string = String.valueOf((float) length / 1024).indexOf(".");
            result = ((float) length / 1024 + "000").substring(0,
                    sub_string + 3) + "KB";
        } else if (length < 1024)
            result = Long.toString(length) + "B";
        return result;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourcesId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourcesId);
        return height;
    }

    /**
     * 获得当前版本号
     * @param context
     * @return
     */
    public static String getVersionCode(Context context){
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode="";
        try {
            packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            versionCode=packageInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    /**
     *  格式化数字大小
     * @param text
     * @return
     */
    public static  String  formText(String text) {
        StringBuffer buffer=new StringBuffer();
        char[] chars = text.toCharArray();
        int length = chars.length;
        if(length<4){
            return  text;
        }else if(length==4){//千
            char aa = chars[0];
            buffer.append(aa);
            buffer.append("k");
            return buffer.toString();
        }else if(length==5){//万
            char aa = chars[0];
            char bb = chars[1];
            buffer.append(aa);
            buffer.append(bb);
            buffer.append("k");
            return buffer.toString();
        }else if(length==6){//十万
            char aa = chars[0];
            char bb = chars[1];
            char cc = chars[2];
            buffer.append(aa);
            buffer.append(bb);
            buffer.append(cc);
            buffer.append("k");
            return buffer.toString();
        }else if(length==7){//百万
            char aa = chars[0];
            buffer.append(aa);
            buffer.append(".");
            buffer.append("m");
            return buffer.toString();
        }else if(length==8){//千万
            char aa = chars[0];
            char bb = chars[1];
            char cc = chars[2];
            char dd = chars[3];
            char ee=  chars[4];
            buffer.append(aa);
            buffer.append(bb);
            buffer.append("m");
            return buffer.toString();
        }else if(length==9){//亿
            char aa = chars[0];
            char bb = chars[1];
            char cc = chars[2];
            buffer.append(aa);
            buffer.append(bb);
            buffer.append(cc);
            buffer.append("m");
            return buffer.toString();
        }else if(length==10){
            char aa = chars[0];
            char bb = chars[1];
            char cc = chars[2];
            char dd = chars[3];
            buffer.append(aa);
            buffer.append(bb);
            buffer.append(cc);
            buffer.append(dd);
            buffer.append("m");
            return buffer.toString();
        }
        return text;
    }

}
