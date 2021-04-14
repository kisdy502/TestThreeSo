package com.sdt.testthreeso;

import com.sdt.testthreeso.bean.Category;
import com.sdt.testthreeso.bean.Channel;
import com.sdt.testthreeso.bean.LiveSource;

/**
 * @ClassName ILiveView
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/22 10:35
 * @Version 1.0
 */
public interface ILiveView {

    public void realPlay(Category category, Channel channel, LiveSource liveSource, boolean refreshMenu);

    void initData(Category mPlayingCategory, Channel mPlayingChannel);
}
