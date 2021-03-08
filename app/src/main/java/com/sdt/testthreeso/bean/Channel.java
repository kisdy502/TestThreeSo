package com.sdt.testthreeso.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Channel {
    public final static int ITEM_TYPE_CHANNEL = 1;
    public final static int ITEM_TYPE_LOVEWATCHCAHNNEL = 2;
    public final static int ITEM_TYPE_VODCHANNEL = 3;
    public final static int ITEM_TYPE_FCAD = 4;

    public final static int NORMAL_CHANNEL = 0;
    public final static int HD_CHANNEL = 1;

    public static final int CHANNEL_TYPE_ADD_SHARE = 9999;
    public static final int CHANNEL_TYPE_CREATE_SHARE = 9998;

    /**
     * province : 110000
     * name : 北京卫视高清
     * remoteNo : 47
     * channelImg : http://192.168.52.17:8888/file/201808/4ae5ec20d65d49b29aabc2f439ab5376.png
     * channelCate : 1,10000
     * channelId : 1788
     */
    private int _id;

    @SerializedName("channelId")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("remoteNo")
    private String remoteNo;

    @SerializedName("province")
    private String province;

    @SerializedName("replayCode")
    private String replayCode;

    @SerializedName("HD")
    private int hd;

    @SerializedName("channelImg")
    private String channelPic;

    @SerializedName("channelCate")
    private String categoryId;

    @SerializedName("listImg")
    private String listImg;

    @SerializedName("type")
    private int channelType;  //频道类型 0为普通频道， 1为跳转频道  自定义TYPE: 9999/9998 订阅频道相关

    private int playSourceIndex = 0;
    private int activeIndex = 0;

    private boolean fcAdChannel = false;

    public boolean isFcAdChannel() {
        return fcAdChannel;
    }

    public void setFcAdChannel(boolean fcad) {
        this.fcAdChannel = fcad;
    }

    @SerializedName("originalCate")
    private String originalCate;

    private int retryTime = 0;
    private int copyrightFlag;

    private boolean exclude;
    private boolean active;
    private boolean collected;
    private boolean playing;

    private long epgLoadTime = 0L;



    private List<LiveSource> playSourceList;
    private LiveSource playSource;

    public Channel() {

    }

    public Channel(String id, String name, String remoteNo, String categoryId) {
        super();
        this.id = id;
        this.name = name;
        this.remoteNo = remoteNo;
        this.categoryId = categoryId;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setExclude(boolean exclude) {
        this.exclude = exclude;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isExclude() {
        return exclude;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelPic() {
        return channelPic;
    }

    public void setChannelPic(String channelPic) {
        this.channelPic = channelPic;
    }

    public String getRemoteNo() {
        return remoteNo;
    }

    public void setRemoteNo(String remoteNo) {
        this.remoteNo = remoteNo;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public List<LiveSource> getPlaySourceList() {
        return playSourceList;
    }

    public void setPlaySourceList(List<LiveSource> sourceList) {
        this.playSourceList = sourceList;
    }



    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public void resetRetryTime() {
        retryTime = 0;
    }


    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public long getEpgLoadTime() {
        return epgLoadTime;
    }

    public void setEpgLoadTime(long epgLoadTime) {
        this.epgLoadTime = epgLoadTime;
    }

    public String getReplayCode() {
        return replayCode;
    }

    public void setReplayCode(String replayCode) {
        this.replayCode = replayCode;
    }

    public int getHd() {
        return hd;
    }

    public boolean isHDChannel() {
        return hd == HD_CHANNEL;   //  0: 非高清 1:高清频道
    }

    public void setHd(int hd) {
        this.hd = hd;
    }

    public String getListImg() {
        return listImg;
    }

    public void setListImg(String listImg) {
        this.listImg = listImg;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    //    判断是否是图片channel
    public boolean isVodType() {
        return channelType == 1;
    }

    public boolean isAddShareChannel() {
        return channelType == CHANNEL_TYPE_ADD_SHARE;
    }

    public boolean isCreateShareChannel() {
        return channelType == CHANNEL_TYPE_CREATE_SHARE;
    }

    public String getOriginalCate() {
        return originalCate;
    }

    public void setOriginalCate(String originalCate) {
        this.originalCate = originalCate;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: " + id);
        builder.append(", name: " + name);
        return builder.toString();
    }


    public void setCopyrightFlag(int copyrightFlag) {
        this.copyrightFlag = copyrightFlag;
    }

    public int getCopyrightFlag() {
        return copyrightFlag;
    }


    /**
     * 判断一个频道属于广告节目，首先要获取其所属分类
     * 遍历所有的分类，如果有一个分类的adType值为1，就是广告频道
     *
     * @param channel
     * @return
     */
    public static boolean isAdChannel(Channel channel, Category adCategory) {
        if (channel == null) {
            return false;
        }
        String cateId = channel.getCategoryId();
        if (TextUtils.isEmpty(cateId)) {
            return false;
        }
        String[] cateIds = cateId.split(",");
        for (String id : cateIds) {
            if (adCategory != null) {
                if (id.equalsIgnoreCase(adCategory.getId())) {
                    return true;
                }
            }
        }
        return false;

    }

    private static Category getCategory(String cateId, List<Category> mCategoryList) {
        if (mCategoryList == null || mCategoryList.isEmpty() || TextUtils.isEmpty(cateId)) {
            return null;
        }
        for (Category category : mCategoryList) {
            if (category.getId().equals(cateId)) {
                return category;
            }
        }
        return null;
    }

}
