package com.sdt.testthreeso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary;
import com.sdt.testthreeso.bean.BaseChannelsResultData;
import com.sdt.testthreeso.net.LiveRetrofit;
import com.sdt.testthreeso.repository.ChannelRepository;
import com.sdt.testthreeso.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import a.a.d;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mustang.with.With;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
//    private DataChangedReceiver receiver = new DataChangedReceiver();

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
            }, 0);
        }

//        IntentFilter filter1 = new IntentFilter("com.sdt.Intent.CHANGE_NEXT_CATEGORY");
//        IntentFilter filter2 = new IntentFilter("com.sdt.Intent.CHANGE_NEXT_CHANNEL");
//        IntentFilter filter3 = new IntentFilter("com.sdt.Intent.CHANGE_NEXT_SOURCE");
//        IntentFilter filter4 = new IntentFilter("com.sdt.Intent.CHANGE_TO_TARGET_CHANNEL");
//        registerReceiver(receiver, filter1);
//        registerReceiver(receiver, filter2);
//        registerReceiver(receiver, filter3);
//        registerReceiver(receiver, filter4);
        init();
        loadBaseData();
        //App.getInstance().initWorkWithIntent();
        showProgressDialog();
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
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<BaseChannelsResultData, Integer>() {
                    @Override
                    public Integer apply(BaseChannelsResultData baseChannelsResultData) throws Exception {
                        new ChannelRepository().handleChannelResult(baseChannelsResultData);
                        return 1;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer result) {
                        dismissProgressDialog();
                        Intent intent = new Intent(MainActivity.this, LivePlayActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


//    private void getChannelDetail() {
//        showProgressDialog();
//        LiveRetrofit.getInstance().getApi().getChannelDetail(playingChannel.getChannelId(), "1", "440300", "8")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<SourceDetailResultData>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(SourceDetailResultData sourceDetailResultData) {
//                        handleDetail(sourceDetailResultData);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //progressDialog.dismiss();
//                        Log.e(TAG, "onError", e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

//    private void handleDetail(SourceDetailResultData sourceDetail) {
//        ChannelLiveSource channelLiveSource = sourceDetail.getLiveSource();
//        if (channelLiveSource != null
//                && channelLiveSource.getSourceList() != null
//                && !channelLiveSource.getSourceList().isEmpty()) {
//            List<LiveSource> sourceList = channelLiveSource.getSourceList();
//            for (LiveSource liveSource : sourceList) {
//                Log.d(TAG, "liveSource:" + liveSource.getUrlType());
//                Log.d(TAG, "liveSource:" + liveSource.getUrl());
//            }
//            playingChannel.setPlaySourceList(sourceList);
//            sourceIndex = 0;
//            startPlay();
//        } else {
//            Log.d(TAG, "no source list" + playingChannel.getName());
//            if (channelIndex == channelList.size() - 1) {
//                changeCategory();  //播放到最后一个了
//            } else {
//                channelIndex++;
//                playingChannel = channelList.get(channelIndex);
//                getChannelDetail();
//            }
//
//        }
//    }


//    private void startPlay() {
//        if (playingChannel.getPlaySourceList() != null && sourceIndex < playingChannel.getPlaySourceList().size() - 1) {
//            LiveSource liveSource = playingChannel.getPlaySourceList().get(sourceIndex);
//            if (liveSource.getUrlType() > 111 && liveSource.getUrlType() <= 1000) {
//                dismissProgressDialog();
//                Intent intent = new Intent(this, LivePlayActivity.class);
//                intent.putExtra("playUrl", liveSource.getUrl());
//                intent.putExtra("channelName", playingChannel.getName());
//                intent.putExtra("channelId", playingChannel.getChannelId());
//                intent.putExtra("categoryIndex", categoryIndex);
//                intent.putExtra("channelIndex", channelIndex);
//                intent.putExtra("sourceIndex", sourceIndex);
//                startActivity(intent);
//            } else {
//                sourceIndex++;
//                startPlay();
//            }
//        } else {
//            channelIndex++;
//            playingChannel = channelList.get(channelIndex);
//            getChannelDetail();
//        }
//    }


    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
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

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
            FileUtils.printAllDirectory(getApplicationContext());
        } else if (id == R.id.btn_t3) {
            Intent intent = new Intent(this, LivePlayActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_t4) {
            String ffmpegVersion = FfmpegLibrary.getVersion();
            Log.d(TAG, "ffmpeg so version:" + ffmpegVersion);
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

            try {
                Log.d(TAG, "b:" + With.b.toJSONString());
                Log.d(TAG, "c" + With.c);
                Log.d(TAG, "d:" + With.d);
//                With.a();
//                List<a.c.a> list = new ArrayList<>();
//                Runnable r = new a.c.b(list);
//                new Thread(r).start();
//
//                With.b("请求监测地址错误,code=,url=");
                onExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String text = a.a.a.a(With.b.toJSONString());
                        Log.d(TAG, "text:" + text);
                        d<String, Integer> dd = a.a.c.a().b("http://ad-c2s.fengmanginfo.com/v1/c", a.a.a.a(With.b.toJSONString()));
                        Log.d(TAG, "dd:" + dd.a);
                        Log.d(TAG, "dd:" + dd.b);
                        Log.d(TAG, "解密response:" + With.a(dd.a).toJSONString());

                        With.a(new IOException("test"));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    private class DataChangedReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equalsIgnoreCase("com.sdt.Intent.CHANGE_NEXT_CATEGORY")) {
//                changeCategory();
//            } else if (action.equalsIgnoreCase("com.sdt.Intent.CHANGE_NEXT_CHANNEL")) {
//                if (channelIndex == channelList.size() - 1) {
//                    changeCategory();  //播放到最后一个了
//                } else {
//                    ++channelIndex;
//                    playingChannel = channelList.get(channelIndex);
//                    getChannelDetail();
//                }
//            } else if (action.equalsIgnoreCase("com.sdt.Intent.CHANGE_NEXT_SOURCE")) {
//                Log.d(TAG, action + ":" + sourceIndex);
//                ++sourceIndex;
//                startPlay();
//            } else if (action.equalsIgnoreCase("com.sdt.Intent.CHANGE_TO_TARGET_CHANNEL")) {
//                categoryIndex = intent.getIntExtra("categoryIndex", 0);
//                channelIndex = intent.getIntExtra("channelIndex", 0);
//                sourceIndex = 0;
//                channelList = GlobalChannelManager.getInstance().getCategoryList().get(categoryIndex).getChannelList();
//                playingChannel = channelList.get(channelIndex);
//                Log.d(TAG, "categoryIndex" + categoryIndex);
//                Log.d(TAG, "channelIndex" + channelIndex);
//                getChannelDetail();
//            }
//        }
//    }

//    private void changeCategory() {
//        ++categoryIndex;
//        channelIndex = 0;
//        channelList = GlobalChannelManager.getInstance().getCategoryList().get(categoryIndex).getChannelList();
//        if (channelList == null || channelList.isEmpty()) {
//            Log.d(TAG, "channelList is empty");
//            return;
//        }
//        Log.d(TAG, "channelList:" + channelList.size());
//        playingChannel = channelList.get(channelIndex);
//        getChannelDetail();
//    }
}

