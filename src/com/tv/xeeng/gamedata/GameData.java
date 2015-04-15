package com.tv.xeeng.gamedata;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.AssetManager;
import android.util.Xml;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.game.BaseGame;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.itemdata.RoomLevelItemData;
import com.tv.xeeng.gui.itemdata.UserLevelItemData;
import com.tv.xeeng.manager.UserPreference;

public class GameData {

	/*
	 * game type
	 */

	public static final int ALTP_TYPE = 3;
	public static final int BACAY_TYPE = 11;
	public static final int PHOM_TYPE = 4;
	public static final int TLMN_TYPE = 5;
	public static final int PIKACHU_TYPE = 31;
	public static final int BAUCUA_TYPE = 12;
	public static final int SAM_TYPE = 37;
	public static final int MAU_BINH_TYPE = 99;
	public static final int MAX_CAPACITY = 24;

	private static GameData INSTANCE;

	public String passwordMD5;

	public String activePhone;
	public String activeSystax;
	public String activeUserNotifyMsg;
	public int gameId;
	public boolean isRejectAllInvitaion = false;
	public int currentTableNumber;
	public long minBetCash;
	public boolean isInBackground = false;
	public boolean isReadyGameTimeOut = false;
	public boolean isShowChargingActivity = false;
	public boolean isShowActiveAccountDialog = false;
	public String ads;
	public long currentRoomId;
	public ArrayList<UserLevelItemData> allUserLevels;
	public RoomLevelItemData currentRoomLevel;
	public boolean isNeedUpdate;
	public String updateLink;
	public String updateDescription;

	private Player myself;
	private boolean isGuest;

	private BaseGame mgame;

	private String mPartnerId, mRefCode;

	public static GameData shareData() {

		if (INSTANCE == null) {
			INSTANCE = new GameData();
		}

		return INSTANCE;
	}

	public static void initialize() {

		INSTANCE = new GameData();
	}

	/*
	 * singleton
	 */
	private GameData() {

		if (getMyself() == null) {
			setMyself(new Player());
		}
		isShowChargingActivity = false;
		isRejectAllInvitaion = UserPreference.sharePreference()
				.isAutoDenyInvitation();
	}

	/*
	 * public methods
	 */
	public BaseGame getGame() {
		// Log.v("BASEGAME", " ---> mgame " + mgame);
		return mgame;
	}

	public void setGame(BaseGame agame) {

		if (agame == null) {

			if (mgame != null) {

				mgame.clean();
				mgame = null;
			}
		}
		if (agame == mgame)
			return;
		mgame = agame;
	}

	public void update(long _userId, String _userName, long _cash, int _sex,
			long _avatarId) {

		// Log.d(getClass().getSimpleName(), "username: " + _userName);

		activePhone = null;
		activeSystax = null;

		getMyself().character = _userName;
		getMyself().id = _userId;
		getMyself().cash = _cash;
		getMyself().avatarId = _avatarId;

	}

	public boolean isActived() {

		if (activePhone == null && activeSystax == null) {

			return true;
		} else {

			return false;
		}
	}

	public void clean() {

		INSTANCE = null;
	}

	public Player getMyselfFromPlayerlist() {
		if (mgame != null) {
			for (Player player : mgame.getPlayerList()) {
				if (player.id == myself.id) {
					return player;
				}
			}
		}
		return null;
	}

	/*
     */
	public Player getMyself() {
		return myself;
	}

	public void setMyself(Player myself) {
		this.myself = myself;
	}

	public String getPartnerId() {

		if (mPartnerId == null) {
			getConfigs();

			// Get partner id according to Install Referrer params
			String params = UserPreference.sharePreference()
					.getInstallRefferer();

			try {
				Map<String, List<String>> map = CommonUtils
						.parseUriParams(params);

				List<String> values = map.get("c");

				if (values != null) {
					mPartnerId = values.get(0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return mPartnerId;
	}

	public String getRefCode() {

		if (mRefCode == null) {

			getConfigs();
		}

		return mRefCode;
	}

	private void getConfigs() {

		try {

			AssetManager assetManager = CustomApplication.shareApplication()
					.getAssets();
			InputStream input = assetManager.open("configs/configs.xml");
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(input, null);
			int eventType = parser.getEventType();
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:

					name = parser.getName();
					if (name.equalsIgnoreCase("partner_id")) {
						mPartnerId = parser.nextText();
					} else if (name.equalsIgnoreCase("ref_id")) {

						mRefCode = parser.nextText();
					} else if (name.equalsIgnoreCase("debuggable")) {

						// Logger.getInstance().enableLog("1".equalsIgnoreCase(parser.nextText()));
					}
					break;
				case XmlPullParser.END_TAG:

					break;
				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isGuest() {
		return isGuest;
	}

	public void setGuest(boolean isGuest) {
		this.isGuest = isGuest;
	}

}
