package com.tv.xeeng.dataLayer;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.R;
import com.tv.xeeng.dataLayer.game.BaseGame;
import com.tv.xeeng.dataLayer.utils.MD5;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.BaseGameRoom;
import com.tv.xeeng.gamedata.entity.Card;
import com.tv.xeeng.gamedata.entity.CardGameTable;
import com.tv.xeeng.gamedata.entity.ChatItemData;
import com.tv.xeeng.manager.Logger;
import com.tv.xeeng.manager.UserPreference;

public class BusinessRequester {
	static String MESSAGE_KEY = "v";

	static BusinessRequester instance;
	String className;

	public static BusinessRequester getInstance() {

		if (instance == null) {

			instance = new BusinessRequester();
		}

		return instance;
	}

	public BusinessRequester() {

		className = this.getClass().getSimpleName();
		Logger.getInstance().registerLogForClass(className);
	}

	public void login(String userName, String password, String macAddress) {

		UserPreference preference = UserPreference.sharePreference();
		preference.rememberUserLogin(userName);
		GameData.shareData().getMyself().password = password;

		JSONObject login = new JSONObject();
		StringBuffer content = new StringBuffer();

		content.append(MessagesID.LOGIN);
		content.append(NetworkUtils.N4_SEPERATOR);
		content.append(userName);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(MD5.toMD5(password));
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(CommonUtils.getAppVersion());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("8");
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(GameData.shareData().getPartnerId());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(NetworkUtils.MXH);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("0");
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(GameData.shareData().getRefCode());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		// content.append(Secure.getString(CustomApplication.shareApplication()
		// .getContentResolver(), Secure.ANDROID_ID)
		// + ";"
		// + CommonUtils.getDeviceName()
		// + ";android;"
		// + Build.VERSION.RELEASE + ";" + macAddress);

		content.append("android;" + Build.VERSION.RELEASE + ";"
				+ CommonUtils.getDeviceUniqueId() + ";"
				+ UserPreference.sharePreference().getInstallRefferer());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("0");
		try {
			login.put("v", content.toString());
			GameSocket.shareSocket().addRequestToQueue(login);
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	public void relogin(String userName, String password) {

		UserPreference preference = UserPreference.sharePreference();
		preference.rememberUserLogin(userName);
		GameData.shareData().getMyself().password = password;

		JSONObject login = new JSONObject();
		StringBuffer content = new StringBuffer();
		content.append(MessagesID.LOGIN);
		content.append(NetworkUtils.N4_SEPERATOR);
		content.append(userName);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append((password));
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(CommonUtils.getAppVersion());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("8");
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(GameData.shareData().getPartnerId());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(NetworkUtils.MXH);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("0");
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(GameData.shareData().getRefCode());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(CommonUtils.getDeviceName());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("0");
		try {
			login.put("v", content.toString());
			GameSocket.shareSocket().addRequestToQueue(login);
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	public void logout() {

		JSONObject logout = new JSONObject();
		try {

			GameSocket.shareSocket().isLoggedOut = true;
			logout.put(MESSAGE_KEY, MessagesID.LOGOUT);
			GameSocket.shareSocket().addRequestToQueue(logout);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void register(String userName, String password, String MacID,
			boolean isMale, String fullName, String personalId, String phone) {

		JSONObject register = new JSONObject();
		StringBuffer content = new StringBuffer();
		content.append(MessagesID.REGISTER_ACCOUNT);
		content.append(NetworkUtils.N4_SEPERATOR);
		content.append(userName);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(MD5.toMD5(password));
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(GameData.shareData().getPartnerId());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(6);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(MacID);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(isMale ? 1 : 0);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("");
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(NetworkUtils.MXH);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(GameData.shareData().getRefCode());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(CommonUtils.getDeviceName());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append("stop_stop");
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(CommonUtils.getRegisterCount());
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(fullName);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(personalId);
		content.append(NetworkUtils.ELEMENT_SEPERATOR);
		content.append(phone);
		try {
			register.put(MESSAGE_KEY, content);
			GameSocket.shareSocket().addRequestToQueue(register);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ping() {

		JSONObject ping = new JSONObject();
		try {
			ping.put("v", MessagesID.PING);
			GameSocket.shareSocket().addRequestToQueue(ping);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getActiveTableList(long roomId) {

		JSONObject table = new JSONObject();
		try {

			StringBuffer buffer = new StringBuffer();
			buffer.append(MessagesID.GET_TABLES);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(roomId);
			table.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(table);
		} catch (JSONException e) {

		}
	}

	public void changeGameSetting(long betCash, int maxPlayer, Bundle bundle) {

		JSONObject setting = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.SETTING_GAME);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getMatchId());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(maxPlayer);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(betCash);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		if (bundle != null) {

			switch (GameData.shareData().gameId) {
			case GameData.TLMN_TYPE:

				Boolean hidenPockerObj = bundle
						.getBoolean(CardGameTable.HIDE_POCKER_KEY);
				if (hidenPockerObj != null) {

					if (hidenPockerObj.booleanValue()) {

						buffer.append(1);
					} else {

						buffer.append(0);
					}
				} else {

					buffer.append(0);
				}
				break;

			default:
				break;
			}
		}
		try {

			setting.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(setting);
		} catch (JSONException e) {
		}
	}

	public void joinGame(long matchId) {
		Log.v("", " join game ");
		JSONObject joinGame = new JSONObject();

		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.MATCH_JOIN);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(matchId);
		try {

			joinGame.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(joinGame);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createNewGame(int tableIndex) {

		JSONObject newGame = new JSONObject();
		try {

			BaseGameRoom aroom = GameData.shareData().getGame()
					.getCurrentRoom();
			StringBuffer buffer = new StringBuffer();
			buffer.append(MessagesID.MATCH_NEW);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(tableIndex);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(aroom.getRoomId());
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(aroom.getBetCash());
			newGame.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(newGame);
		} catch (JSONException e) {

		}
	}

	public void setZone(int zoneId, int ver) {
		JSONObject setZone = new JSONObject();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(MessagesID.ENTER_ZONE);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(zoneId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(ver);
			setZone.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(setZone);

		} catch (JSONException e) {
			// TODO: handle exception
		}
	}

	public void getRewardList() {
		JSONObject getRewardList = new JSONObject();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(MessagesID.REWARD_LIST);
			getRewardList.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(getRewardList);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void getAllTable(long l, int mesId) {
		JSONObject getAllTable = new JSONObject();

		try {
			StringBuffer buffer = new StringBuffer();

			buffer.append(mesId);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(l);
			getAllTable.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(getAllTable);

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void getFriendList() {
		JSONObject getFriendList = new JSONObject();

		try {
			StringBuffer buffer = new StringBuffer();

			buffer.append(MessagesID.GET_FRIEND_LIST_BLOG);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append("0");

			getFriendList.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(getFriendList);

		} catch (JSONException e) {
			// TODO: handle exception
		}
	}

	public void readyGame(boolean isReady) {

		JSONObject ready = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.MATCH_READY);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getMatchId());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		if (isReady) {

			buffer.append(1);
		} else {

			buffer.append(0);
		}

		try {

			ready.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(ready);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startGame() {

		JSONObject startGame = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.MATCH_START);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getMatchId());
		try {
			startGame.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(startGame);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void leaveGame(long matchId) {

		JSONObject leavegame = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.MATCH_CANCEL);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(matchId);
		try {

			leavegame.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(leavegame);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getAvatarRequest(long playerId) {
		JSONObject getAvatarJsonObject = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {

			buffer.append(MessagesID.GET_AVATAR);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append("1");
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(playerId);

			getAvatarJsonObject.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(getAvatarJsonObject);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			buffer = null;
			getAvatarJsonObject = null;
		}

	}

	public void setAvatarRequest(long playerId) {
		JSONObject setAvatarReq = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.SET_AVATAR);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append("1");
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(playerId);

			setAvatarReq.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(setAvatarReq);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			buffer = null;
			setAvatarReq = null;
		}
	}

	public void getFriendRequests(int index) {
		JSONObject getFriendReqs = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_LIST_REQUEST_FRIEND);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(index);

			getFriendReqs.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(getFriendReqs);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			buffer = null;
			getFriendReqs = null;
		}
	}

	public void addFriend(long friendId, boolean accept) {

		JSONObject addFriend = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.ADD_FRIEND_BLOG);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(friendId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(accept ? 1 : 0);

			addFriend.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(addFriend);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {

			addFriend = null;
			buffer = null;
		}
	}

	public void chargingCard(String code, String serial, String serviceId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.NAPTIEN);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(serviceId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(serial.toUpperCase());
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(code);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append("0");

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getChargingSMSList() {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_CHARGING);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(GameData.shareData().getPartnerId());

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getEventsList() {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_EVENT);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getEventDetail(int eventId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_EVENT_DETAIL);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(eventId);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getList(int messageId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(messageId);
			switch (messageId) {
			case MessagesID.GET_CHARGING:
				buffer.append(NetworkUtils.N4_SEPERATOR);
				buffer.append(GameData.shareData().getPartnerId());
				break;
			default:
				break;
			}

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getMessageId(int mesId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(mesId);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getListDetailAchievement(int achievementId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.LIST_DETAIL);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(achievementId);

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void kickOut(long matchId, long userId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.KICK_OUT);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(matchId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(userId);

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void answerInvitaion(long requestId, long inviterId, boolean isAccept) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.ACCEP_MOI_CHOI);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(requestId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(inviterId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(isAccept ? 1 : 0);

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void sendMessage(long playerId, String title, String message) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.SEND_MESSAGE);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(playerId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(message);

			if (title != null && title.length() > 0) {
				buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
				buffer.append(title);
			}

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void chat(String msg) {

		GameData.shareData()
				.getGame()
				.getChatList()
				.add(new ChatItemData(GameData.shareData().getMyself().id,
						GameData.shareData().getMyself().character, msg));
		JSONObject chat = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.CHAT);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getMatchId());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(msg);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(0);
		try {

			chat.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(chat);
		} catch (JSONException e) {

		} finally {
			buffer = null;
			chat = null;
		}
	}

	public void submitGiftCode(String giftCode) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_GIFT_CODE);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(giftCode);

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void findFastPlayTable(int gameId, int levelId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.FAST_PLAY);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(gameId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(levelId);

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void invitePlayGame(long gameId, long cash, long playerId,
			long matchId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {

			buffer.append(MessagesID.INVITE_PLAYER);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(gameId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(cash);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(playerId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(matchId);

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void reconnect(int type, boolean inGame) throws JSONException,
			NullPointerException {
		JSONObject reconect = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.RECONNECTION);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(type);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(GameData.shareData().getMyself().character);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(GameData.shareData().getMyself().id);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		if (inGame) {

			buffer.append(GameData.shareData().getGame().getMatchId());
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			if (type == 5) {

			} else if (type == 4) {

				buffer.append(GameData.shareData().gameId);
				buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
				BaseGame bgame = GameData.shareData().getGame();
				bgame.getCurrentRoom();
				// long roomID =
				// GameData.shareData().getGame().getCurrentRoom().getRoomId();
				long roomID = 0;
				buffer.append(roomID);
			}
		} else {

			if (GameData.shareData().getMyself().password != null) {
				buffer.append(MD5
						.toMD5(GameData.shareData().getMyself().password));
			}

			if (type >= 2) { // at room list screen

				buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
				buffer.append(GameData.shareData().gameId);
			}

			if (type >= 3) { // at table list screen

				buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
				// GameData.shareData().getGame().getCurrentRoom().getRoomId();
				buffer.append(0);
			}
		}

		buffer.append(NetworkUtils.DIFF_ARRAY_SEPERATOR);
		buffer.append(1);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(7);

		reconect.put(MESSAGE_KEY, buffer);
		GameSocket.shareSocket().addRequestToQueue(reconect);
	}

	public void getRewardCash(String objectId, int typeId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_REWARD_CASH);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(objectId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(typeId);

			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void playTLMNTurn(long matchId, String cards) {

		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.MATCH_TURN);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(GameData.TLMN_TYPE);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(matchId);
			if (cards != null && cards.length() > 0) {

				buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
				buffer.append(cards);
			}

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
			Log.v("BusinessRequester", " -----> " + object.toString());

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

			object = null;
			buffer = null;
		}
	}

	public String betPackage(String str) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.FINISH_PUT_MONEY);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(str);
		return buffer.toString();
	}

	public void getFreePlayerList() {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.FREE_PLAYER);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getMatchId());
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getCurrentBetCash());
		buffer.append(NetworkUtils.N4_SEPERATOR);
		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}
	}

	public void sendProgress(int score) {

		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.LINE_EAT);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getCurrentTable().getId());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(score);
		try {

			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}
	}

	public void showCard(Card pCard) {

		if (pCard == null)
			return;

		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.QUAY_DIA);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getGame().getMatchId());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(pCard.serverValue);
		try {

			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}
	}

	public void acceptedInvite(long requestID, long invitedId, int isAccepted,
			int matchId) {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.ACCEPTED_INVITE);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(requestID);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(invitedId);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(isAccepted);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(matchId);
		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}

	}

	public void transferCash(long cashTransfer, String destName) {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.TRANSFER_CASH);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(destName);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(cashTransfer);
		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}

	}

