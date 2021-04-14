package com.sdt.testthreeso.net;


import com.sdt.testthreeso.App;
import com.sdt.testthreeso.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class LiveRetrofit {

    private static final int DEFAULT_TIMEOUT = 20;

    private Retrofit retrofit;

    private LiveApi mApi;

    public static LiveRetrofit getInstance() {
        return Holder.retrofit;
    }

    private LiveRetrofit() {
        File dir = App.getInstance().getExternalCacheDir();
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.addInterceptor(new ChangeBaseUrlInterceptor());
        //日志拦截影响性能，线上保持关闭
        if (BuildConfig.DEBUG || true) {
            mBuilder.addInterceptor(new LoggingInterceptor
                    .Builder()//构建者模式
                    .loggable(true) //是否开启日志打印
                    .setLevel(Level.BASIC) //打印的等级
                    .log(Platform.INFO) // 打印类型
                    .request("Request") // request的Tag
                    .response("Response")// Response的Tag
                    .addHeader("debug", "log")      // 添加打印头, 注意 key 和 value 都不能是中文
                    .build()
            );
        }
        mBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response realResponse = chain.proceed(chain.request());
                if (chain.request().method().equals("GET")) {
                    return realResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=" + 300)
                            .build();
                } else {
                    return realResponse;
                }
            }
        }).cache(new Cache(dir, 1024 * 1024 * 3))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .dns(new HttpDNS())                                     //直播DNS解析首次耗时5秒，这里适当优化
                .proxy(Proxy.NO_PROXY)                                  //防止使用代理的工具抓包
                .eventListenerFactory(OkHttpEventListener.FACTORY);     //监听请求，可以用来统计请求DNS耗时，成功率

        OkHttpClient client = mBuilder.build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(JsonOrStringConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(HttpConstants.LIVE_HOST)
                .build();

        mApi = retrofit.create(LiveApi.class);
    }

    public LiveApi getApi() {
        return mApi;
    }

    private final static class Holder {
        private static LiveRetrofit retrofit = new LiveRetrofit();
    }

    public static void reCreate() {
        Holder.retrofit = null;
        Holder.retrofit = new LiveRetrofit();
    }
}
