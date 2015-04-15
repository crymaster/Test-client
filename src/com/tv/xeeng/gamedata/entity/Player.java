package com.tv.xeeng.gamedata.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.tv.xeeng.gui.itemdata.UserLevelItemData;

public class Player implements Parcelable {

	public enum PlayerState implements Parcelable {

		WAITING("WAITING"), PLAYING("PLAYING"), BET("BET"), OBSERVING(
				"OBSERVING"), RESULT("RESULT"), FAIL("FAIL");

		String description;

		PlayerState(String pdesc) {
			description = pdesc;
		}

		public String toString() {
			return description;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(ordinal());
		}

		public static final Parcelable.Creator<PlayerState> CREATOR = new Parcelable.Creator<Player.PlayerState>() {

			@Override
			public PlayerState createFromParcel(Parcel source) {
				return PlayerState.values()[source.readInt()];
			}

			@Override
			public PlayerState[] newArray(int size) {
				return new PlayerState[size];
			}
		};
	}

	public long id;
	public long cash;
	public long xeeng;
	public long avatarId;
	public String character;
	public String password;
	public boolean sex;
	public boolean isTableMaster, isReady;
	public PlayerState state;
	public long rewardCash;
	public boolean isOut;
	public boolean isBankrupt;
	public boolean isOnline;
	public String displayStr;
	public UserLevelItemData level = new UserLevelItemData();

	public int vipId;
	
	public Player() {
	}

	public Player(long _id, String _name, long _cash, boolean _istablemaster) {
		id = _id;
		cash = _cash;
		character = _name;
		isTableMaster = _istablemaster;
		state = PlayerState.WAITING;
		isReady = false;
		isOut = false;
		isBankrupt = false;
	}

	public Player(Parcel in) {
		readFromParcel(in);
	}

	public void prepareForNewMatch() {

		state = PlayerState.WAITING;
		rewardCash = 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(cash);
		dest.writeLong(xeeng);
		dest.writeLong(avatarId);
		dest.writeString(character);
		dest.writeInt(sex ? 1 : 0);
		dest.writeInt(isTableMaster ? 1 : 0);
		dest.writeInt(isReady ? 1 : 0);
		dest.writeLong(rewardCash);
		dest.writeInt(isOut ? 1 : 0);
		dest.writeInt(isBankrupt ? 1 : 0);
		dest.writeParcelable(state, flags);
		dest.writeInt(isOnline ? 1 : 0);
		dest.writeParcelable(level, flags);
	}

	private void readFromParcel(Parcel in) {
		id = in.readLong();
		cash = in.readLong();
		xeeng = in.readLong();
		avatarId = in.readLong();
		character = in.readString();
		sex = in.readInt() == 1 ? true : false;
		isTableMaster = in.readInt() == 1 ? true : false;
		isReady = in.readInt() == 1 ? true : false;
		isOut = in.readInt() == 1 ? true : false;
		isBankrupt = in.readInt() == 1 ? true : false;
		state = in.readParcelable(PlayerState.class.getClassLoader());
		isOnline = in.readInt() == 1 ? true : false;
		level = in.readParcelable(UserLevelItemData.class.getClassLoader());
	}

	public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {

		@Override
		public Player createFromParcel(Parcel source) {
			return new Player(source);
		}

		@Override
		public Player[] newArray(int size) {
			return new Player[size];
		}
	};
}
