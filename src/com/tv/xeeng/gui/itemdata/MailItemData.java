package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class MailItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final Parcelable.Creator<MailItemData> CREATOR = new Parcelable.Creator<MailItemData>() {

		@Override
		public MailItemData createFromParcel(Parcel source) {
			return new MailItemData(source.readLong(), source.readLong(),
					source.readString(), source.readString(), source.readLong());
		}

		@Override
		public MailItemData[] newArray(int size) {
			return new MailItemData[size];
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private long id;
	private long senderId;

	private String title;
	private String content;

	private long dateSent;

	// ===========================================================
	// Constructors
	// ===========================================================
	public MailItemData(long id, long senderId, String title, String content,
			long dateSent) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.title = title;
		this.content = content;
		this.dateSent = dateSent;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public long getId() {
		return id;
	}

	public long getSenderId() {
		return senderId;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public long getDateSent() {
		return dateSent;
	}

	public void setContent(String content) {
		this.content = content;
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
		dest.writeLong(id);
		dest.writeLong(senderId);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeLong(dateSent);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
