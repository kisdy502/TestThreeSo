package com.sdt.testthreeso.presenter;

import android.util.Log;

import com.sdt.testthreeso.ChannelManager;
import com.sdt.testthreeso.ILiveView;
import com.sdt.testthreeso.LoadCallback;
import com.sdt.testthreeso.bean.Category;
import com.sdt.testthreeso.bean.Channel;
import com.sdt.testthreeso.bean.LiveSource;
import com.sdt.testthreeso.repository.LiveRepository;

/**
 * @ClassName LivePresent
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/20 17:49
 * @Version 1.0
 */
public class LivePresenter {

    private static final String TAG = "LivePresenter";
    ILiveView liveView;

    private LiveRepository liveRepository;

    private int categoryIndex = 0;
    private int channelIndex = 0;
    private int sourceIndex = 0;

    private Category mPlayingCategory;
    private Channel mPlayingChannel;

    public LivePresenter(ILiveView liveView) {
        this.liveView = liveView;
        liveRepository = new LiveRepository();
    }

    public void changeNextCategory() {

    }

    public void changeNextChannel() {
        if (channelIndex == mPlayingCategory.getChannelList().size() - 1) {
            Log.d(TAG, "当前分类最后一个频道!");
        } else {
            channelIndex++;
            mPlayingChannel = mPlayingCategory.getChannelList().get(channelIndex);
            sourceIndex = 0;
            preparePlay(true);
        }
    }

    public void changeNextSource() {
        if (mPlayingChannel.getPlaySourceList() == null || mPlayingChannel.getPlaySourceList().isEmpty()) {
            Log.w(TAG, "当前节目没有可以播放的源");
            return;
        } else {
            if (sourceIndex == mPlayingChannel.getPlaySourceList().size() - 1) {
                Log.w(TAG, "已经是最后一个源了");
            } else {
                sourceIndex++;
                preparePlay(true);
            }
        }
    }

    public void preparePlay(boolean refreshMenu) {
        if (mPlayingChannel.getPlaySourceList() == null || mPlayingChannel.getPlaySourceList().isEmpty()) {
            liveRepository.getChannelDetial(mPlayingChannel, new LoadCallback<Integer>() {
                @Override
                public void onLoadSuccess(Integer sourceSize) {
                    if (sourceSize == 0) {
                        Log.e(TAG, "节目没有可以播放的源:" + mPlayingChannel.getName());
                    } else {
                        LiveSource liveSource = mPlayingChannel.getPlaySourceList().get(sourceIndex);
                        liveView.realPlay(mPlayingCategory, mPlayingChannel, liveSource, refreshMenu);
                    }
                }

                @Override
                public void onLoadFailed(Throwable e) {
                    Log.e(TAG, "请求源列表接口出错!" + mPlayingChannel.getName());
                }
            });
        } else {
            LiveSource liveSource = mPlayingChannel.getPlaySourceList().get(sourceIndex);
            liveView.realPlay(mPlayingCategory, mPlayingChannel, liveSource, refreshMenu);
        }
    }

    public void changeChannelByMenu(Category targetCategory, Channel targetChanel) {
        mPlayingCategory = targetCategory;
        mPlayingChannel = targetChanel;
        categoryIndex = ChannelManager.getInstance().getCategoryIndex(mPlayingCategory);
        channelIndex = ChannelManager.getInstance().getChannelIndex(mPlayingCategory, mPlayingChannel);
        sourceIndex = 0;
        preparePlay(false);
    }


    public void loadCategoryAndChannel() {
        mPlayingCategory = ChannelManager.getInstance().getCategoryList().get(categoryIndex);
        if (mPlayingCategory.getChannelList() == null || mPlayingCategory.getChannelList().isEmpty()) {
            categoryIndex++;
            loadCategoryAndChannel();
        } else {
            mPlayingChannel = mPlayingCategory.getChannelList().get(channelIndex);
            sourceIndex = 0;
            liveView.initData(mPlayingCategory, mPlayingChannel);
            preparePlay(false);
        }
    }
}
