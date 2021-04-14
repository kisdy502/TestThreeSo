package com.sdt.testthreeso;

import androidx.annotation.MainThread;

/**
 * @ClassName LoadCallback
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/22 10:06
 * @Version 1.0
 */
public interface LoadCallback<T> {
    @MainThread
    public void onLoadSuccess(T t);

    @MainThread
    public void onLoadFailed(Throwable e);
}
