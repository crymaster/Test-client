package com.tv.xeeng.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.gamedata.GameData;

public class UserPreference {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String SENT_BACKEND_STATUS = "reg_id_backend_status";

	private static final String KEY_TUTORIAL = "key_tutorial";
	private static final String KEY_INSTALL_REFFERER = "key_install_referrer";

	private static String USER_ACCOUNT_KEY = "account";
	private static String USER_PASSWORD_KEY = "pswd";
	private static String UP_STORE = "game_up";
	private static String USER_ACCOUNT_LOGIN = "login.account";
	private static String FACEBOOK_USER_ID = "facebook.user.id";
	private static String REG_COUNT = "reg.count";
	private static String SOUND_BG_MUSIC_KEY = "sound_bg_music";
	private static String SOUND_EFFECT_KEY = "sound_effect";
	private static String VIBRATION_KEY = "vibration";
	private static String AUTO_DENY_INVITAION = "auto.deny.invitaion";
	private static String FB_ACCESS_TOKEN = "access_token";
	private static String FB_ACCESS_EXPIRES = "access_expires";
	private static UserPreference instance = new UserPreference();
	private SharedPreferences reader;
	private Editor writer;

	private UserPreference() {

		try {
			reader = CustomApplication.shareApplication().getSharedPreferences(
					UP_STORE, Context.MODE_PRIVATE);
			writer = reader.edit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static UserPreference sharePreference() {

		return instance;
	}

	public void rememberRegCount(int regCount) {
		writer.putInt(REG_COUNT, regCount);
		writer.commit();
	}

	public int readRegCount() {
		return reader.getInt(REG_COUNT, 0);
	}

	public void rememberFacebookUserId(String fbUserId) {
		if (fbUserId == null) {
			return;
		}

		writer.putString(FACEBOOK_USER_ID, fbUserId);
		writer.commit();
	}

	public String readFacebookUserId() {
		return reader.getString(FACEBOOK_USER_ID, "");
	}

	public void rememberUserLogin(String username) {
		if (username == null) {
			return;
		}

		writer.putString(USER_ACCOUNT_LOGIN, username);
		writer.commit();
	}

	public String readLoginUsername() {
		return reader.getString(USER_ACCOUNT_LOGIN, "");
	}

	public void rememberUser(String acc, String pswd) {

		if (acc == null && pswd == null) {

			writer.remove(USER_ACCOUNT_KEY);
			writer.remove(USER_PASSWORD_KEY);
			writer.commit();
			return;
		}
		writer.putString(USER_ACCOUNT_KEY, acc);
		writer.putString(USER_PASSWORD_KEY, pswd);
		writer.commit();
	}

	/*
	 * description: return user account & password that user remembered before
	 * return null if not
	 */
	public String[] getUser() {

		String result[] = null;
		String acc = reader.getString(USER_ACCOUNT_KEY, null);
		String pswd = reader.getString(USER_PASSWORD_KEY, null);
		if (acc != null && pswd != null) {

			result = new String[2];
			result[0] = acc;
			result[1] = pswd;
		}

		return result;
	}

	public boolean isSoundBgMusicOn() {

		boolean result = true;
		result = reader.getBoolean(SOUND_BG_MUSIC_KEY, true);
		return result;
	}

	public boolean isSoundEffectOn() {

		boolean result = true;
		result = reader.getBoolean(SOUND_EFFECT_KEY, true);
		return result;
	}

	public void setSoundBgMusicEnable(boolean isEnable) {

		writer.putBoolean(SOUND_BG_MUSIC_KEY, isEnable);
		writer.commit();
	}

	public void setSoundEffectEnable(boolean isEnable) {

		writer.putBoolean(SOUND_EFFECT_KEY, isEnable);
		writer.commit();
	}

	public boolean isVibrationOn() {
		return reader.getBoolean(VIBRATION_KEY, false);
	}

	public void setVibrationEnable(boolean isEnable) {
		writer.putBoolean(VIBRATION_KEY, isEnable);
		writer.commit();
	}

	public boolean isAutoDenyInvitation() {
		return reader.getBoolean(AUTO_DENY_INVITAION, false);
	}

	public void setAutoDenyInvitation(boolean autoDeny) {

		GameData.shareData().isRejectAllInvitaion = autoDeny;

		writer.putBoolean(AUTO_DENY_INVITAION, autoDeny);
		writer.commit();
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	public void storeRegistrationId(Context context, String regId) {

		int appVersion = CommonUtils.getAppVersion(context);
		writer.putString(PROPERTY_REG_ID, regId);
		writer.putInt(PROPERTY_APP_VERSION, appVersion);
		writer.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p/>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(Context context) {

		String registrationId = reader.getString(PROPERTY_REG_ID, "");
		if (registrationId == null || registrationId.length() == 0) {

			return "";
			// 2000
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = reader.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = CommonUtils.getAppVersion(context);
		if (registeredVersion != currentVersion) {

			return "";
		}
		return registrationId;
	}

	public void storeBackendStatus(boolean pStatus) {

		writer.putBoolean(SENT_BACKEND_STATUS, pStatus);
		writer.commit();
	}

	public boolean getBackendStatus() {

		return reader.getBoolean(SENT_BACKEND_STATUS, false);
	}

	public long getFbExpires(Context context) {

		long expires = reader.getLong(FB_ACCESS_EXPIRES, 0);
		return expires;

	}

	public String getFbAccessToken(Context context) {
		String token = reader.getString(FB_ACCESS_TOKEN, "");
		if (token == null || token.length() == 0) {

			return "";
		}

		return token;
	}

	public void storeFbAccessToken(String mValue) {

		writer.putString(FB_ACCESS_TOKEN, mValue);
		writer.commit();

	}

	public void storeFbExpires(long mValue) {

		writer.putLong(FB_ACCESS_EXPIRES, mValue);
		writer.commit();
	}

	public void storeNewbieStatus() {
		writer.putInt(KEY_TUTORIAL,
				getNewbieStatus() < 4 ? getNewbieStatus() + 1 : 4);
		writer.commit();
	}

	public int getNewbieStatus() {
		Log.d("UserPreference",
				"Newbie status: " + reader.getInt(KEY_TUTORIAL, 0));
		return reader.getInt(KEY_TUTORIAL, 0);
	}

	public void storeInstallRefferer(String content) {
		writer.putString(KEY_INSTALL_REFFERER, content);
		writer.commit();
	}

	public String getInstallRefferer() {
		return reader.getString(KEY_INSTALL_REFFERER, "");
	}
}
