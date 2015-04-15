package com.tv.xeeng.gamedata.entity;

public class ChatItemData {

	private long mplayerId;
	private String mplayerName;
	private String mmessage;

	public ChatItemData(long pPlayerId, String pPlayerName, String pMessage) {

		mplayerId = pPlayerId;
		mplayerName = pPlayerName;
		mmessage = pMessage;
		if (mplayerName == null) {

			mplayerName = "-";
		}
		if (mmessage == null) {

			mmessage = "-";
		}
	}

	public ChatItemData(ChatItemData origin) {

		mplayerId = origin.mplayerId;
		mplayerName = origin.mplayerName;
		mmessage = origin.mmessage;
	}

	public long getPlayerId() {
		return mplayerId;
	}

	public String getPlayerName() {
		return mplayerName;
	}

	public String getMessage() {
		return mmessage;
	}

}
