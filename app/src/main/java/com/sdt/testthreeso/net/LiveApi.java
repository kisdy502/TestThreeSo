package com.sdt.testthreeso.net;


import com.sdt.testthreeso.bean.BaseChannelsResultData;
import com.sdt.testthreeso.bean.SourceDetailResultData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface LiveApi {


    @Headers({"Content-Encoding:gzip"})
    @GET("/liveApi/api/v2/listClassAndChannelv2.action")
    @ResponseFormat(ResponseFormat.JSON)
    Observable<BaseChannelsResultData> getBaseData(@Query("hideCateSwitch") String hideCateSwitch,
                                                   @Query(value = "version", encoded = true) String version);

    @GET("/liveApi/api/v2/channelDetail.action")
    @ResponseFormat(ResponseFormat.JSON)
    Observable<SourceDetailResultData> getChannelDetail(@Query("channelId") String channelId,
                                                        @Query("egcode") String egcode,
                                                        @Query("regionId") String regionId,
                                                        @Query("custom2") String custom2);

}
















