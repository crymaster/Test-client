package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;
import com.tv.xeeng.R;

public class ChargingItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final Parcelable.Creator<ChargingItemData> CREATOR = new Parcelable.Creator<ChargingItemData>() {

		@Override
		public ChargingItemData createFromParcel(Parcel source) {
			return new ChargingItemData(
					ChargingItemType.values()[source.readInt()],
					source.readString(), source.readString());
		}

		@Override
		public ChargingItemData[] newArray(int size) {
			return null;
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private ChargingItemType type;
	private String provider;
	private String smsTemplate;
	private String content;

	private int price;
	private int value;

	// ===========================================================
	// Constructors
	// ===========================================================
	public ChargingItemData(String provider, String content,
			ChargingItemType type, String smsTemplate) {
		this.type = type;
		this.provider = provider;
		this.smsTemplate = smsTemplate != null ? smsTemplate : "";

		String[] tokens = content.split(" = ");
		this.content = content;
		if (tokens.length > 1) {
			this.price = Integer.parseInt(tokens[0].replace("K", "000"));
			this.value = Integer.parseInt(tokens[1].split(" ")[0]);
		} else {
			this.price = -1;
			this.value = -1;
		}
	}

	public ChargingItemData(ChargingItemType type, String provider,
			String content) {
		super();
		this.type = type;
		this.provider = provider;
		this.content = content;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public ChargingItemType getType() {
		return type;
	}

	public String getProvider() {
		return provider;
	}

	public int getPrice() {
		return price;
	}

	public int getValue() {
		return value;
	}

	public String getSmsTemplate() {
		return smsTemplate;
	}

	public String getContent() {
		return content;
	}

	public int getLogoDrawable() {
		if (type == ChargingItemType.SMS) {
			return R.drawable.icon_sms_1;
		}
		if (provider.equalsIgnoreCase("mobifone")) {
			return R.drawable.icon_logo_mobifone;
		}
		if (provider.equalsIgnoreCase("vinaphone")) {
			return R.drawable.icon_logo_vinaphone;
		}
		if (provider.equalsIgnoreCase("viettel")) {
			return R.drawable.icon_logo_viettel;
		}
		if (provider.equalsIgnoreCase("mobay")) {
			return R.drawable.icon_logo_mobay;
		}
		if (type == ChargingItemType.VISA) {
			return R.drawable.icon_logo_visa;
		}
		if (provider.equalsIgnoreCase("fpt")){
			return R.drawable.icon_logo_fpt_gate;
		}
		if (provider.contains("vnpt")){
			return R.drawable.icon_logo_vnpt_epay;
		}
		return R.drawable.icon_sms_1;
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
		dest.writeString(provider);
		dest.writeInt(type.ordinal());
		dest.writeString(content);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	public enum ChargingItemType {
		CARD, SMS, VISA;

		@Override
		public String toString() {
			switch (this) {
			case SMS:
				return "SMS";
			case CARD:
				return "Thẻ cào";
			default:
				return super.toString();
			}
		}
	}
}
