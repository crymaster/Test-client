package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class EventItemData implements Parcelable {
	public int index;
	public String eventName;
	public int eventId;

	public EventItemData(int mIndex, String mEventName, int mEventId) {
		index = mIndex;
		eventName = mEventName;
		eventId = mEventId;
	}

	private EventItemData(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(index);
		dest.writeString(eventName);
		dest.writeInt(eventId);
	}

	private void readFromParcel(Parcel source) {
		source.readInt();
		source.readString();
		source.readInt();
	}

	public final static Parcelable.Creator<EventItemData> CREATOR = new Parcelable.Creator<EventItemData>() {

		@Override
		public EventItemData createFromParcel(Parcel source) {
			return new EventItemData(source);
		}

		@Override
		public EventItemData[] newArray(int size) {
			return new EventItemData[size];
		}
	};

}
