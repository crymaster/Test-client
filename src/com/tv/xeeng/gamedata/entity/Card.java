package com.tv.xeeng.gamedata.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Comparable<Card>, Parcelable {

	public static final int CARD_TYPE_CHUON = 1;
	public static final int CARD_TYPE_BICH = 2;
	public static final int CARD_TYPE_RO = 3;
	public static final int CARD_TYPE_CO = 4;

	public int value;
	public int type;
	public int serverValue;

	public Card(int mServerValue) {
		serverValue = mServerValue;
		value = (mServerValue - 1) % 13 + 1;
		int temp = (mServerValue - 1) / 13 + 1;

		switch (temp) {
		case 1:
			type = CARD_TYPE_CHUON;
			break;
		case 2:
			type = CARD_TYPE_BICH;
			break;
		case 3:
			type = CARD_TYPE_RO;
			break;
		case 4:
			type = CARD_TYPE_CO;
			break;
		default:
			break;
		}
	}

	public Card(int pValue, int pType) {

		value = pValue;
		type = pType;
	}

	public Card(Card pcard) {

		serverValue = pcard.serverValue;
		value = pcard.value;
		type = pcard.type;
	}

	public Card(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int compareTo(Card another) {

		if (serverValue < another.serverValue) {
			return -1;
		} else if (serverValue > another.serverValue) {
			return 1;
		}

		return 0;
	}

	public int compareInTLMN(Card another) {
		if (getCardValueInTLMN() < another.getCardValueInTLMN()) {
			return -1;
		} else if (getCardValueInTLMN() > another.getCardValueInTLMN()) {
			return 1;
		} else {
			if (getCardTypeInTLMN() < another.getCardTypeInTLMN()) {
				return -1;
			} else if (getCardTypeInTLMN() > another.getCardTypeInTLMN()) {
				return 1;
			}

			return 0;
		}
	}

	public int compareDefault(Card another) {
		if (value < another.value) {
			return -1;
		} else if (value > another.value) {
			return 1;
		} else {
			if (type < another.type) {
				return -1;
			} else if (type > another.type) {
				return 1;
			}

			return 0;
		}
	}

	public int compareInSam(Card another) {
		if (getCardValueInTLMN() < another.getCardValueInTLMN()) {
			return -1;
		} else if (getCardValueInTLMN() > another.getCardValueInTLMN()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public int compareInPoker(Card another) {
		if (getCardValueInPoker() < another.getCardValueInPoker()) {
			return -1;
		}
		if (getCardValueInPoker() > another.getCardValueInPoker()) {
			return 1;
		}
		return 0;
	}

	public int getCardValueInTLMN() {
		int result = (serverValue - 1) % 13;

		switch (result) {
		case 0:
			return 11;
		case 1:
			return 12;
		default:
			return result - 2;
		}
	}
	
	public int getCardValueInPoker() {
		return (serverValue + 11) % 13;
	}

	public int getCardTypeInTLMN() {
		int type = (serverValue - 1) / 13;

		switch (type) {
		case 0:
			return 2;
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 4;
		default:
			return -1;
		}
	}

	// @Override
	// public boolean equals(Object o) {
	// if (o == null)
	// return false;
	// if (!(o instanceof Card))
	// return false;
	//
	// Card otherCard = (Card) o;
	// return this.serverValue == otherCard.serverValue;
	// }

	//
	// @Override
	// public int hashCode() {
	// return serverValue - 1;
	// }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(serverValue);
		dest.writeInt(value);
		dest.writeInt(type);
	}

	private void readFromParcel(Parcel source) {
		serverValue = source.readInt();
		value = source.readInt();
		type = source.readInt();
	}

	public final static Parcelable.Creator<Card> CREATOR = new Creator<Card>() {

		@Override
		public Card[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Card[size];
		}

		@Override
		public Card createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Card(source);
		}
	};

}
