package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class UserLevelItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final Creator<UserLevelItemData> CREATOR = new Creator<UserLevelItemData>() {

		@Override
		public UserLevelItemData createFromParcel(Parcel source) {
			return new UserLevelItemData(source.readLong(),
					source.readString(), source.readLong(), source.readLong());
		}

		@Override
		public UserLevelItemData[] newArray(int size) {
			return new UserLevelItemData[size];
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private long id;
	private String name;
	private long minGold;
	private long maxGold;

	// ===========================================================
	// Constructors
	// ===========================================================
	public UserLevelItemData() {

	}

	public UserLevelItemData(long id, String name, long minGold, long maxGold) {
		super();
		this.id = id;
		this.name = name;
		this.minGold = minGold;
		this.maxGold = maxGold;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getMinGold() {
		return minGold;
	}

	public long getMaxGold() {
		return maxGold;
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
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeLong(minGold);
		dest.writeLong(maxGold);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
