package com.tv.xeeng.gui.itemdata;

import android.os.Parcel;
import android.os.Parcelable;

public class MonthlyEventItemData implements Parcelable {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String EVENT_TYPE_GHEP_CHU = "EVENT_GHEP_CHU";
	public static final String EVENT_TYPE_LAT_BAI = "EVENT_LAT_BAI";

	public static final Creator<MonthlyEventItemData> CREATOR = new Creator<MonthlyEventItemData>() {

		@Override
		public MonthlyEventItemData createFromParcel(Parcel source) {
			return new MonthlyEventItemData(source.readLong(),
					source.readString(), source.readString(),
					source.readString(),
					(String[]) source.readArray(String.class.getClassLoader()),
					source.readString());
		}

		@Override
		public MonthlyEventItemData[] newArray(int size) {
			return null;
		}
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private long id;

	private String name;
	private String content;
	private String description;

	private String[] components;
	private String type;

	// ===========================================================
	// Constructors
	// ===========================================================
	public MonthlyEventItemData(long id, String name, String content,
			String description, String[] components, String type) {
		super();
		this.id = id;
		this.name = name;
		this.content = content;
		this.description = description;
		this.components = components;
		this.type = type;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public String getDescription() {
		return description;
	}

	public String[] getComponents() {
		return components;
	}

	public String getType() {
		return type;
	}

	public long getId() {
		return id;
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
		dest.writeString(name);
		dest.writeString(content);
		dest.writeString(description);
		dest.writeArray(components);
		dest.writeString(type);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
