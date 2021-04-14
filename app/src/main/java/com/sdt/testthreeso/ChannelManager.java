package com.sdt.testthreeso;

import com.sdt.testthreeso.bean.Category;
import com.sdt.testthreeso.bean.Channel;

import java.util.List;

/**
 * @ClassName GlobalChannelManager
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/18 10:32
 * @Version 1.0
 */
public class ChannelManager {

    private List<Category> mCategoryList;

    public List<Category> getCategoryList() {
        return mCategoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.mCategoryList = categoryList;
    }

    public int getCategoryIndex(Category category) {
        for (int i = 0, size = mCategoryList.size(); i < size; i++) {
            if (mCategoryList.get(i).getId().equalsIgnoreCase(category.getId())) {
                return i;
            }
        }
        return -1;
    }

    public int getChannelIndex(Category category, Channel channel) {
        List<Channel> channelList = category.getChannelList();
        if (channelList == null || channelList.isEmpty()) {
            return -1;
        }
        for (int i = 0, size = channelList.size(); i < size; i++) {
            if (channelList.get(i).getChannelId().equalsIgnoreCase(channel.getChannelId())) {
                return i;
            }
        }
        return -1;
    }

    public static ChannelManager getInstance() {
        return Holder.manager;
    }


    private final static class Holder {
        private static ChannelManager manager = new ChannelManager();
    }
}
