package com.music.android.managers;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.music.android.MusicApp;
import com.music.android.R;
import com.music.android.listener.OnBitmapListener;
import com.music.android.listener.OnBitmapLoadListener;


/**
 * Created by yun.liu@avazu.net on 2016/5/20.
 */
public class ImageLoaderManager {

    public static void imageLoader(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url) && imageView != null) {
            Glide.with(MusicApp.context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
    }

    public static void imageLoader(ImageView imageView, int defaultImage, String url) {
        if (url != null) {
            Glide.with(MusicApp.context).load(url).placeholder(defaultImage).error(defaultImage).crossFade().into(imageView);
        }
    }

    public static void imageLoader(ImageView imageView, int defaultImage, Uri uri) {
        Glide.with(MusicApp.context).load(uri).placeholder(defaultImage).error(defaultImage).into(imageView);
    }

    public static void imageLoader(ImageView imageView, int drawable) {
        if (imageView != null) {
            try {
                Glide.with(MusicApp.context).load(drawable).diskCacheStrategy(DiskCacheStrategy.ALL).error(drawable).into(imageView);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

    }

    public static void imageLoaderDefault(ImageView imageView, int defaultImage, String url) {
        Glide.with(MusicApp.context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImage).error(defaultImage).into(imageView);
    }

    public static void imageLoaderCircleBitmap(ImageView imageView, int defaultImage, String url) {
        Glide.with(MusicApp.context).
                load(url).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                bitmapTransform(new CropCircleTransformation(imageView.getContext())).
                placeholder(defaultImage).
                error(defaultImage).
                crossFade(1000).
                into(imageView);
    }


    public static void imageLoaderBitMap(Uri uri, final OnBitmapLoadListener listener) {
        Glide.with(MusicApp.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        if (listener != null) {
                            listener.onResourceReady(resource);
                        }
                    }
                });
    }

    public static void imageLoaderBitMap(String url, final OnBitmapListener listener) {
        Glide.with(MusicApp.context)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (listener != null) {
                            listener.onResourceReady(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                    }
                });
    }


    public static void imageLoaderBitMap(Uri uri, final OnBitmapListener listener) {
        Glide.with(MusicApp.context)
                .load(uri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (listener != null) {
                            listener.onResourceReady(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                    }
                });
    }





    public static void imageLoaderBitMap(String url, final OnBitmapLoadListener listener) {
        Glide.with(MusicApp.context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        if (listener != null) {
                            listener.onResourceReady(resource);
                        }
                    }
                });
    }




    public static void imageLoaderAssents(ImageView gifImageView, String fileName) {
        if (fileName != null) {
            String url = "file:///android_asset/" + fileName;
            Glide.with(MusicApp.context).load(url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(gifImageView);
        }
    }

}
