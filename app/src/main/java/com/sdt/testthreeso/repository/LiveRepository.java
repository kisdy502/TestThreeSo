package com.sdt.testthreeso.repository;

import android.util.Log;

import com.sdt.testthreeso.LoadCallback;
import com.sdt.testthreeso.bean.Channel;
import com.sdt.testthreeso.bean.ChannelLiveSource;
import com.sdt.testthreeso.bean.LiveSource;
import com.sdt.testthreeso.bean.SourceDetailResultData;
import com.sdt.testthreeso.net.LiveRetrofit;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName LiveRespotriy
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/20 17:49
 * @Version 1.0
 */
public class LiveRepository {

    private final static String TAG = "LiveRepository";

    public void getChannelDetial(Channel playingChannel, LoadCallback<Integer> callback) {
        LiveRetrofit.getInstance().getApi().getChannelDetail(playingChannel.getChannelId(),
                "1", "440300", "8")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<SourceDetailResultData, Integer>() {
                    @Override
                    public Integer apply(SourceDetailResultData sourceDetailResultData) throws Exception {
                        ChannelLiveSource channelLiveSource = sourceDetailResultData.getLiveSource();
                        if (channelLiveSource != null
                                && channelLiveSource.getSourceList() != null
                                && !channelLiveSource.getSourceList().isEmpty()) {
                            List<LiveSource> sourceList = channelLiveSource.getSourceList();
                            for (LiveSource liveSource : sourceList) {
                                Log.d(TAG, liveSource.getUrlType() + ";" + liveSource.getUrl());
                            }
                            playingChannel.setPlaySourceList(sourceList);
                            return sourceList.size();
                        }
                        return 0;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer souceSize) {
                        callback.onLoadSuccess(souceSize);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                        callback.onLoadFailed(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
