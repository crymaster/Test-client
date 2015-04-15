package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.R;

public class CashShopItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TYPE_GOLD = "gold";
	public static final String TYPE_XEENG = "xeng";

	public static final Creator<CashShopItemData> CREATOR = new Creator<CashShopItemData>() {

		@Override
		public CashShopItemData createFromParcel(Parcel source) {
			return new CashShopItemData(source.readString(),
					source.readString(), source.readString(),
					source.readLong(), source.readString());
		}

		@Override
		public CashShopItemData[] newArray(int size) {
			return new CashShopItemData[size];
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private String code;
	private String name;
	private String description;
	private long price;
	private String type;

	// ===========================================================
	// Constructors
	// ===========================================================
	public CashShopItemData(String code, String name, String description,
			long price, String type) {
		super();
		this.code = code;
		this.name = name;
		this.description = description;
		this.price = price;
		this.type = type;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getPrice() {
		return price;
	}

	public String getType() {
		return type;
	}

	public int getResId() {
		int resId = CustomApplication
				.shareApplication()
				.getResources()
				.getIdentifier(InventoryItemData.RES_PREFIX + code, "drawable",
						CustomApplication.shareApplication().getPackageName());
		return resId > 0 ? resId : R.drawable.homdo_bg_empty;
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
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeLong(price);
		dest.writeString(type);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
