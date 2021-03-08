package com.sdt.testthreeso;

import android.util.Log;

import java.util.concurrent.Callable;

public class Callable3 implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        Thread.sleep(9000);
        Log.d("Callable3", "Name:" + Thread.currentThread().getId());
        return 99;
    }
}
