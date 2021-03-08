package com.sdt.testthreeso.net;

import android.util.Log;


import androidx.annotation.Nullable;

import com.sdt.testthreeso.BuildConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络优化
 * DNS耗时统计，成功率统计，前台后台请求统计
 */
public class OkHttpEventListener extends EventListener {
    private final static String TAG = "HttpEventListener";

    private OkHttpEvent okHttpEvent = new OkHttpEvent();

    @Override
    public void callStart(Call call) {
        //Log.i(TAG, "callStart:"+call.request().url().toString());
        super.callStart(call);
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
        okHttpEvent.dnsStartTime = System.currentTimeMillis();
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        okHttpEvent.dnsEndTime = System.currentTimeMillis();
        if (BuildConfig.DEBUG) {
            //LogWriter.writeText(App.getInstance(), "DNS解析耗时(ms):" + (okHttpEvent.dnsEndTime - okHttpEvent.dnsStartTime));
        }
        Log.d(TAG, "DNS解析耗时(ms):" + (okHttpEvent.dnsEndTime - okHttpEvent.dnsStartTime));
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
    }

    @Override
    public void secureConnectStart(Call call) {
        super.secureConnectStart(call);
    }

    @Override
    public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
        super.secureConnectEnd(call, handshake);
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol, IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        super.connectionAcquired(call, connection);
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        super.connectionReleased(call, connection);
    }

    @Override
    public void requestHeadersStart(Call call) {
        super.requestHeadersStart(call);
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        super.requestHeadersEnd(call, request);
    }

    @Override
    public void requestBodyStart(Call call) {
        super.requestBodyStart(call);
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        okHttpEvent.responseSize = byteCount;
        Log.i(TAG, "response size:" + (okHttpEvent.responseSize));

    }

    @Override
    public void responseHeadersStart(Call call) {
        super.responseHeadersStart(call);
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        super.responseHeadersEnd(call, response);
    }

    @Override
    public void responseBodyStart(Call call) {
        super.responseBodyStart(call);
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
        okHttpEvent.requestSuccess = true;
        Log.d(TAG, "response success coust time ts:" + (okHttpEvent.dnsEndTime - okHttpEvent.dnsStartTime));
        if (BuildConfig.DEBUG) {
            //LogWriter.writeText(App.getInstance(), "response success");
        }
    }

    @Override
    public void callFailed(Call call, IOException e) {
        super.callFailed(call, e);
        okHttpEvent.requestSuccess = false;
        okHttpEvent.errorStack = Log.getStackTraceString(e);
        Log.e(TAG, "response failed:" + call.request().url() + e.getMessage());
        if (BuildConfig.DEBUG) {
            //LogWriter.writeText(App.getInstance(), "response faield" + call.request().url() + ioe.getMessage());
        }
    }

    public static Factory FACTORY = new Factory() {
        @Override
        public EventListener create(Call call) {
            return new OkHttpEventListener();
        }
    };
}
