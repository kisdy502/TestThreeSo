package com.sdt.testthreeso.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * SourceDetailResultData
 *
 * @author vio_wang
 * @date 2018-08-31
 */
public class SourceDetailResultData extends BaseJsonResultData {

	@SerializedName("channel")
	private ChannelLiveSource liveSource;

	public ChannelLiveSource getLiveSource() {
		return liveSource;
	}

	public void setLiveSource(ChannelLiveSource liveSource) {
		this.liveSource = liveSource;
	}


}
