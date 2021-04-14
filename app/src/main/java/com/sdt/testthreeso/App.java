package com.sdt.testthreeso;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.sdt.testthreeso.crash.JCrash;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import mustang.with.With;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @ClassName App
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/4 18:38
 * @Version 1.0
 */
public class App extends Application {
    private final static String TAG = "App";
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        buildDataSourceFactory();
        buildRenderersFactory();
        JCrash.init(this, null, getPackageName(), BuildConfig.VERSION_NAME);
        Log.d(TAG,"d:"+With.d);
        Log.d(TAG,"c:"+With.c);
        Log.d(TAG,"b:"+With.b);
        With.start(App.getInstance());
        Log.d(TAG,"d:"+With.d);
        Log.d(TAG,"c:"+With.c);
        Log.d(TAG,"b:"+With.b);
    }

    public void initWork() {
        WorkManager mWorkManager = WorkManager.getInstance(instance);
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(LogSaveWorker.class, 15, TimeUnit.MINUTES).build();
        mWorkManager.enqueue(request);


    }

    public void initWorkWithIntent() {
        WorkManager mWorkManager = WorkManager.getInstance(instance);
        PeriodicWorkRequest blurRequest =
                new PeriodicWorkRequest.Builder(LogSaveWorker.class, 15, TimeUnit.MINUTES)
                        .setInputData(createInputDataForUri(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")))
                        .build();
        mWorkManager.enqueue(blurRequest);
    }

    private Data createInputDataForUri(Uri mImageUri) {
        Data.Builder builder = new Data.Builder();
        if (mImageUri != null) {
            builder.putString("KEY_IMAGE_URI", mImageUri.toString());
        }
        return builder.build();
    }

    private DataSource.Factory dataSourceFactory;

    public DataSource.Factory getDataSourceFactory() {
        return dataSourceFactory;
    }

    private void buildDataSourceFactory() {
        if (dataSourceFactory != null) {
            return;
        }
//        DefaultDataSourceFactory upstreamFactory =
//                new DefaultDataSourceFactory(getApplicationContext(), new DefaultHttpDataSource.Factory());
//        dataSourceFactory = new DefaultDataSourceFactory(
//                getApplicationContext(),
//                upstreamFactory);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()                //可以拦截到所有的图片请求链接，下载耗时 ,okhttp3替代老的项目
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8,TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String url = chain.request().url().toString();
                        Response response = chain.proceed(chain.request());
                        Log.d(TAG, "playUrl:" + url + " ,code:" + response.code());
                        return response;
                    }
                }).build();

        dataSourceFactory = new OkHttpDataSource.Factory(okHttpClient);  //优化，利用OkHttp3链路复用，多线程控制，提升http请求成功率


    }


    private DefaultRenderersFactory renderersFactory;

    public DefaultRenderersFactory getRenderersFactory() {
        return renderersFactory;
    }

    private void buildRenderersFactory() {
        if (renderersFactory != null) {
            return;
        }
        renderersFactory = new DefaultRenderersFactory(getApplicationContext());
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
    }

}
