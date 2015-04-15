package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class RewardItemData implements Parcelable {

	private int mindex;
	private String mdescriptionString;
	private int mcode;

	public RewardItemData() {
		// TODO Auto-generated constructor stub
	}

	public RewardItemData(int mIndex, int mCode, String mDescription) {
		setIndex(mIndex);
		setDescriptionString(mDescription);
		setCode(mCode);
	}

	public RewardItemData(Parcel in) {
		readFromParcel(in);
	}

	public int getIndex() {
		return mindex;
	}

	public void setIndex(int index) {
		this.mindex = index;
	}

	public String getDescriptionString() {
		return mdescriptionString;
	}

	public void setDescriptionString(String descriptionString) {
		this.mdescriptionString = descriptionString;
	}

	public int getCode() {
		return mcode;
	}

	public void setCode(int code) {
		this.mcode = code;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mindex);
		dest.writeString(mdescriptionString);
		dest.writeInt(mcode);
	}

	private void readFromParcel(Parcel in) {
		mindex = in.readInt();
		mdescriptionString = in.readString();
		mcode = in.readInt();
	}

	public static final Parcelable.Creator<RewardItemData> CREATOR = new Parcelable.Creator<RewardItemData>() {

		@Override
		public RewardItemData createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new RewardItemData(source);
		}

		@Override
		public RewardItemData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new RewardItemData[size];
		}
	};
}
