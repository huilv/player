package com.music.android.network.api;

import com.music.android.bean.MusicInfoBean;

import java.util.List;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by liuyun on 17/3/2.
 */

public interface SoundCloudApi {

    @GET("tracks")
    Flowable<List<MusicInfoBean>> getMusicInfoByKeyWord(@Query("client_id") String client_id,@Query("q") String q, @Query("offset") String offset);

    @GET("tracks/{id}")
    Flowable<MusicInfoBean> getMusicInfoById(@Path("id") String id, @Query("q") String q);

    @Streaming
    @GET
    Flowable<ResponseBody> downloadMusic(@Url String musicUrl);
}
