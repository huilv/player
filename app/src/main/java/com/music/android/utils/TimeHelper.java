package com.music.android.utils;

import android.content.Context;

import com.google.android.exoplayer2.C;
import com.music.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by hui.lv on 2017/3/14.
 */

public class TimeHelper {

    public static String formatTime(long timeMs) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString() : formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    public static String getTime(Context context) {
        SimpleDateFormat format=new SimpleDateFormat("HH:mm");
        Date d1=new Date(System.currentTimeMillis());
        String time=format.format(d1);
        return time;
    }
    public static int getData(Context context) {
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        return date;
    }

    public static String getWeek(Context context) {
        String Week = "";
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new Date(System.currentTimeMillis()));
        } catch (Exception e) {

        }
        int wk = c.get(Calendar.DAY_OF_WEEK);
        if (wk== Calendar.MONDAY) {
            Week = context.getResources().getString(R.string.monday);
        } else if (wk == Calendar.TUESDAY) {
            Week = context.getResources().getString(R.string.tuesday);
        } else if (wk== Calendar.WEDNESDAY) {
            Week = context.getResources().getString(R.string.wednesday);
        } else if (wk == Calendar.THURSDAY) {
            Week = context.getResources().getString(R.string.thursday);
        } else if (wk== Calendar.FRIDAY) {
            Week = context.getResources().getString(R.string.friday);
        } else if (wk == Calendar.SATURDAY) {
            Week = context.getResources().getString(R.string.saturday);
        } else if (wk == Calendar.SUNDAY) {
            Week = context.getResources().getString(R.string.sunday);
        }
            return Week;
    }


    public static String getMonth(Context context) {
        String month = "";
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new Date(System.currentTimeMillis()));
        } catch (Exception e) {

        }
        int  mth= c.get(Calendar.MONTH) ;
        if (mth == Calendar.JANUARY) {
            month = context.getResources().getString(R.string.january);
        }else if (mth == Calendar.FEBRUARY) {
            month = context.getResources().getString(R.string.february);
        }else if (mth == Calendar.MARCH) {
            month = context.getResources().getString(R.string.march);
        }else if (mth == Calendar.APRIL) {
            month = context.getResources().getString(R.string.april);
        }else if (mth == Calendar.MAY) {
            month = context.getResources().getString(R.string.may);
        }else if (mth == Calendar.JUNE) {
            month = context.getResources().getString(R.string.june);
        }else if (mth == Calendar.JULY) {
            month = context.getResources().getString(R.string.july);
        }else if (mth == Calendar.AUGUST) {
            month = context.getResources().getString(R.string.august);
        }else if (mth == Calendar.SEPTEMBER) {
            month = context.getResources().getString(R.string.september);
        }else if (mth == Calendar.OCTOBER) {
            month = context.getResources().getString(R.string.october);
        }else if (mth == Calendar.NOVEMBER) {
            month = context.getResources().getString(R.string.november);
        }else if (mth == Calendar.DECEMBER) {
            month = context.getResources().getString(R.string.december);
        }


        return month;
    }

}
