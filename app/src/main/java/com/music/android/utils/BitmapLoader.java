package com.music.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import com.music.android.MusicApp;
import com.music.android.listener.OnBitmapLoadSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hui.lv on 2017/3/24.
 */

public class BitmapLoader {
    private File cacheFileDir;
    private Bitmap bitmap;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static ThreadFactory sThreadFactory = new ThreadFactory() {
        private AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "BitmapLoader #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    };
    private static ThreadPoolExecutor executors = new ThreadPoolExecutor(3, 5, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10), sThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(1024 * 1024 * 4) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getHeight() * value.getWidth();
        }
    };

    public BitmapLoader() {
        cacheFileDir = new File(MusicApp.context.getCacheDir().getAbsolutePath());
    }


    public void load(String path, OnBitmapLoadSuccessListener listener) {
        executors.execute(new MusicRunnable(path, listener));
    }

    private class MusicRunnable implements Runnable {
        private String path;
        private OnBitmapLoadSuccessListener listener;

        private MusicRunnable(String path, OnBitmapLoadSuccessListener listener) {
            this.path = path;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                bitmap = mLruCache.get(path);
                if (bitmap != null) {
                    post(listener, path,200);
                    return;
                }
                String encode = MD5Encoder.encode(path);
                File file = new File(cacheFileDir, encode);
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    post(listener, path,500);
                    return;
                }
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream stream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(stream);
                    if (bitmap != null) {
                        mLruCache.put(path, bitmap);
                        FileOutputStream fos = new FileOutputStream(new File(cacheFileDir, MD5Encoder.encode(path)));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        stream.close();
                        fos.close();
                        post(listener, path, 10);
                    }else{
                        postFailed(listener);
                    }

                }else{
                    postFailed(listener);
                }
            } catch (Exception e) {
                postFailed(listener);
            }
        }


    }
    private void postFailed(final OnBitmapLoadSuccessListener listener) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onFailed();
                }
            }
        },200);
    }
    private void post(final OnBitmapLoadSuccessListener listener, final String path, int timeDelay) {
        L.d("startNotification","5555555555");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    L.d("startNotification","666666666666");
                    listener.onSuccess(bitmap, path);
                }
            }
        },timeDelay);
    }

}
