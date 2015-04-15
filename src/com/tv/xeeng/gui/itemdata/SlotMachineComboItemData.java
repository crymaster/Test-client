package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class SlotMachineComboItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final Creator<SlotMachineComboItemData> CREATOR = new Creator<SlotMachineComboItemData>() {

		@Override
		public SlotMachineComboItemData[] newArray(int size) {
			return new SlotMachineComboItemData[size];
		}

		@Override
		public SlotMachineComboItemData createFromParcel(Parcel source) {
			return new SlotMachineComboItemData(source.readInt(),
					source.readString(), source.readString(),
					source.readString());
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private int round;
	private String lastCombo;
	private String nextCombo;
	private String price;

	// ===========================================================
	// Constructors
	// ===========================================================
	public SlotMachineComboItemData(int round, String lastCombo,
			String nextCombo, String price) {
		super();
		this.round = round;
		this.lastCombo = lastCombo;
		this.nextCombo = nextCombo;
		this.price = price;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public int getRound() {
		return round;
	}

	public String getLastCombo() {
		return lastCombo;
	}

	public String getNextCombo() {
		return nextCombo;
	}

	public String getPrice() {
		return price;
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
		dest.writeInt(round);
		dest.writeString(lastCombo);
		dest.writeString(nextCombo);
		dest.writeString(price);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
