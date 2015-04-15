package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendRequestItemData implements Parcelable {

	public long playerId;
	public String playerName;

	public FriendRequestItemData(long mPlayerId, String mPlayername) {
		playerId = mPlayerId;
		playerName = mPlayername;
	}

	public FriendRequestItemData(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(playerId);
		dest.writeString(playerName);
	}

	private void readFromParcel(Parcel in) {
		playerId = in.readLong();
		playerName = in.readString();
	}

	public static final Parcelable.Creator<FriendRequestItemData> CREATOR = new Parcelable.Creator<FriendRequestItemData>() {

		@Override
		public FriendRequestItemData createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new FriendRequestItemData(source);
		}

		@Override
		public FriendRequestItemData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new FriendRequestItemData[size];
		}
	};
}
