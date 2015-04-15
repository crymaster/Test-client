package com.tv.xeeng.dataLayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import com.tv.xeeng.CustomApplication;

import java.util.Vector;

public class NetworkUtils {

	public static final String ELEMENT_SEPERATOR = new String(
			new byte[] { (byte) (1) });
	public static final String ARRAY_SEPERATOR = new String(
			new byte[] { (byte) (2) });
	public static final String DIFF_ARRAY_SEPERATOR = new String(
			new byte[] { (byte) (3) });
	public static final String N4_SEPERATOR = new String(
			new byte[] { (byte) (4) });
	public static final String N5_SEPERATOR = new String(
			new byte[] { (byte) (5) });

	public static int PARTNER_ID = 96;
	public final static int MXH = 1;
	public static String ACTIVE_ACCOUNT_NUMBER_PHONE = "number.phone";
	public static String ACTIVE_ACCOUNT_BODY = "body";
	public static String MESSAGE_STATUS = "msg.status";
	public static String MESSAGE_INFO = "msg.info";
	public static String PLAYER_ID = "player.id";
	public static String MESSAGE_INFO_2 = "msg.info.2";
	public static String MESSAGE_INFO_3 = "msg.info.3";
	public static String MESSAGE_INFO_4 = "msg.info.4";
	public static String MESSAGE_INFO_5 = "msg.info.5";

	public static String DUTY_DESCRIPTION = "duty.description";
	public static String FIGHT_PARAMS = "fight.params";
	public static String PLAYED_CARD = "played.card";
	public static String SHOULD_HA_BAI = "should.ha.bai";
	public static String PHOM_ID = "phom.id";
	public static String PLAYER_INDEX = "player.index";

	public static final int MAX_TABLES = 24;

	/**
	 * Check is ping response
	 * 
	 * @param responseMsg
	 * @return
	 */
	public static boolean isPingResponse(String responseMsg) {
		if (responseMsg.charAt(0) == '1' && responseMsg.charAt(1) == 1) {
			return true;
		}
		return false;
	}

	public static String[] stringSplit(String splitStr, String delimiter) {
		if (splitStr.length() == 0) {
			return new String[0];
		}
		StringBuffer token = new StringBuffer();
		Vector<String> tokens = new Vector<String>();
		// split
		// this line may cause Out Of Memory
		int strLength = splitStr.length();
		for (int i = 0; i < strLength; i++) {
			if (delimiter.indexOf(splitStr.charAt(i)) != -1) {
				tokens.addElement(token.toString());
				token.setLength(0);
			} else {
				token.append(splitStr.charAt(i));
			}
		}
		tokens.addElement(token.toString());
		// convert the vector into an array
		String[] splitArray = new String[tokens.size()];
		for (int i = 0; i < splitArray.length; i++) {
			splitArray[i] = (String) tokens.elementAt(i);
		}
		tokens.removeAllElements();
		tokens = null;
		return splitArray;
	}

	public static boolean isOnline() {

		ConnectivityManager conMgr;
		boolean isOnline = true;
		try {

			conMgr = (ConnectivityManager) CustomApplication.shareApplication()
					.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {

				// notify user you are online
				isOnline = true;
			} else {

				// notify user you are not online
				isOnline = false;
			}
		} catch (Exception e) {
		}

		return isOnline;
	}

	public static NetworkInfo.State getWifiState() {

		ConnectivityManager conMgr;
		try {

			conMgr = (ConnectivityManager) CustomApplication.shareApplication()
					.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
			if (activeNetwork != null) {

				return activeNetwork.getState();
			} else {

				return State.UNKNOWN;
			}
		} catch (Exception e) {

			return State.UNKNOWN;
		}
	}
}
