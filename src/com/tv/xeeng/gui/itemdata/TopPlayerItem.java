package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;
import com.tv.xeeng.CommonUtils;

public class TopPlayerItem implements Parcelable {

	public int index;
	public long playerId;
	public String playerName;
	public String playerGold;

	public TopPlayerItem(int mIndex, long mPlayerId, String mPlayerName,
			String mValue) {
		index = mIndex;
		playerId = mPlayerId;
		playerName = mPlayerName;
		playerGold = mValue;
	}

	public TopPlayerItem(int mIndex, String mPlayerName, long cash,
			String suffix) {
		index = mIndex;
		playerName = mPlayerName;
		playerGold = new StringBuffer(CommonUtils.groupingCash(cash))
				.append(" ").append(suffix).toString();
	}

	public TopPlayerItem(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(index);
		dest.writeLong(playerId);
		dest.writeString(playerName);
		dest.writeString(playerGold);
	}

	private void readFromParcel(Parcel source) {
		index = source.readInt();
		playerId = source.readLong();
		playerName = source.readString();
		playerGold = source.readString();
	}

	public static final Parcelable.Creator<TopPlayerItem> CREATOR = new Creator<TopPlayerItem>() {

		@Override
		public TopPlayerItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new TopPlayerItem[size];
		}

		@Override
		public TopPlayerItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new TopPlayerItem(source);
		}
	};

}
