package com.tv.xeeng.gui.itemdata;

public class ChargingMethodItemData {
	public enum ChargingType {
		TYPE_SMS, TYPE_CARD, GIFTCODE, TYPE_CARD_VIETTEL, TYPE_CARD_MOBIFONE, TYPE_CARD_VINAPHONE;
	}

	public ChargingType type;
	public String getValue;
	public String giveValue;
	public SMSPayment payment;

	public ChargingMethodItemData(ChargingType mType, String mGiveValue,
			String mGetValue) {
		type = mType;
		giveValue = mGiveValue;
		getValue = mGetValue;
	}

	public ChargingMethodItemData(String mGiveValue, String mGetValue,
			SMSPayment mPayment) {
		type = ChargingType.TYPE_SMS;
		giveValue = mGiveValue;
		getValue = mGetValue;
		payment = mPayment;
	}

}