	public void sendOffLineMessage() {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.OFFLINE_MESSAGE_LIST);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(GameData.shareData().getMyself().id);
		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}
	}

	public void getMessageDetail(int messageId) {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.READ_MASSAGE);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(messageId);
		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}

	}

	public void sendFastLogin(String macAddress) throws JSONException {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.FAST_LOGIN);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(macAddress);
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(GameData.shareData().getPartnerId());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(GameData.shareData().getRefCode());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(CommonUtils.getAppVersion());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		buffer.append(CommonUtils.getRegisterCount());
		buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
		// buffer.append(Secure.getString(CustomApplication.shareApplication()
		// .getContentResolver(), Secure.ANDROID_ID)
		// + ";"
		// + CommonUtils.getDeviceName()
		// + ";android;"
		// + Build.VERSION.RELEASE + ";" + macAddress);

		buffer.append("android;" + Build.VERSION.RELEASE + ";"
				+ CommonUtils.getDeviceUniqueId() + ";"
				+ UserPreference.sharePreference().getInstallRefferer());

		obj.put(MESSAGE_KEY, buffer.toString());
		GameSocket.shareSocket().addRequestToQueue(obj);
	}

	public void getRoomLevels(int zoneId) {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.GET_ROOM_LEVELS);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(zoneId);

		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {

		}
	}

	public void getTableList(int levelId) {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.GET_TABLES);
		buffer.append(NetworkUtils.N4_SEPERATOR);
		buffer.append(levelId);

		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {

		}
	}

	public void getAllNews() {

	}

	public void getNewsDetail(int eventId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_NEWS_DETAIL);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(eventId);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getUserInfo(long playerId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_USER_INFO);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(playerId);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getShopItems() {
		JSONObject obj = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessagesID.GET_SHOP_ITEMS);

		try {
			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}
	}

	public void purchaseShopItem(long userId, String shopItemId) {
		try {
			JSONObject obj = new JSONObject();
			StringBuffer buffer = new StringBuffer();

			buffer.append(MessagesID.PURCHASE_STORE_ITEM);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(userId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(shopItemId);

			obj.put(MESSAGE_KEY, buffer.toString());
			GameSocket.shareSocket().addRequestToQueue(obj);
		} catch (JSONException e) {
		}
	}

	public void createNewTable(int zoneId, int levelId, long moneyBet,
			int maxPlayers, String tableName) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.CREATE_NEW_TABLE);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(zoneId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(levelId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(moneyBet);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(maxPlayers);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(tableName);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void sendPrivateMessage(long id, CharSequence title,
			CharSequence content) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.SEND_PRIVATE_MESSAGE);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(id);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(title);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(content);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	@Deprecated
	public void getAllPrivateMessages() {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_ALL_PRIVATE_MESSAGES);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getPrivateChatMessages(long targetId, int offset, int limit) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_CHAT_PRIVATE_MESSAGES);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(targetId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(offset);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(limit);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllUserLevels() {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_ALL_USER_LEVELS);
			object.put(MESSAGE_KEY, buffer);

			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void removeSocialFriend(long id) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.REMOVE_SOCIAL_FRIEND);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(id);

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getPrivateMessage(long id) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.GET_PRIVATE_MESSAGE);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(id);

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void resetPassword(String username, String personalId,
			String phoneNumber, String newPassword) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.RESET_PASSWORD);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(username);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(personalId);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(phoneNumber);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(newPassword);

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public boolean getUserAvatar(long userId) {
		String url = "http://avatar."
				+ CustomApplication.shareApplication().getResources()
						.getString(R.string.domain_name) + "/avatar/" + userId
				+ "/avatar.jpg";

		try {
			URLConnection con = (new URL(url)).openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(10000);

			InputStream is = con.getInputStream();

			Options opts = new Options();
			opts.inPreferredConfig = Config.ARGB_8888;

			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);

			CommonUtils.addBitmapToCache(userId, bitmap);
			return true;
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return false;
		}
	}

	public void receiveFreeGold() {
		getMessageId(MessagesID.RECEIVE_FREE_GOLD);
	}

	public void getInventory() {
		getMessageId(MessagesID.GET_INVENTORY);
	}

	public void getEventHistory(long eventId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		if (eventId > 0) {

			buffer.append(MessagesID.GET_EVENT_HISTORY);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(eventId);
		} else {
			buffer.append(MessagesID.GET_ROULLETE_EVENT_HISTORY);
		}
		try {
			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getMonthlyEventList() {
		getMessageId(MessagesID.GET_MONTHLY_EVENT_LIST);
	}

	public void useInventoryItem(String itemCode) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.USE_INVENTORY_ITEM);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(itemCode);

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getCashShopItems() {
		getMessageId(MessagesID.GET_CASH_SHOP_ITEMS);
	}

	public void purchaseCashShopItem(String itemCode) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.PURCHASE_CASH_SHOP_ITEM);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(itemCode);

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void participateEvent(int messageId, long eventId) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(messageId);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(eventId);

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getRouletteGameInfo() {
		getMessageId(MessagesID.GET_ROULETTE_GAME_INFO);
	}

	public void endRoullete() {
		getMessageId(MessagesID.END_ROULETTE);
	}

	public void startRoullete(String value, String type) {
		JSONObject object = new JSONObject();
		StringBuffer buffer = new StringBuffer();

		try {
			buffer.append(MessagesID.START_ROULETTE);
			buffer.append(NetworkUtils.N4_SEPERATOR);
			buffer.append(value);
			buffer.append(NetworkUtils.ELEMENT_SEPERATOR);
			buffer.append(type);

			object.put(MESSAGE_KEY, buffer);
			GameSocket.shareSocket().addRequestToQueue(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			object = null;
			buffer = null;
		}
	}

	public void getRouletteGuide() {
		getMessageId(MessagesID.GET_ROULETTE_GUIDE);
	}
}
