package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class SMSPayment implements Parcelable {

	public String MISDN;
	public String partner;
	public String content;

	public SMSPayment(String _MSIDN, String _partner, String _content) {
		MISDN = _MSIDN;
		partner = _partner;
		content = _content;
	}

	public SMSPayment(Parcel source) {
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
		dest.writeString(MISDN);
		dest.writeString(partner);
		dest.writeString(content);
	}

	private void readFromParcel(Parcel in) {
		MISDN = in.readString();
		partner = in.readString();
		content = in.readString();
	}

	public static final Parcelable.Creator<SMSPayment> CREATOR = new Parcelable.Creator<SMSPayment>() {

		@Override
		public SMSPayment createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new SMSPayment(source);
		}

		@Override
		public SMSPayment[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SMSPayment[size];
		}

	};

}
