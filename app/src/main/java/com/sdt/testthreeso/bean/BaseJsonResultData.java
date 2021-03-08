package com.sdt.testthreeso.bean;

import com.google.gson.annotations.SerializedName;

/**
 * BaseJsonResultData
 *
 * @author vio_wang
 * @date 2018-03-22
 */
public class BaseJsonResultData {
	@SerializedName("status")
	protected int status;

	@SerializedName("timestamp")
	protected long timestamp;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public BaseJsonResultData() {
	}

}
