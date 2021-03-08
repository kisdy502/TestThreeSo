package com.sdt.testthreeso.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SDT13411 on 2018/1/5.
 */

public class LogWriter {

    private static final String TAG = "LogWriter";

    public final static String DIR_NAME = "log";

    public static void clearLog(Context context) {
        File normalDir = createLogWriteDir(context, DIR_NAME);
        deleteDir(normalDir, true);
    }

    public static void writeText(Context context, String text) {
        File logDir = createLogWriteDir(context, DIR_NAME);
        String logFileName = convertTimestamp2Date(System.currentTimeMillis(), pattern).concat(".txt");
        File writeFile = new File(logDir, logFileName);
        if (!writeFile.getParentFile().canWrite()) {
            return;
        }
        writeData(writeFile, text);
    }

    /**
     * 日志存储路径
     *
     * @param context
     * @return
     */
    public static File createLogWriteDir(Context context, String fileName) {
        File dir = context.getExternalFilesDir(fileName);
        return dir;
    }


    public static void writeData(File logFile, String data) {
        boolean created = true;
        if (!logFile.exists()) {
            try {
                created = logFile.createNewFile();
                if (created) {
                    logFile.setReadable(true, true);
                    logFile.setWritable(true, true);    //可读可写
                }
            } catch (IOException e) {
                e.printStackTrace();
                created = false;
                return;
            }
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(logFile, true);  //向日志追加内容
            writer.write(data);
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != writer) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteDir(File dir, boolean delRoot) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null && children.length > 0) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i].isFile()) {
                        deleteFile(children[i]);
                    } else if (children[i].isDirectory()) {
                        deleteDir(children[i], true);
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        if (delRoot) {
            boolean success = dir.delete();
            Log.i(TAG, "delete " + dir.getAbsolutePath() + ",result:" + success);
            return success;
        }
        return true;
    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private static String pattern = "yyyy-MM-dd";

    private static String convertTimestamp2Date(Long timestamp, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(timestamp));
    }
}
