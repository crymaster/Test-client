package com.tv.xeeng.gui.itemdata;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.R;

public class InventoryItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String RES_PREFIX = "event_";

	public static final Parcelable.Creator<InventoryItemData> CREATOR = new Parcelable.Creator<InventoryItemData>() {

		@Override
		public InventoryItemData createFromParcel(Parcel source) {
			return new InventoryItemData(source.readString(),
					source.readString(), source.readString(), source.readInt(),
					source.readInt(), source.readInt() == 1);
		}

		@Override
		public InventoryItemData[] newArray(int size) {
			return new InventoryItemData[size];
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private String itemCode;
	private String name;
	private String description;
	private int isUsable;

	private int count;
	private int resId;

	// ===========================================================
	// Constructors
	// ===========================================================
	public InventoryItemData(String itemCode, String name, String description,
			int count, int resId, boolean isUsable) {
		super();
		this.itemCode = itemCode;
		this.name = name;
		this.description = description;

		this.count = count;
		this.resId = resId;
		this.isUsable = isUsable ? 1 : 0;
	}

	public InventoryItemData(String itemCode, String name, String description,
			int count, boolean isUsable) {
		this(itemCode, name, description, count, R.drawable.homdo_bg_empty,
				isUsable);

		Context context = CustomApplication.shareApplication();
		int id = context.getResources().getIdentifier(RES_PREFIX + itemCode,
				"drawable", context.getPackageName());

		if (id != 0) {
			resId = id;
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isUsable() {
		return isUsable == 1;
	}

	public void setUsable(boolean isUsable) {
		this.isUsable = isUsable ? 1 : 0;
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
		dest.writeString(itemCode);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeInt(count);
		dest.writeInt(resId);
		dest.writeInt(isUsable);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
