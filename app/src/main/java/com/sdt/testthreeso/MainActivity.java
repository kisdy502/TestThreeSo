package com.sdt.testthreeso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.sdt.testthreeso.bean.BaseChannelsResultData;
import com.sdt.testthreeso.bean.Channel;
import com.sdt.testthreeso.bean.ChannelLiveSource;
import com.sdt.testthreeso.bean.LiveSource;
import com.sdt.testthreeso.bean.SourceDetailResultData;
import com.sdt.testthreeso.net.LiveRetrofit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private List<Channel> channelList = new ArrayList<>();
    private int playIndex = 0;
    private int sourceIndex = 0;
    private Channel playingChannel = null;
    private DataChangedReceiver receiver = new DataChangedReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_t1).setOnClickListener(this);
        findViewById(R.id.btn_t2).setOnClickListener(this);
        findViewById(R.id.btn_t3).setOnClickListener(this);
        findViewById(R.id.btn_t4).setOnClickListener(this);
        findViewById(R.id.btn_t5).setOnClickListener(this);
        findViewById(R.id.btn_t6).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_kdm).setOnClickListener(this);

        findViewById(R.id.btn_calc1).setOnClickListener(this);
        findViewById(R.id.btn_calc2).setOnClickListener(this);
        findViewById(R.id.btn_calc3).setOnClickListener(this);
        findViewById(R.id.btn_calc4).setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.BODY_SENSORS,
                    Manifest.permission.READ_CALL_LOG,
            }, 0);
        }

        IntentFilter filter = new IntentFilter("com.sdt.Intent.CHANGE_NEXT");
        registerReceiver(receiver, filter);
        init();
        loadBaseData();
    }

    private ThreadPoolExecutor threadPoolExecutor;
    private ExecutorService singleExecutor;         //可以让线程串行
    private ExecutorService onExecutor;             //可以让线程串行

    private void init() {
        int poolSize = Runtime.getRuntime().availableProcessors();
        threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize * 2,
                10L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


        singleExecutor = Executors.newSingleThreadExecutor();
        onExecutor = Executors.newFixedThreadPool(1);
    }


    private void loadBaseData() {
        LiveRetrofit.getInstance().getApi().getBaseData("1", "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BaseChannelsResultData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseChannelsResultData baseChannelsResultData) {
                channelList = baseChannelsResultData.getChannelList();
                Log.d(TAG, "channelList:" + channelList.size());
                playingChannel = channelList.get(playIndex);
                getChannelDetail();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError", e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void getChannelDetail() {
        showDialog();
        LiveRetrofit.getInstance().getApi().getChannelDetail(playingChannel.getId(), "1", "440300", "8")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SourceDetailResultData>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SourceDetailResultData sourceDetailResultData) {
                progressDialog.dismiss();
                ChannelLiveSource channelLiveSource = sourceDetailResultData.getLiveSource();
                if (channelLiveSource.getSourceList().isEmpty()) {
                    Log.d(TAG, "no source list" + playingChannel.getName());
                    playIndex++;
                    playingChannel = channelList.get(playIndex);
                    getChannelDetail();
                } else {
                    List<LiveSource> sourceList = channelLiveSource.getSourceList();
                    for (LiveSource liveSource : sourceList) {
                        Log.d(TAG, "liveSource:" + liveSource.getUrlType());
                        Log.d(TAG, "liveSource:" + liveSource.getUrl());
                    }
                    playingChannel.setPlaySourceList(sourceList);
                    sourceIndex = 0;
                    startPlay();
                }
            }

            @Override
            public void onError(Throwable e) {
                progressDialog.dismiss();
                Log.e(TAG, "onError", e);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    private void startPlay() {
        if (playingChannel.getPlaySourceList() != null && sourceIndex < playingChannel.getPlaySourceList().size() - 1) {
            LiveSource liveSource = playingChannel.getPlaySourceList().get(sourceIndex);
            if (liveSource.getUrlType() > 111 && liveSource.getUrlType() <= 1000) {
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra("playUrl", liveSource.getUrl());
                Log.d(TAG, "channel:" + playingChannel.getId());
                Log.d(TAG, "channel:" + playingChannel.getName());
                Log.d(TAG, "channelIndex:" + playIndex);
                Log.d(TAG, "sourceIndex:" + sourceIndex);
                startActivity(intent);
            } else {
                sourceIndex++;
                startPlay();
            }
        } else {
            playIndex++;
            playingChannel = channelList.get(playIndex);
            getChannelDetail();
        }
    }


    private ProgressDialog progressDialog;

    private void showDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        // 设置进度条风格，风格为圆形，旋转的
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 标题
        progressDialog.setTitle("提示");
        // 设置ProgressDialog 提示信息
        progressDialog.setMessage("这是一个圆形进度条对话框");
        // 设置ProgressDialog 标题图标
        progressDialog.setIcon(R.mipmap.ic_launcher);
        // 设置ProgressDialog 的进度条是否不明确
        progressDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(true);
        // 让ProgressDialog显示
        progressDialog.show();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_t1) {
            onExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ValuesGen.genAllXml(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (id == R.id.btn_t2) {

        } else if (id == R.id.btn_t3) {
            startActivity(new Intent(MainActivity.this, PlayerActivity.class));
        } else if (id == R.id.btn_t4) {
        } else if (id == R.id.btn_t5) {
            String data = "admin";
            FunCaller.encryptString(data);
        } else if (id == R.id.btn_t6) {
            String data = "aabbccddAABBCCDDxxyyzzXXYYZZ";
            FunCaller.encryptBytes(data.getBytes(Charset.forName("UTF-8")), data.length());
        } else if (id == R.id.btn_register) {
            File txtFile = new File(getApplication().getExternalFilesDir("test"), "test.txt");
            FunCaller.writeFile(txtFile.getAbsolutePath(), "android ndk devlopment");
        } else if (id == R.id.btn_kdm) {
            File txtFile = new File(getApplication().getExternalFilesDir("test"), "test.txt");
            String text = FunCaller.readText(txtFile.getAbsolutePath());
            Log.d(TAG, "read text is:" + text);
        } else if (id == R.id.btn_calc1) {
            short data = 5;
            short d = FunCaller.calcBit(data);
            Log.d(TAG, "calcBit result:" + d);
        } else if (id == R.id.btn_calc2) {
            long d = FunCaller.calcMumber(6000000L);
            Log.d(TAG, "calcMumber result:" + d);
        } else if (id == R.id.btn_calc3) {
            double d = FunCaller.calcDistance(87887.55D);
            Log.d(TAG, "calcDistance result:" + d);
        } else if (id == R.id.btn_calc4) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class DataChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action + ":" + sourceIndex);
            ++sourceIndex;
            startPlay();
        }
    }
}

