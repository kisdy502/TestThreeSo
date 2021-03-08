package com.sdt.testthreeso;

import android.util.Log;

import java.util.concurrent.Callable;

public class Callable1 implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        Thread.sleep(3000);
        Log.d("Callable1","Name:"+Thread.currentThread().getId());
        return 33;
    }
}
