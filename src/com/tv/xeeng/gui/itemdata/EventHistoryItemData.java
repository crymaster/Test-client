package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class EventHistoryItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final Creator<EventHistoryItemData> CREATOR = new Creator<EventHistoryItemData>() {

		@Override
		public EventHistoryItemData createFromParcel(Parcel source) {
			return new EventHistoryItemData(source.readString(),
					source.readLong());
		}

		@Override
		public EventHistoryItemData[] newArray(int size) {
			return new EventHistoryItemData[size];
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private String message;
	private long date;

	// ===========================================================
	// Constructors
	// ===========================================================
	public EventHistoryItemData(String message, long date) {
		super();
		this.message = message;
		this.date = date;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public String getMessage() {
		return message;
	}

	public long getDate() {
		return date;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(message);
		dest.writeLong(date);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
