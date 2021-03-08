package com.sdt.testthreeso.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Dns;

public class HttpDNS implements Dns {

    private final static String TAG = "HttpDNS";

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        //Log.i(TAG, "hostname:" + hostname);
        return Dns.SYSTEM.lookup(hostname);             //没有接入httpdns，这里先注释代码

        /*String ip = DNSHelper.getIpByHost(hostname);
        if (TextUtils.isEmpty(ip)) {
            // 解析失败，使用系统解析
            return Dns.SYSTEM.lookup(hostname);

        } else {
            //返回自己解析的地址列表
            InetAddress[] inetAddresses = InetAddress.getAllByName(ip);
            if (inetAddresses == null || inetAddresses.length == 0) {
                // 解析失败，使用系统解析
                return Dns.SYSTEM.lookup(hostname);
            } else {
                List<InetAddress> addressList = Arrays.asList(InetAddress.getAllByName(ip));
                Log.i(TAG, "inetAddresses:" + inetAddresses);
                return addressList;
            }
        }*/
    }
}
