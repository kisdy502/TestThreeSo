package com.sdt.testthreeso;

import android.app.Application;

/**
 * @ClassName App
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/4 18:38
 * @Version 1.0
 */
public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
