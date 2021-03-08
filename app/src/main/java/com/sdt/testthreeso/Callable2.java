package com.sdt.testthreeso;

import android.util.Log;

import java.util.concurrent.Callable;

public class Callable2 implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        Thread.sleep(6000);
        Log.d("Callable2","Name:"+Thread.currentThread().getId());
        return 66;
    }
}
