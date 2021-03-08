package com.sdt.testthreeso.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelLiveSource {
	/**
	 * channelId : 1
	 * name : CCTV-1
	 * remoteNo : 1
	 * onlineFlag : 0
	 * playList : [{"id":"311885","url":"YXNoZDovLz9jaWQ9bWlndV9jY3R2MWhk","urlType":"1043","playType":"0","definition":"0","interval":"0"}]
	 */

	@SerializedName("channelId") 	private String channelId;
	@SerializedName("name") 		private String name;
	@SerializedName("remoteNo") 	private String remoteNo;
	@SerializedName("onlineFlag") 	private int onlineFlag;
	@SerializedName("playList") 	private List<LiveSource> sourceList;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemoteNo() {
		return remoteNo;
	}

	public void setRemoteNo(String remoteNo) {
		this.remoteNo = remoteNo;
	}

	public int getOnlineFlag() {
		return onlineFlag;
	}

	public void setOnlineFlag(int onlineFlag) {
		this.onlineFlag = onlineFlag;
	}

	public List<LiveSource> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<LiveSource> sourceList) {
		this.sourceList = sourceList;
	}

}