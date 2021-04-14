package com.sdt.testthreeso.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Category {
    private int _id;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("adtype")
    private String adType;     //1广告分类， 0 普通分类

    @SerializedName("liveType")
    private int liveType;     //1点播， 0 直播

    @SerializedName("channelIds")
    private String channelIds;  //频道排序用

    private List<Channel> channelList=new ArrayList<>();
    private List<Channel> originalChannelList=new ArrayList<>();

    private boolean playing;
    private boolean isExcluded;

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public Category(String id, String name, String type, String adType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.adType = adType;
    }

    public Category() {
        // do nothing
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public int getLiveType() {
        return liveType;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public void setChannelIds(String channelIds) {
        this.channelIds = channelIds;
    }

    public String getChannelIds() {
        return channelIds;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: " + id);
        builder.append(", name: " + name);
        builder.append(", type: " + type);
        builder.append(", adType : " + adType);
        builder.append(", channelList: " + channelList);
        builder.append(", liveType: " + liveType);
        return builder.toString();
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setOriginalChannelList(List<Channel> originalChannelList) {
        this.originalChannelList = originalChannelList;
    }

    public List<Channel> getOriginalChannelList() {
        return originalChannelList;
    }

    public boolean isAdCategory() {
        return TextUtils.equals(adType, "1");
    }


    public boolean isExcluded() {
        return isExcluded;
    }

    public void setExcluded(boolean excluded) {
        isExcluded = excluded;
    }
}
