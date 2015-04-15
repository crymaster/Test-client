package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class ShopItemData implements Parcelable {

	private String id;
	private float value;
	private float price;
	private String description;
	private float bonus;

	public ShopItemData() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public float getBonus() {
		return bonus;
	}

	public void setBonus(float bonus) {
		this.bonus = bonus;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeFloat(value);
		dest.writeFloat(price);
		dest.writeString(description);
		dest.writeFloat(bonus);
	}

	public static final Parcelable.Creator<ShopItemData> CREATOR = new Parcelable.Creator<ShopItemData>() {

		@Override
		public ShopItemData createFromParcel(Parcel source) {
			ShopItemData item = new ShopItemData();
			item.setId(source.readString());
			item.setValue(source.readFloat());
			item.setPrice(source.readFloat());
			item.setDescription(source.readString());
			item.setBonus(source.readFloat());
			
			return item;
		}

		@Override
		public ShopItemData[] newArray(int size) {
			return new ShopItemData[size];
		}

	};

}
