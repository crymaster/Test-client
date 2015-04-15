package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class InvitationItemData implements Parcelable {

	public long inviterPlayerId;
	public int gameId;
	public String invitationStr;
	public long minBetCash;
	public long requestId;

	public InvitationItemData(long _inviterId, int _gameId,
			String _invitationStr, long _minBet, long _requestId) {
		inviterPlayerId = _inviterId;
		gameId = _gameId;
		invitationStr = _invitationStr;
		minBetCash = _minBet;
		requestId = _requestId;
	}

	public InvitationItemData(Parcel source) {
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
		dest.writeLong(inviterPlayerId);
		dest.writeInt(gameId);
		dest.writeString(invitationStr);
		dest.writeLong(minBetCash);
		dest.writeLong(requestId);
	}

	private void readFromParcel(Parcel source) {
		inviterPlayerId = source.readLong();
		gameId = source.readInt();
		invitationStr = source.readString();
		minBetCash = source.readLong();
		requestId = source.readLong();
	}

	public static final Parcelable.Creator<InvitationItemData> CREATOR = new Parcelable.Creator<InvitationItemData>() {

		@Override
		public InvitationItemData createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new InvitationItemData(source);
		}

		@Override
		public InvitationItemData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new InvitationItemData[size];
		}
	};

}
