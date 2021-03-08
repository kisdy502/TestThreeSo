package com.sdt.testthreeso.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseChannelsResultData extends BaseJsonResultData {
	@SerializedName("version")
	private String version;

    @SerializedName("tvclassList")
    private List<Category> categoryList;

    @SerializedName("channelList")
    private List<Channel> channelList;

    public BaseChannelsResultData() {
	}

    public BaseChannelsResultData(List<Category> categoryList, List<Channel> channelList) {
        super();
        this.categoryList = categoryList;
        this.channelList = channelList;
    }

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("status: " + status);
        builder.append(", categoryList: " + categoryList);
        builder.append(", channelList: " + channelList);
        builder.append(", timestamp: " + timestamp);
        return builder.toString();
    }
}
