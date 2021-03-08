package com.sdt.testthreeso.net;


import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * LiveRetrofit对象创建消耗资源较多，全局尽量创建一个
 * 加入每个项目独有的公共头,这里大部分和业务逻辑相关，不同项目使用都要修改这个拦截器，或者不需要这个拦截器
 */
public class ChangeBaseUrlInterceptor implements Interceptor {
    private final static String TAG = "ChangeBaseUrl";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        HttpUrl oldHttpUrl = request.url();                              //从request中获取原有的HttpUrl实例oldHttpUrl
//        Request.Builder builder = request.newBuilder();                  //获取request的创建者builder
//        List<String> headerValues = request.headers(HttpConstants.BASEURL);     //获取headers,url_name
//        if (headerValues != null && headerValues.size() > 0) {
//            builder.removeHeader(HttpConstants.BASEURL);
//            String headerValue = headerValues.get(0);            //判断这个是哪一个方法需要使用什么baseUrl
//            HttpUrl newBaseUrl;
//            if (HttpConstants.BASEURL_COMMON.equals(headerValue)) {
//                newBaseUrl = HttpUrl.parse(HttpConstants.LIVE_HOST);
//            } else if (HttpConstants.BASEURL_USER.equals(headerValue)) {
//                newBaseUrl = HttpUrl.parse(HttpConstants.liveUserHost);
//            } else if (HttpConstants.BASEURL_IP.equals(headerValue)) {
//                newBaseUrl = HttpUrl.parse(HttpConstants.liveIp);
//            } else if (HttpConstants.BASEURL_CDN.equals(headerValue)) {
//                newBaseUrl = HttpUrl.parse(HttpConstants.HOST_CDN_IP);
//            } else if (HttpConstants.BASEURL_PAY.equals(headerValue)) {
//                newBaseUrl = HttpUrl.parse(HttpConstants.livePay);
//            } else if (HttpConstants.BASEURL_PCONLINE.equals(headerValue)) {
//                newBaseUrl = HttpUrl.parse(HttpConstants.PCONLINE_IP);
//            } else {
//                newBaseUrl = HttpUrl.parse(HttpConstants.liveHost);
//            }
//            //重建新的HttpUrl，修改需要修改的url部分
//            HttpUrl newFullUrl = oldHttpUrl
//                    .newBuilder()
//                    .scheme(newBaseUrl.scheme())
//                    .host(newBaseUrl.host())
//                    .port(newBaseUrl.port())
//                    .build();
//            if (BuildConfig.DEBUG || PrefManager.getShowDebugMode(App.getInstance())) {
//                VLog.d(TAG, "real url:" + newFullUrl.toString());
//            }
//            Request realRequest = builder.url(newFullUrl)
//                    //.addHeader("Accept-Encoding", "gzip,deflate,sdch")
//                    .addHeader("softwareChannel", InternalUtils.obtainChannel(App.getInstance()))
//                    .addHeader("pkgName", BuildConfig.APPLICATION_ID)
//                    .addHeader("version", BuildConfig.VERSION_NAME)
//                    .addHeader("X-Kds-channel", InternalUtils.obtainChannel(App.getInstance()))
//                    .addHeader("X-Kds-Ver", BuildConfig.VERSION_NAME)
//                    .build();
//            return chain.proceed(realRequest);
//        }
        return chain.proceed(request);
    }
}
