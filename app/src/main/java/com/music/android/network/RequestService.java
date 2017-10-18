package com.music.android.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.music.android.network.api.MyServerApi;
import com.music.android.network.api.SoundCloudApi;
import com.music.android.utils.AesUtils;
import com.music.android.utils.L;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuyun on 17/3/2.
 */

public class RequestService {

    private final static int CONNECT_TIME_OUT = 30 * 1000;

    private static Retrofit.Builder mBuilder = null;

    private static OkHttpClient mClient = null;

    private final static Gson gson = new GsonBuilder().setLenient().create();

    static {

        mBuilder = new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(UrlConst.BASE_URL);

        mClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl.Builder urlBuilder = request.url().newBuilder();
                Request newRequest = request.newBuilder()
                        .url(urlBuilder.build())
                        .build();
                Response response = chain.proceed(newRequest);

                String body = response.body().string();

                L.d("StyleFragment", " RequestService " + body);

                L.e("StyleFragment", " RequestService toString " + AesUtils.getDecryptBody(body));

                return response.newBuilder().body(ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), AesUtils.getDecryptBody(body))).build();
            }
        }).connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS).build();
    }

    private RequestService() {

    }

    public static MyServerApi createRequestService() {
        return mBuilder.client(mClient).build().create(MyServerApi.class);
    }

}
