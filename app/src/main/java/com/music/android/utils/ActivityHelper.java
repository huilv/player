package com.music.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by hui.lv on 2017/3/14.
 */

public class ActivityHelper {

    /**
     * 存放activity的列表
     */
    public HashMap<Class<?>, Activity> hashMap = new LinkedHashMap<>();
    private static ActivityHelper activityCollector;


    public ActivityHelper() {
    }

    public static ActivityHelper getDefault() {
        if (activityCollector == null) {
            synchronized (ActivityHelper.class) {
                activityCollector = new ActivityHelper();
            }
        }
        return activityCollector;
    }

    /**
     * 添加Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity, Class<?> clz) {
        hashMap.put(clz, activity);
    }

    /**
     * 判断一个Activity 是否存在
     *
     * @param clz
     * @return
     */
    public <T extends Activity> boolean isActivityExist(Class<T> clz) {
        boolean res;
        Activity activity = getActivity(clz);
        if (activity == null) {
            res = false;
        } else {
            if (activity.isFinishing()) {
                res = false;
            } else {
                res = true;
            }
        }

        return res;
    }

    /**
     * 获得指定activity实例
     *
     * @param clazz Activity 的类对象
     * @return
     */
    public <T extends Activity> T getActivity(Class<T> clazz) {
        return (T) hashMap.get(clazz);
    }

    /**
     * 移除activity,代替finish
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (hashMap.containsValue(activity)) {
            hashMap.remove(activity.getClass());
        }
    }

    /**
     * 移除所有的Activity
     */
    public void removeAllActivity() {
        if (hashMap != null && hashMap.size() > 0) {
            Set<Entry<Class<?>, Activity>> sets = hashMap.entrySet();
            for (Entry<Class<?>, Activity> s : sets) {
                if (!s.getValue().isFinishing()) {
                    s.getValue().finish();
                }
            }
        }
        hashMap.clear();
    }

}