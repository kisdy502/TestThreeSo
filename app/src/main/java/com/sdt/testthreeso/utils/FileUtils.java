package com.sdt.testthreeso.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * @ClassName FileUtils
 * @Description 获取各种目录
 * @Author Administrator
 * @Date 2021/3/30 17:19
 * @Version 1.0
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    public static void printAllDirectory(Context context) {

        File filesDir = context.getExternalFilesDir("");    // /data/data/包名/files
        File cacheDir = context.getExternalCacheDir();    // /data/data/包名/cache
        showFleInfo(filesDir);
        showFleInfo(cacheDir);

        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdcard = Environment.getExternalStorageDirectory();
            File sdcardDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File sdcardMovies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            File sdcardMusic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File sdcardDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            showFleInfo(sdcard);
            showFleInfo(sdcardDcim);
            showFleInfo(sdcardMovies);
            showFleInfo(sdcardMusic);
            showFleInfo(sdcardDownloads);

        }
    }

    private static void showFleInfo(File dir) {
        Log.d(TAG, "dir:" + dir.getAbsolutePath() + "," + dir.canRead() + "," + dir.canWrite());
    }
}
