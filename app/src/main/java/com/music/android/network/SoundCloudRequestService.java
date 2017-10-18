package com.music.android.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.music.android.managers.Constant;
import com.music.android.network.api.SoundCloudApi;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuyun on 17/3/2.
 */

public class SoundCloudRequestService {

    private final static int CONNECT_TIME_OUT = 30 * 1000;

    private static Retrofit.Builder mBuilder = null;

    private static OkHttpClient mClient = null;

    private final static Gson gson = new GsonBuilder().setLenient().create();

    static {

        mBuilder = new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(UrlConst.SOUND_CLOUD_BASE_URL);

        mClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl.Builder urlBuilder = request.url().newBuilder();
//                urlBuilder.addEncodedQueryParameter(Constant.PropertyConstant.CLIENT_ID, UrlConst.CLIENT_ID);
                urlBuilder.addEncodedQueryParameter(Constant.PropertyConstant.LIMIT, UrlConst.LIMIT);
                Request newRequest = request.newBuilder()
                        .url(urlBuilder.build())
                        .build();
                return chain.proceed(newRequest);
            }
        }).connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS).build();
    }

    private SoundCloudRequestService() {

    }

    public static SoundCloudApi createRequestService() {
        return mBuilder.client(mClient).build().create(SoundCloudApi.class);
    }

}
