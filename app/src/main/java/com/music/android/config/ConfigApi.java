package com.music.android.config;

import com.music.android.bean.ConfigData;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by CTer on 17/4/17.
 */

public interface ConfigApi {

    // pubid=xxx&moduleid=xxx&pkg_id=xxx&pkg_ver=xxx&deviceid=xxx&file_ver=xxx&imei=xxx&android_id=xxx&os=xxx&func=xxx

    @GET("p/config")
    Flowable<ConfigData> getAppConfig(@Query("pubid") String pubid,
                                      @Query("moduleid") String moduleid,
                                      @Query("pkg_name") String pkg_name,
                                      @Query("pkg_ver") String pkg_ver,
                                      @Query("file_ver") String file_ver,
                                      @Query("os") String os,
                                      @Query("func") String func);

}
