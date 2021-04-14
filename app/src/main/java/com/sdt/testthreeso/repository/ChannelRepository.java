package com.sdt.testthreeso.repository;

import android.text.TextUtils;
import android.util.Log;

import com.sdt.testthreeso.ChannelManager;
import com.sdt.testthreeso.bean.BaseChannelsResultData;
import com.sdt.testthreeso.bean.Category;
import com.sdt.testthreeso.bean.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ChannelRepository
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/17 14:02
 * @Version 1.0
 */
public class ChannelRepository {

    private final static String TAG = "ChannelRepository";

    public void handleChannelResult(BaseChannelsResultData baseResult) {
        long start = System.currentTimeMillis();
        List<Channel> notCateChannelList = new ArrayList<>();
        List<Category> categoryList = baseResult.getCategoryList();
        List<Channel> channelList = baseResult.getChannelList();
        for (Category category : categoryList) {
            for (Channel channel : channelList) {
                String channelCate = channel.getChannelCate();
                if (TextUtils.isEmpty(channelCate)) {
                    notCateChannelList.add(channel);
                } else {
                    String[] cateArray = channelCate.split(",");
                    if (channelIsCategory(category, cateArray)) {
                        category.getChannelList().add(channel);
                    }
                }
            }
            Log.i(TAG, "Handle 分类:" + category.getId() + "," + category.getName());
            for (Channel channel : category.getChannelList()) {
                Log.d(TAG, channel.getChannelId() + "," + channel.getName() + "," + channel.getChannelCate());
            }
            Log.w(TAG, "Handle end-------------------------------------------------------------");
        }
        ChannelManager.getInstance().setCategoryList(categoryList);
        long end = System.currentTimeMillis();
        Log.i(TAG, "Handle all Category coust time(ms):" + (end - start));
    }

    private boolean channelIsCategory(Category category, String[] cateArray) {
        for (String cate : cateArray) {
            if (category.getId().equalsIgnoreCase(cate)) {
                return true;
            }
        }
        return false;
    }

}
