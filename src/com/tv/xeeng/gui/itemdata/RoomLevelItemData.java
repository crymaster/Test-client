package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class RoomLevelItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final Parcelable.Creator<RoomLevelItemData> CREATOR = new Parcelable.Creator<RoomLevelItemData>() {

		@Override
		public RoomLevelItemData createFromParcel(Parcel source) {
			long[] bets = {};
			source.readLongArray(bets);
			return new RoomLevelItemData(source.readInt(), source.readString(),
					source.readLong(), bets, source.readInt() > 0);
		}

		@Override
		public RoomLevelItemData[] newArray(int size) {
			return new RoomLevelItemData[size];
		}
	};

	// ===========================================================
	// Fields
	// ===========================================================
	private int id;
	private String name;

	private int vip;

	private long minBuyIn;
	private long[] availableBuyIn;

	// ===========================================================
	// Constructors
	// ===========================================================
	public RoomLevelItemData(int id, String name, long minBet,
			long[] availableBets, boolean isVip) {
		super();
		this.id = id;
		this.name = name;
		this.minBuyIn = minBet;
		this.availableBuyIn = availableBets;
		this.vip = isVip ? 1 : 0;
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
		return minBuyIn;
	}

	public long[] getAvailableBets() {
		return availableBuyIn;
	}

	public boolean isVip() {
		return vip > 0;
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
		dest.writeLong(minBuyIn);
		dest.writeLongArray(availableBuyIn);
		dest.writeInt(vip);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
