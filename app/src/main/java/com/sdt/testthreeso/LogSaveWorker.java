package com.sdt.testthreeso;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sdt.testthreeso.utils.LogWriter;

/**
 * @ClassName BlurWorker
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/9 9:37
 * @Version 1.0
 */
public class LogSaveWorker extends Worker {

    private static final String TAG = LogSaveWorker.class.getSimpleName();

    public LogSaveWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "LogSaveWorker doWork...");
        String resourceUri = getInputData().getString("KEY_IMAGE_URI");
        Log.d(TAG, "resourceUri:" + resourceUri);
        LogWriter.writeText(getApplicationContext(), "exec work:" + System.currentTimeMillis());
        Data outputData = new Data.Builder()
                .putString("KEY_IMAGE_URI", resourceUri)
                .build();
        return Result.success(outputData);
    }


}
