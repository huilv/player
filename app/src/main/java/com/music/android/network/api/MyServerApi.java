package com.music.android.network.api;


import com.music.android.bean.ClientIdBean;
import com.music.android.bean.HotListBean;
import com.music.android.bean.IndexInfoBean;
import com.music.android.bean.RankInfoBean;
import com.music.android.bean.StyleInfoBean;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by liuyun on 17/3/20.
 */

public interface MyServerApi {

    @GET("v3/home/index")
    Flowable<IndexInfoBean> getIndex();

    @GET("v2/hot/list")
    Flowable<HotListBean> getHotList();

    @GET("v2/hot/list")
    Call<String> getConfig(@Body RequestBody body);

    @GET("v2/rank/index")
    Flowable<RankInfoBean> getRankIndex();

    @POST("v2/rank/list")
    Flowable<HotListBean> getRankList(@Body RequestBody body);

    @GET("v2/category/index")
    Flowable<StyleInfoBean> getStyleIndex();

    @POST("v2/category/list")
    Flowable<HotListBean> getStyleList(@Body RequestBody body);

    @GET("v2/env/client/info")
    Flowable<ClientIdBean> getClientId(@Query("client_id") String client_id);

    @GET("v2/audio/index")
    Flowable<StyleInfoBean> getAudioList();

    @POST("v2/audio/list")
    Flowable<HotListBean> getAudioList(@Body RequestBody body);

}
