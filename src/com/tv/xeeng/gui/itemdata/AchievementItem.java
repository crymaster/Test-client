package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class AchievementItem implements Parcelable {
	public int itemId;
	public String title;

	public AchievementItem(int mItemId, String mTitle) {
		itemId = mItemId;
		title = mTitle;
	}

	public AchievementItem(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(itemId);
		dest.writeString(title);
	}

	private void readFromParcel(Parcel source) {
		itemId = source.readInt();
		title = source.readString();
	}

	public static final Parcelable.Creator<AchievementItem> CREATOR = new Parcelable.Creator<AchievementItem>() {

		@Override
		public AchievementItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new AchievementItem(source);
		}

		@Override
		public AchievementItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new AchievementItem[size];
		}
	};

}
