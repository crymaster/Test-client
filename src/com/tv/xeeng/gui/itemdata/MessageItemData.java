package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageItemData implements Parcelable {
	public String userName;
	public int messageId;
	public String messageName;
	public int index;

	public MessageItemData(int mIndex, int messageID, String mEventName,
			String mMessageName) {
		messageId = messageID;
		userName = mEventName;
		messageName = mMessageName;
		index = mIndex;
	}

	public MessageItemData(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userName);
		dest.writeInt(messageId);
		dest.writeString(messageName);
	}

	private void readFromParcel(Parcel source) {
		source.readInt();
		source.readString();
		source.readInt();
		source.readString();
	}

	public final static Parcelable.Creator<MessageItemData> CREATOR = new Parcelable.Creator<MessageItemData>() {

		@Override
		public MessageItemData createFromParcel(Parcel source) {
			return new MessageItemData(source);
		}

		@Override
		public MessageItemData[] newArray(int size) {
			return new MessageItemData[size];
		}
	};

}
