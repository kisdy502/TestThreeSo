package com.sdt.testthreeso.bean;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

public class LiveSource {
	/**
	 * id : 311885
	 * url : YXNoZDovLz9jaWQ9bWlndV9jY3R2MWhk
	 * urlType : 1043
	 * playType : 0
	 * definition : 0
	 * interval : 0
	 */

	@SerializedName("id") 			private String id;
	@SerializedName("url")		 	private String url;
	@SerializedName("urlType") 		private int urlType;
	@SerializedName("playType") 	private int playType;
	@SerializedName("definition") 	private int definition;
	@SerializedName("interval") 	private int interval;

	private String name;

	private int number;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		try {
			if (url.startsWith("kds") || url.startsWith("http") || url.startsWith("p2p")) {
				return url;
			} else {
				byte[] urlByte = Base64.decode(url.getBytes(), Base64.DEFAULT);
				return new String(urlByte);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getUrlType() {
		return urlType;
	}

	public void setUrlType(int urlType) {
		this.urlType = urlType;
	}

	public int getPlayType() {
		return playType;
	}

	public void setPlayType(int playType) {
		this.playType = playType;
	}

	public int getDefinition() {
		return definition;
	}

	public void setDefinition(int definition) {
		this.definition = definition;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}


	public LiveSource() {
	}

	@Override
    public String toString() {
		final StringBuffer sb = new StringBuffer("LiveSource{");
		sb.append("id='").append(id).append('\'');
		sb.append(", url='").append(url).append('\'');
		sb.append(", urlType=").append(urlType);
		sb.append(", playType=").append(playType);
		sb.append(", definition=").append(definition);
		sb.append(", interval=").append(interval);
		sb.append(", name='").append(name).append('\'');
		sb.append(", number=").append(number);
		sb.append('}');
		return sb.toString();
	}
}