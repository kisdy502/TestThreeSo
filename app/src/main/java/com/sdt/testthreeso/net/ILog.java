package com.sdt.testthreeso.net;


import android.text.TextUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.internal.platform.Platform;

/**
 * 日志输出
 */
class ILog {
    final static int segmentSize = 2 * 1024;

    protected ILog() {
        throw new UnsupportedOperationException();
    }

    static void log(int type, String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(tag);
        switch (type) {
            case Platform.INFO:
                logSuperString(logger, msg, Platform.INFO);
                break;
            default:
                logSuperString(logger, msg, Platform.WARN);
                break;
        }
    }

    /**
     * 超长日志完整打印
     *
     * @param logger
     * @param msg
     * @param type
     */
    public static void logSuperString(java.util.logging.Logger logger, String msg, int type) {
        if (msg == null || msg.length() == 0)
            return;
        while (msg.length() > segmentSize) {// 循环分段打印日志
            String logContent = msg.substring(0, segmentSize);
            realLog(type, logger, logContent);
            msg = msg.replace(logContent, "");
        }
        realLog(type, logger, msg);

    }

    private static void realLog(int type, Logger logger, String msg) {
        if (msg == null || msg.length() == 0)
            return;
        switch (type) {
            case Platform.INFO:
                logger.log(Level.INFO, msg);
                break;
            default:
                logger.log(Level.WARNING, msg);
                break;
        }
    }
}


