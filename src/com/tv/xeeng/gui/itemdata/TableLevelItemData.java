package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class TableLevelItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final Parcelable.Creator<TableLevelItemData> CREATOR = new Parcelable.Creator<TableLevelItemData>() {

		@Override
		public TableLevelItemData createFromParcel(Parcel source) {
			long[] bets = {};
			source.readLongArray(bets);
			return new TableLevelItemData(source.readInt(),
					source.readString(), source.readLong(), bets);
		}

		@Override
		public TableLevelItemData[] newArray(int size) {
			return new TableLevelItemData[size];
		}
	};

	// ===========================================================
	// Fields
	// ===========================================================
	private int id;
	private String name;

	private long minBet;
	private long[] availableBets;

	// ===========================================================
	// Constructors
	// ===========================================================
	public TableLevelItemData(int id, String name, long minBet,
			long[] availableBets) {
		super();
		this.id = id;
		this.name = name;
		this.minBet = minBet;
		this.availableBets = availableBets;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getMinBet() {
		return minBet;
	}

	public long[] getAvailableBets() {
		return availableBets;
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
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeLong(minBet);
		dest.writeLongArray(availableBets);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
