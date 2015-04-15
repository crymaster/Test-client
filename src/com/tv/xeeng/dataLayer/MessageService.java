package com.tv.xeeng.dataLayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.game.BaseGame;
import com.tv.xeeng.dataLayer.game.TLMNGame;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.itemdata.AchievementItem;
import com.tv.xeeng.gui.itemdata.CashShopItemData;
import com.tv.xeeng.gui.itemdata.ChargingItemData;
import com.tv.xeeng.gui.itemdata.ChargingItemData.ChargingItemType;
import com.tv.xeeng.gui.itemdata.EventHistoryItemData;
import com.tv.xeeng.gui.itemdata.EventItemData;
import com.tv.xeeng.gui.itemdata.FriendRequestItemData;
import com.tv.xeeng.gui.itemdata.InventoryItemData;
import com.tv.xeeng.gui.itemdata.MailItemData;
import com.tv.xeeng.gui.itemdata.MessageItemData;
import com.tv.xeeng.gui.itemdata.MonthlyEventItemData;
import com.tv.xeeng.gui.itemdata.RewardItemData;
import com.tv.xeeng.gui.itemdata.RoomLevelItemData;
import com.tv.xeeng.gui.itemdata.ShopItemData;
import com.tv.xeeng.gui.itemdata.SlotMachineComboItemData;
import com.tv.xeeng.gui.itemdata.TopPlayerItem;
import com.tv.xeeng.gui.itemdata.UserLevelItemData;
import com.tv.xeeng.manager.Logger;
import com.tv.xeeng.manager.UserPreference;

public class MessageService {

	/*
	 * 
	 * BROADCAST RECEIEVER CONSTANTS INTENT DESCRIPTION
	 */
	public static String INTENT_NAVIGATE_LOGIN_ACTIVITY = "com.vmgames.login.activity";
	public static String INTENT_LOGIN = "com.vmgames.login";
	public static String INTENT_FAST_LOGIN = "com.xeeng.fast.login";
	public static String INTENT_LOGOUT = "com.vmgames.logout";
	public static String INTENT_REGISTER = "com.vmgames.register";
	public static String INTENT_NETWORK_DEVICE_PROBLEM = "com.vmgames.network.device.prob";
	public static String INTENT_NEED_RECONNECTION = "com.vmgames.need.reconnection";
	public static String INTENT_RECONNECTION_RESPONSE = "com.vmgames.reconnection.resp";
	public static String INTENT_RECONECTING = "com.vmgames.recreate.socket";

	public static String INTENT_ACTIVE_ACCOUNT = "com.vmgames.active.account";
	public static String INTENT_NETWORK_ERROR = "com.vmgames.network.error";

	public static String INTENT_ENTER_ZONE = "com.vmgames.enter.zone";
	public static String INTENT_GET_TABLES = "com.vmgames.get.table";
	public static String INTENT_GET_PIKA_TOPIC = "com.vmgames.get.pika.topic";
	public static String INTENT_GET_PIKA_DETAIL_TOPIC = "com.vmgames.get.pika.detail.topic";

	public static String INTENT_CHAT_GAME = "com.vmgames.chat.game";
	public static String INTENT_NEW_MATCH = "com.vmgames.new.game";
	public static String INTENT_JOIN_MATCH = "com.vmgames.join.game";
	public static String INTENT_LEAVE_MATCH = "com.vmgames.leave.game";
	public static String INTENT_USER_JOINED_MATCH = "com.vmgames.joined.match";
	public static String INTENT_USER_IS_KICKED = "com.vmgames.user.is.kicked";
	public static String INTENT_GAME_START = "com.vmgames.game.start";

	public static String INTENT_ALTP_QUESTION = "com.vmgames.altp.question";
	public static String INTENT_ALTP_HELP = "com.vmgames.altp.help";
	public static String INTENT_ALTP_ANSWER = "com.vmgames.altp.answer";
	public static String INTENT_INVITE_PLAY = "com.xeeng.invite";
	/**
	 * if message return success then info include status & readyPlayer id else
	 * info include status & message
	 */
	public static String INTENT_USER_READY_MATCH = "com.vmgames.user.ready.match";
	public static String INTENT_SETTING_MATCH = "com.vmgames.setting.match";
	public static String INTENT_END_MATCH = "com.vmgames.end.match";

	public static String INTENT_REWARD_LIST = "com.vmgames.getRewardList";
	public static String INTENT_GET_REWARD_CASH = "com.vmgames.getRewardCash";

	public static String INTENT_GET_PLAYERS_FREE = "com.vmgames.getPlayerFree";
	public static String INTENT_GET_FRIENDS_LIST_BLOG = "com.vmgames.getFriendsListBlog";
	public static String INTENT_GET_FRIENDS_ONLINE_LIST_BLOG = "com.xeeng.getFriendsOnlineListBlog";

	public static String INTENT_GET_FRIEND_REQUESTS = "com.vmgamges.getFriendRequests";
	public static String INTENT_GET_AVATAR = "com.vmgames.getAvatar";

	public static String INTENT_REQUEST_FRIEND = "com.vmgames.requestFriend";
	public static String INTENT_CARD_CHARGING_RESULT = "com.vmgames.card.charging.result";
	public static String INTENT_GET_SMS_CHARGING_LIST = "com.vmgames.sms.charging.list";
	public static String INTENT_GET_EVENTS_LIST = "com.vmgames.events.list";
	public static String INTENT_GET_EVENT_DETAILS = "com.vmgames.event.detail";

	public static String INTENT_ADVERTISEMENT = "com.vmgames.advertisement";
	public static String INTENT_GET_LIST_ACHIEVEMENT = "com.vmgames.getListAchievement";
	public static String INTENT_GET_TOP_PLAYER = "com.vmgames.getTopPlayer";

	public static String INTENT_RECONNECTION = "com.vmgames.reconnection";

	public static String INTENT_GET_POKER = "com.vmgames.getPoker";
	public static String INTENT_MATCH_TURN = "com.vmgames.matchTurn";
	public static String INTENT_INVITE_PLAY_NEW_GAME = "com.vmgames.invite.play.new.game";
	public static String INTENT_SEND_INVITATION_SUCCESSFUL = "com.vmgames.send.invitation.successful";

	public static String INTENT_GIFT_CODE = "com.vmgames.giftcode";
	public static String INTENT_TROUBLE_FAST_PLAY = "com.vmgames.trouble.fast.play";

	public static String INTENT_RECEIVE_REWARD = "com.vmgames.receiveReward";

	public static String INTENT_FLIP_OUT_CARD = "com.vmgames.flip.out.card";

	public static String INTENT_BOC_BAI = "com.vmgames.phom.boc.bai";
	public static String INTENT_AN_PHOM = "com.vmgames.phom.an.phom";
	public static String INTENT_GUI_PHOM = "com.vmgames.phom.gui.phom";
	public static String INTENT_HA_PHOM = "com.vmgames.phom.ha.phom";
	public static String INTENT_GET_TRANSFER_CASH_MESSAGE = "com.xeeng.getTransferCash";
	public static String INTENT_GET_OFFLINE_MESSAGE = "com.xeeng.getOfflineMss";

	public static String INTENT_GET_MESSAGE_DETAILS = "com.xeeng.message.detail";
	public static String INTENT_PROGRESS_PIKACHU = "com.vmgames.pika.progress";

	public static String INTENT_GET_ROOM_LEVELS = "com.xeeng.message.getTableLevels";

	public static final String INTENT_GET_ALL_NEWS = "com.xeeng.getAllNEWS";
	public static final String INTENT_GET_NEWS_DETAIL = "com.xeeng.getNewsDetail";

	public static final String INTENT_GET_USER_INFO = "com.xeeng.getUserInfo";

	public static final String INTENT_GET_SHOP_ITEMS = "com.xeeng.v2.getAllShopItems";
	public static final String INTENT_CREATE_NEW_TABLE = "com.xeeng.createNewTable";
	public static final String INTENT_PURCHASE_SHOP_ITEM = "com.xeeng.v2.purchaseShopItem";
	public static final String INTENT_SEND_PRIVATE_MESSAGE = "com.xeeng.v2.sendPrivateMessage";
	public static final String INTENT_GET_ALL_PRIVATE_MESSAGES = "com.xeeng.v2.getAllPrivateMessage";
	public static final String INTENT_GET_ALL_USER_LEVELS = "com.xeeng.v2.getAllUserLevels";
	public static final String INTENT_REMOVE_SOCIAL_FRIEND = "com.xeeng.v2.removeSocialFriend";
	public static final String INTENT_GET_PRIVATE_MESSAGE = "com.xeeng.v2.getPrivateMessage";
	public static final String INTENT_RESET_PASSWORD = "com.xeeng.v2.resetPassword";
	public static final String INTENT_MULTIPLE_LOGIN = "com.xeeng.v2.multipleLogin";
	public static final String INTENT_RECEIVE_FREE_GOLD = "com.xeeng.v2.receiveFreeGold";
	public static final String INTENT_ACCEPTED_INVITE = "com.xeeng.v2.acceptedInvite";

	public static final String INTENT_CONNECTION_SLOW = "com.xeeng.v2.connectionSlow";
	public static final String INTENT_GET_INVENTORY = "com.xeeng.v2.getInventory";
	public static final String INTENT_GET_EVENT_HISTORY = "com.xeeng.v2.getEventHistory";
	public static final String INTENT_USE_INVENTORY_ITEM = "com.xeeng.v2.useInventoryItem";
	public static final String INTENT_RECEIVE_EVENT_ITEM = "com.xeeng.v2.receiveEventItem";
	public static final String INTENT_GET_MONTHLY_EVENT_LIST = "com.xeeng.v2.getMonthlyEventList";
	public static final String INTENT_GET_CASH_SHOP_ITEMS = "com.xeeng.v2.getCashShopItems";
	public static final String INTENT_PURCHASE_CASH_SHOP_ITEM = "com.xeeng.v2.purchaseCashShopItem";

	public static final String INTENT_PARTICIPATE_JOIN_ITEMS_EVENT = "com.xeeng.v2.participateJoinItemsEvent";
	public static final String INTENT_PARTICIPATE_FLIP_CARDS_EVENT = "com.xeeng.v2.participateFlipCardsEvent";

	public static final String INTENT_GET_ROULETTE_GAME_INFO = "com.xeeng.v2.getRoulleteGameInfo";
	public static final String INTENT_START_ROULLETE = "com.xeeng.v2.startRoulete";
	public static final String INTENT_END_ROULLETE = "com.xeeng.v2.endRoulete";
	public static final String INTENT_GET_ROULETTE_GUIDE = "com.xeeng.v2.getRoulleteGuide";

	public static final String INTENT_GET_PRIVATE_CHAT_MESSAGES = "com.xeeng.v2.getPrivateMessages";

	public static void initDataGame(int gameID) {

		Log.v(" MESSAGE SERVICE ", " game id: " + gameID);
		if (gameID == GameData.TLMN_TYPE) {

			TLMNGame game = new TLMNGame();
			GameData.shareData().setGame(game);
			GameData.shareData().gameId = gameID;
		}
	}

	/*
	 * singleton
	 */
	static MessageService instance;

	/*
	 * private instance
	 */

	private String className;
	private LocalBroadcastManager broadcastManager;

	public static MessageService getInstance() {

		if (instance == null) {

			instance = new MessageService();
		}

		return instance;
	}

	private MessageService() {

		className = this.getClass().getSimpleName();
		Logger.getInstance().registerLogForClass(className);
		try {
			broadcastManager = LocalBroadcastManager
					.getInstance(CustomApplication.shareApplication()
							.getApplicationContext());
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void processMessage(ArrayList<JSONObject> messageArray) {

		for (JSONObject message : messageArray) {

			final String[] source;
			try {
				source = NetworkUtils.stringSplit(message.getString("v"),
						NetworkUtils.N4_SEPERATOR);
				Logger.getInstance().info(this,
						" ------> RESPONE: " + message.toString());
				if (source == null || source.length < 1) {

					// Logger.getInstance().warn(className,
					// "Message has no body");
					continue;
				}
				String[] first = NetworkUtils.stringSplit(source[0],
						NetworkUtils.ELEMENT_SEPERATOR);
				final int mId = Integer.parseInt(first[0]);
				final boolean status = "1".equalsIgnoreCase(first[1]);
				Log.v("", " -----> Game Socket MessageID = " + mId);
				switch (mId) {
				case MessagesID.LOGIN:
					processLoginMessage(source[1], status);
					break;

				case MessagesID.FAST_LOGIN:
					processFastLoginMessage(source[1], status);
					break;

				case MessagesID.LOGOUT:
					processLogoutMessage(source[1], status);
					break;
				//
				case MessagesID.REGISTER_ACCOUNT:
					processRegisterMessage(source[1], status);
					break;

				case MessagesID.QUANG_CAO:
					processAdsMessage(source[1], status);
					break;
				case MessagesID.ENTER_ZONE:

					if (GameData.shareData().gameId == GameData.TLMN_TYPE) {

						TLMNGame game = new TLMNGame();
						GameData.shareData().setGame(game);

					} 
					if (GameData.shareData().getGame() != null) {

						GameData.shareData().getGame()
								.processEnterZoneMessage(source[1], status);
					} else {
						Log.e(className, "getGame() is null");
					}
					break;

				case MessagesID.GET_TABLES:
					GameData.shareData().getGame()
							.processGetTableMessage(source[1], status);
					break;

				case MessagesID.CHAT:
					if (BaseXeengGame.INSTANCE != null) {
						BaseXeengGame.INSTANCE
								.runOnUpdateThread(new Runnable() {
									public void run() {

										GameData.shareData()
												.getGame()
												.processChatMessage(source[1],
														status);
									}
								});
					}
					break;

				case MessagesID.SETTING_GAME:

					if (BaseXeengGame.INSTANCE != null) {

						BaseXeengGame.INSTANCE
								.runOnUpdateThread(new Runnable() {
									public void run() {

										GameData.shareData()
												.getGame()
												.processSettingMatchMessage(
														source[1], status);
									}
								});
					}
					break;

				case MessagesID.MATCH_NEW:
					GameData.shareData().getGame()
							.processCreateNewTableMessage(source[1], status);
					break;

				case MessagesID.MATCH_JOIN:
					Log.v("", " -----> message id: match join");
					GameData.shareData().getGame()
							.processJoinMatchMessage(source[1], status);
					break;

				case MessagesID.MATCH_JOINED:
					GameData.shareData().getGame()
							.processJoinedMatchMessage(source[1], status);
					break;

				case MessagesID.MATCH_END:
					Log.v("MessageService", " ---> MATCH_END");
					GameData.shareData().getGame()
							.processEndMatchMessage(source[1], status);
					// IMPORTANT (TUNGHX)
					// TODO TUNGHX
					BaseGame game = GameData.shareData().getGame();
					game.setHasPreviousMatch(true);
					// game.resetActionQueue();
					break;

				// Game Socket MessageID
				case MessagesID.MATCH_CANCEL:
					GameData.shareData().getGame()
							.processUserLeaveMessage(source[1], status);
					break;

				case MessagesID.OUT:
					GameData.shareData().getGame()
							.processUserIsKickedMessage(source[1], status);
					break;

				case MessagesID.MATCH_READY:
					GameData.shareData().getGame()
							.processReadyMatchMessage(source[1], status);
					break;
				case MessagesID.TRANSFER_CASH:
					procesGetMoneyTransferMessage(source[1], status);
					break;
				case MessagesID.OFFLINE_MESSAGE_LIST:
					processGetMessageList(source[1], status);
					break;
				case MessagesID.REWARD_LIST:
					processGetRewardListMessage(source[1], status);
					break;
				case MessagesID.GET_FRIEND_LIST_BLOG:
					processGetFriendListBlogMessage(source[1], status);
					processGetFriendOnlineListBlogMessage(source[1], status);
					break;
				case MessagesID.FREE_PLAYER:
					processGetPlayerFreeMessage(source[1], status);
					break;
				case MessagesID.GET_LIST_REQUEST_FRIEND:
					processGetFriendRequests(source[1], status);
					break;
				case MessagesID.GET_AVATAR:
					processGetAvatarPlayer(source[1], status);
					break;
				case MessagesID.ADD_FRIEND_BLOG:
					if (BaseXeengGame.INSTANCE != null) {
						BaseXeengGame.INSTANCE
								.runOnUpdateThread(new Runnable() {
									public void run() {

										long playerId = -1;
										String playerName = null;
										if (status) {
											String addFriendInfo[] = NetworkUtils
													.stringSplit(
															source[1],
															NetworkUtils.ELEMENT_SEPERATOR);
											if (addFriendInfo.length > 2) {

												playerId = Long
														.parseLong(addFriendInfo[1]);
												playerName = addFriendInfo[2];
											} else {

											}
										}

										int isAdd = 0;
										try {

											isAdd = Integer.parseInt(source[1]);
										} catch (NumberFormatException nfe) {
										}
										BaseXeengGame.INSTANCE
												.showFriendRequest(status,
														playerId, playerName,
														isAdd > 0 ? true
																: false,
														source[1]);
									}
								});
					} else {
						Intent result = new Intent(INTENT_REQUEST_FRIEND);
						result.putExtra(NetworkUtils.MESSAGE_STATUS, status);
						result.putExtra(NetworkUtils.MESSAGE_INFO, source[1]);
						broadcastManager.sendBroadcast(result);
					}
					break;
				case MessagesID.NAPTIEN:

					Intent result = new Intent(INTENT_CARD_CHARGING_RESULT);

					result.putExtra(NetworkUtils.MESSAGE_STATUS, status);
					result.putExtra(NetworkUtils.MESSAGE_INFO, source[1]);

					broadcastManager.sendBroadcast(result);

					break;
				case MessagesID.GET_CHARGING:
					processGetSMSChargingList(source[1], status);
					break;
				case MessagesID.GET_EVENT:
					processGetEventsList(source[1], status);
					break;
				case MessagesID.GET_ALL_NEWS:
					processGetAllNews(source[1], status);
					break;
				case MessagesID.GET_EVENT_DETAIL:
					processGetEventDetail(source[1], status);
					break;
				case MessagesID.GET_NEWS_DETAIL:
					processGetNewsDetail(source[1], status);
					break;
				case MessagesID.READ_MASSAGE:
					processGetMessageDetail(source[1], status);
					break;
				case MessagesID.LIST_ACHIEVEMENT:
					procesGetListAchievement(source[1], status);
					break;
				case MessagesID.GET_BEST_PLAYER:
				case MessagesID.GET_RICHESTS:
					processGetTopPlayer(source[1], status, mId);
					break;
				case MessagesID.LIST_DETAIL:
					processGetListDetailAchievement(source[1], status);
					break;
				case MessagesID.INVITE_PLAYER:
					processInviteNewGame(source[1], status);
					break;
				case MessagesID.GET_GIFT_CODE:

					Intent intent = new Intent(INTENT_GIFT_CODE);
					intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

					if (status) {
						intent.putExtra(NetworkUtils.MESSAGE_INFO,
								Long.parseLong(source[1]));
					} else {
						intent.putExtra(NetworkUtils.MESSAGE_INFO, source[1]);
					}

					broadcastManager.sendBroadcast(intent);

					break;
				case MessagesID.FAST_PLAY:
					Log.d("Fast Play", "Status: " + status);
					if (!status) {
						Intent fastPlay = new Intent(INTENT_TROUBLE_FAST_PLAY);
						fastPlay.putExtra(NetworkUtils.MESSAGE_STATUS, status);
						if (source.length > 0) {
							fastPlay.putExtra(NetworkUtils.MESSAGE_INFO,
									source[1]);
						}
						broadcastManager.sendBroadcast(fastPlay);
					}
					break;

				case MessagesID.RECONNECTION:

					if (BaseXeengGame.INSTANCE != null) {

						BaseXeengGame.INSTANCE
								.runOnUpdateThread(new Runnable() {
									public void run() {

										if (GameData.shareData().getGame() != null) {

											GameData.shareData()
													.getGame()
													.processReconnectionMessage(
															source[1], status);
										}
									}
								});
					} else {
						if (status) {
							if (GameData.shareData().getGame() != null) {
								GameData.shareData()
										.getGame()
										.processReconnectionMessage(source[1],
												status);
							}
						} else {
							Intent i = new Intent(INTENT_RECONNECTION);
							i.putExtra(NetworkUtils.MESSAGE_STATUS, status);
							broadcastManager.sendBroadcast(i);
						}
					}
					break;
				case MessagesID.GET_REWARD_CASH:
					Intent getRewardCash = new Intent(INTENT_GET_REWARD_CASH);
					getRewardCash.putExtra(NetworkUtils.MESSAGE_STATUS, status);

					if (status) {
						getRewardCash.putExtra(NetworkUtils.MESSAGE_INFO,
								Long.parseLong(source[1]));
					} else {
						getRewardCash.putExtra(NetworkUtils.MESSAGE_INFO,
								source[1]);
					}

					broadcastManager.sendBroadcast(getRewardCash);

					break;
				case MessagesID.GET_ROOM_LEVELS:
					processGetTableLevels(source[1], status);
					break;
				case MessagesID.GET_USER_INFO:
					processGetUserInfo(source[1], status);
					break;
				case MessagesID.CREATE_NEW_TABLE:
					GameData.shareData().getGame()
							.processCreateNewTableMessage(source[1], status);
					break;
				case MessagesID.GET_SHOP_ITEMS:
					processGetShopItems(source[1], status);
					break;
				case MessagesID.PURCHASE_STORE_ITEM:
					processPurchaseShopItem(source[1], status);
					break;
				case MessagesID.SEND_PRIVATE_MESSAGE:
					processSendPrivateMessage(source[1], status);
					break;
				case MessagesID.GET_ALL_PRIVATE_MESSAGES:
					try {
						processGetAllPrivateMessages(source[1], status);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case MessagesID.GET_ALL_USER_LEVELS:
					processGetAllUserLevels(source[1], status);
					break;
				case MessagesID.REMOVE_SOCIAL_FRIEND:
					processRemoveSocialFriend(source[1], status);
					break;
				case MessagesID.GET_PRIVATE_MESSAGE:
					try {
						processGetPrivateMessage(source[1], status);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case MessagesID.RESET_PASSWORD:
					processResetPasswordMessage(source[1], status);
					break;
				case MessagesID.MULTIPLE_LOGIN:
					processMultipleLoginMessage(source[1], status);
					break;
				case MessagesID.RECEIVE_FREE_GOLD:
					processReceiveFreeGoldMessage(source[1], status);
					break;
				case MessagesID.ACCEPTED_INVITE:
					processAcceptedInviteMessage(source[1], status);
					break;

				// case MessagesID.BAO_SAM:
				// if (GameData.shareData().getGame() != null) {
				//
				// }
				// break;
				case MessagesID.GET_INVENTORY:
					processGetInventoryMessage(source[1], status);
					break;
				case MessagesID.GET_EVENT_HISTORY:
				case MessagesID.GET_ROULLETE_EVENT_HISTORY:
					processGetEventHistoryMessage(source[1], status);
					break;
				case MessagesID.USE_INVENTORY_ITEM:
					processUseInventoryItemMessage(source[1], status);
					break;
				case MessagesID.PARTICIPATE_JOIN_ITEMS_EVENT:
					processParticipateJoinItemsEventMessage(source[1], status);
					break;
				case MessagesID.PARTICIPATE_FLIP_CARDS_EVENT:
					processParticipateFlipCardsEventMessage(source[1], status);
					break;
				case MessagesID.RECEIVE_EVENT_ITEM:
					processReceiveEventItemMessage(source[1], status);
					break;
				case MessagesID.GET_MONTHLY_EVENT_LIST:
					processGetMonthlyEventListMessage(source[1], status);
					break;
				case MessagesID.GET_CASH_SHOP_ITEMS:
					processGetCashShopItemsMessage(source[1], status);
					break;
				case MessagesID.PURCHASE_CASH_SHOP_ITEM:
					processPurchaseCashShopItemMessage(source[1], status);
					break;
				case MessagesID.GET_ROULETTE_GAME_INFO:
					processGetRouleteGameInfoMessage(source[1], status);
					break;
				case MessagesID.START_ROULETTE:
					processStartRoulete(source[1], status);
					break;
				case MessagesID.END_ROULETTE:
					processEndRoulete(source[1], status);
					break;
				case MessagesID.GET_ROULETTE_GUIDE:
					processGetRouletteGuide(source[1], status);
					break;
				case MessagesID.GET_CHAT_PRIVATE_MESSAGES:
					processGetPrivateChatMessages(source[1], status);
					break;
				default:
					if (source.length < 2) {
						Log.e(getClass().getSimpleName(), "source[1] is null");
					} else {

						if (GameData.shareData().getGame() != null) {
							GameData.shareData()
									.getGame()
									.processSpecificMessage(mId, source[1],
											status);
						}
					}
					break;
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}

	private void processGetPrivateChatMessages(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_PRIVATE_CHAT_MESSAGES);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		ArrayList<MailItemData> chatMessages = new ArrayList<MailItemData>();

		for (String str : NetworkUtils.stringSplit(content,
				NetworkUtils.ARRAY_SEPERATOR)) {
			String[] params = NetworkUtils.stringSplit(str,
					NetworkUtils.ELEMENT_SEPERATOR);

			SimpleDateFormat formatter = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm");

			try {
				chatMessages.add(new MailItemData(Long.parseLong(params[0]),
						Long.parseLong(params[1]), params[2], params[3],
						formatter.parse(params[4]).getTime()));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Collections.reverse(chatMessages);
		intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
				chatMessages);

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetRouletteGuide(String string, boolean status) {
		Intent intent = new Intent(INTENT_GET_ROULETTE_GUIDE);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] itemStrs = NetworkUtils.stringSplit(string,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<SlotMachineComboItemData> list = new ArrayList<SlotMachineComboItemData>();

			for (String str : itemStrs) {
				String[] info = NetworkUtils.stringSplit(str,
						NetworkUtils.ELEMENT_SEPERATOR);
				list.add(new SlotMachineComboItemData(Integer.parseInt(info[0]
						.trim()), info[1], info[2], info[3]));
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO, list);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, string);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetRouleteGameInfoMessage(String string, boolean status) {
		Intent intent = new Intent(INTENT_GET_ROULETTE_GAME_INFO);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, string);

		broadcastManager.sendBroadcast(intent);
	}

	private void processStartRoulete(String string, boolean status) {
		Intent intent = new Intent(INTENT_START_ROULLETE);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, string);

		broadcastManager.sendBroadcast(intent);
	}

	private void processEndRoulete(String string, boolean status) {
		Intent intent = new Intent(INTENT_END_ROULLETE);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, string);

		broadcastManager.sendBroadcast(intent);
	}

	private void processParticipateFlipCardsEventMessage(String content,
			boolean status) {
		Intent intent = new Intent(INTENT_PARTICIPATE_FLIP_CARDS_EVENT);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processPurchaseCashShopItemMessage(String content,
			boolean status) {
		Intent intent = new Intent(INTENT_PURCHASE_CASH_SHOP_ITEM);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetMonthlyEventListMessage(String content,
			boolean status) {
		Intent intent = new Intent(INTENT_GET_MONTHLY_EVENT_LIST);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] items = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);

			ArrayList<MonthlyEventItemData> data = new ArrayList<MonthlyEventItemData>();
			for (String itemStr : items) {
				String[] itemData = NetworkUtils.stringSplit(itemStr,
						NetworkUtils.ELEMENT_SEPERATOR);
				String[] components = NetworkUtils
						.stringSplit(itemData[4], ";");

				String type = itemData[5];

				if (type.equalsIgnoreCase(MonthlyEventItemData.EVENT_TYPE_GHEP_CHU)
						|| type.equalsIgnoreCase(MonthlyEventItemData.EVENT_TYPE_LAT_BAI)) {
					data.add(new MonthlyEventItemData(Long
							.parseLong(itemData[0]), itemData[1], itemData[2],
							itemData[3], components, itemData[5]));
				}
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO, data);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processReceiveEventItemMessage(String content, boolean status) {
		Intent intent = new Intent(INTENT_RECEIVE_EVENT_ITEM);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processParticipateJoinItemsEventMessage(String content,
			boolean status) {
		Intent intent = new Intent(INTENT_PARTICIPATE_JOIN_ITEMS_EVENT);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processUseInventoryItemMessage(String content, boolean status) {
		Intent intent = new Intent(INTENT_USE_INVENTORY_ITEM);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetEventHistoryMessage(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_EVENT_HISTORY);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String items[] = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<EventHistoryItemData> itemDatas = new ArrayList<EventHistoryItemData>();

			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");

			for (String itemString : items) {
				String item[] = NetworkUtils.stringSplit(itemString,
						NetworkUtils.ELEMENT_SEPERATOR);
				try {
					itemDatas.add(new EventHistoryItemData(item[0], formatter
							.parse(item[1]).getTime()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					itemDatas);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetInventoryMessage(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_INVENTORY);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String items[] = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<InventoryItemData> itemDatas = new ArrayList<InventoryItemData>();

			for (String itemString : items) {
				String item[] = NetworkUtils.stringSplit(itemString,
						NetworkUtils.ELEMENT_SEPERATOR);
				itemDatas.add(new InventoryItemData(item[0], item[1], item[2],
						Integer.parseInt(item[3]), "1"
								.equalsIgnoreCase(item[4])));
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					itemDatas);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processAcceptedInviteMessage(String string, boolean status) {
	}

	private void processReceiveFreeGoldMessage(String content, boolean status) {
		Intent intent = new Intent(INTENT_RECEIVE_FREE_GOLD);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processMultipleLoginMessage(String content, boolean status) {
		Intent intent = new Intent(INTENT_MULTIPLE_LOGIN);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processResetPasswordMessage(String string, boolean status) {
		Intent intent = new Intent(INTENT_RESET_PASSWORD);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, string);

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetPrivateMessage(String content, boolean status)
			throws NumberFormatException, ParseException {
		Intent intent = new Intent(INTENT_GET_PRIVATE_MESSAGE);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {

			String[] params = NetworkUtils.stringSplit(content,
					NetworkUtils.ELEMENT_SEPERATOR);

			SimpleDateFormat formatter = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm");

			MailItemData itemData = new MailItemData(Long.parseLong(params[0]),
					Long.parseLong(params[1]), params[2], params[3], formatter
							.parse(params[4]).getTime());

			intent.putExtra(NetworkUtils.MESSAGE_INFO, itemData);

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processRemoveSocialFriend(String string, boolean status) {
		Intent intent = new Intent(INTENT_REMOVE_SOCIAL_FRIEND);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, string);

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetAllUserLevels(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_ALL_USER_LEVELS);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<UserLevelItemData> results = new ArrayList<UserLevelItemData>();
			for (int i = 0; i < values.length; i++) {

				String[] params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);
				UserLevelItemData itemData = new UserLevelItemData(
						Long.parseLong(params[0]), params[1],
						Long.parseLong(params[2]), Long.parseLong(params[3]));
				results.add(itemData);

				itemData = null;
				params = null;
			}
			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					results);

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetAllPrivateMessages(String content, boolean status)
			throws ParseException {
		Intent intent = new Intent(INTENT_GET_ALL_PRIVATE_MESSAGES);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<MailItemData> results = new ArrayList<MailItemData>();
			for (int i = 0; i < values.length; i++) {

				String[] params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				SimpleDateFormat formatter = new SimpleDateFormat(
						"dd/MM/yyyy hh:mm");

				MailItemData itemData = new MailItemData(
						Long.parseLong(params[0]), Long.parseLong(params[1]),
						params[2], "", formatter.parse(params[3]).getTime());
				results.add(itemData);

				itemData = null;
				params = null;
			}
			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					results);

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}
		broadcastManager.sendBroadcast(intent);
	}

	private void processSendPrivateMessage(String string, boolean status) {
		Intent intent = new Intent(INTENT_SEND_PRIVATE_MESSAGE);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, string);

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetUserInfo(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_USER_INFO);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String elems[] = NetworkUtils.stringSplit(content,
					NetworkUtils.ELEMENT_SEPERATOR);

			long userId = Long.parseLong(elems[0]);
			String character = elems[1];
			long cash = Long.parseLong(elems[4]);
			boolean sex = Integer.parseInt(elems[8]) != 0;

			int vipId = 0;
			try {
				vipId = Integer.parseInt(elems[elems.length - 5]);
			} catch (Exception ex) {

			}
			;
			long xeeng = Long.parseLong(elems[elems.length - 4]);
			String loginName = elems[elems.length - 3];
			String personalId = elems[elems.length - 2];
			String telephone = elems[elems.length - 1];

			Player player = new Player();

			player.id = userId;
			player.sex = sex;
			player.character = character;
			player.cash = cash;
			player.xeeng = xeeng;
			player.vipId = vipId;

			intent.putExtra(NetworkUtils.MESSAGE_INFO, player);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetAllNews(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_ALL_NEWS);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<EventItemData> results = new ArrayList<EventItemData>();
			for (int i = 0; i < values.length; i++) {

				String[] params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);
				EventItemData itemData = new EventItemData(i + 1, params[1],
						Integer.parseInt(params[0]));
				results.add(itemData);

				itemData = null;
				params = null;
			}
			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					results);

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetNewsDetail(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_NEWS_DETAIL);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {

			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ELEMENT_SEPERATOR);
			intent.putExtra(NetworkUtils.MESSAGE_INFO, values[2]);

			values = null;

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);

	}

	private void processGetTableLevels(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_ROOM_LEVELS);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {

			ArrayList<RoomLevelItemData> tableLevels = new ArrayList<RoomLevelItemData>();

			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);

			for (String value : values) {
				String[] tokens = NetworkUtils.stringSplit(value,
						NetworkUtils.ELEMENT_SEPERATOR);

				String[] availableBets = NetworkUtils.stringSplit(tokens[3],
						";");
				long[] bets = new long[availableBets.length];
				for (int i = 0; i < availableBets.length; i++) {
					bets[i] = Long.parseLong(availableBets[i]);
				}
				tableLevels.add(new RoomLevelItemData(Integer
						.parseInt(tokens[0]), tokens[1], Long
						.parseLong(tokens[2]), bets, Integer
						.parseInt(tokens[4]) > 0));
			}
			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					tableLevels);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}
		broadcastManager.sendBroadcast(intent);
	}

	private void processGetMessageDetail(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_MESSAGE_DETAILS);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {

			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ELEMENT_SEPERATOR);
			intent.putExtra(NetworkUtils.MESSAGE_INFO, values[1]);

			values = null;

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);

	}

	private void processGetMessageList(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_OFFLINE_MESSAGE);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {

			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<MessageItemData> results = new ArrayList<MessageItemData>();
			String[] params;
			for (int i = 0; i < values.length; i++) {

				params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);
				MessageItemData itemData = new MessageItemData(i + 1,
						Integer.parseInt(params[0]), params[1], params[2]);
				results.add(itemData);

				itemData = null;
				params = null;

			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					results);

			values = null;
			results = null;

		} else {

			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void procesGetMoneyTransferMessage(String msg, boolean status) {

		Intent transfer = new Intent(INTENT_GET_TRANSFER_CASH_MESSAGE);
		transfer.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		String txtDes;
		// if (status) {
		String[] ss = NetworkUtils.stringSplit(msg,
				NetworkUtils.DIFF_ARRAY_SEPERATOR);
		String[] values = NetworkUtils.stringSplit(ss[0],
				NetworkUtils.ELEMENT_SEPERATOR);
		txtDes = values[0];
		transfer.putExtra(NetworkUtils.MESSAGE_INFO_2, txtDes);
		// }else{
		// txtDes = values[0];
		// transfer.putExtra(NetworkUtils.MESSAGE_INFO_2, txtDes);
		// }

		broadcastManager.sendBroadcast(transfer);

	}

	/*
	 * PRIVATE processing common message methods
	 */
	private void processLoginMessage(String msg, boolean status) {
		Intent login = new Intent(INTENT_LOGIN);
		login.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		String arrays[] = NetworkUtils.stringSplit(msg,
				NetworkUtils.DIFF_ARRAY_SEPERATOR);

		String elems[] = NetworkUtils.stringSplit(arrays[0],
				NetworkUtils.ELEMENT_SEPERATOR);

		if (status) {
			long userId = Long.parseLong(elems[0]);
			long cash = Long.parseLong(elems[1]);
			long avatarId = Long.parseLong(elems[2]);
			String avatarVersion = elems[3];

			if (elems.length >= 7) {
				try {
					GameData.shareData().updateLink = elems[4];
					GameData.shareData().updateDescription = elems[5];
					GameData.shareData().isNeedUpdate = !"0"
							.equalsIgnoreCase(elems[6]);
				} catch (ArrayIndexOutOfBoundsException e) {

				}
			}

			UserPreference sharedPreference = UserPreference.sharePreference();

			// TODO
			// Player player = new Player();
			// player.id = userId;
			// player.name = sharedPreference.readLoginUsername();
			// player.cash = cash;
			// player.sex = (sex != 0);
			// player.avatarId = avatarId;
			//
			// GameData.shareData().setMyself(player);

			GameData.shareData().update(userId,
					sharedPreference.readLoginUsername(), cash, 0, avatarId);
			GameData.shareData().setGuest(false);

			if (arrays.length > 1) {
				// String[] elems1 = NetworkUtils.stringSplit(arrays[1],
				// NetworkUtils.ELEMENT_SEPERATOR);
				// login.putExtra(NetworkUtils.ACTIVE_ACCOUNT_BODY, elems1[0]
				// + " " + userId);
				//
				// if (elems1.length > 1) {
				//
				// login.putExtra(NetworkUtils.ACTIVE_ACCOUNT_NUMBER_PHONE,
				// elems1[1]);
				// GameData.shareData().activePhone = elems1[1];
				// GameData.shareData().activeUserNotifyMsg = elems1[2];
				// GameData.shareData().activeSystax = elems1[0] + " "
				// + userId;
				// }
			}
			if (arrays.length > 3) {
				login.putExtra(NetworkUtils.MESSAGE_INFO_2, arrays[3]);
			}

			// Test test test
			// int matchId = 9881;
			// login.putExtra(
			// NetworkUtils.MESSAGE_INFO_2,
			// "Ván bài của bạn trong phòng " + matchId +
			// " chưa kết thúc. Hãy chiến tiếp bạn nhé!\u00015\u0001" +
			// matchId);
		} else {
			if (elems.length > 1) {

				GameData.shareData().activeUserNotifyMsg = elems[0];
				GameData.shareData().activeSystax = elems[1].substring(0,
						elems[1].lastIndexOf(' '));
				GameData.shareData().activePhone = elems[1].substring(elems[1]
						.lastIndexOf(' ') + 1);
				login.putExtra("NeedActive", true);
				login.putExtra(NetworkUtils.MESSAGE_INFO, elems[0]);
			} else {
				login.putExtra(NetworkUtils.MESSAGE_INFO, msg);
			}
		}

		broadcastManager.sendBroadcast(login);
	}

	private void processFastLoginMessage(String msg, boolean status) {

		Intent login = new Intent(INTENT_FAST_LOGIN);
		login.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		if (status) {

			String[] ss = NetworkUtils.stringSplit(msg,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);
			String[] values = NetworkUtils.stringSplit(ss[0],
					NetworkUtils.ELEMENT_SEPERATOR);
			long userId = Long.parseLong(values[0]);
			String userName = values[1];
			long cash = Long.parseLong(values[2]);

			if (values.length >= 6) {
				try {
					GameData.shareData().updateLink = values[3];
					GameData.shareData().updateDescription = values[4];
					GameData.shareData().isNeedUpdate = !"0"
							.equalsIgnoreCase(values[5]);
				} catch (ArrayIndexOutOfBoundsException e) {

				}
			}

			GameData.shareData().update(userId, userName, cash, 0, 0);
			GameData.shareData().setGuest(true);

			/*
             */
			// if (ss.length > 1) {
			//
			// String[] infos = NetworkUtils.stringSplit(ss[1],
			// NetworkUtils.ELEMENT_SEPERATOR);
			// Log.d(getClass().getSimpleName(), new StringBuffer(
			// "info count: ").append(infos.length).toString());
			// login.putExtra(NetworkUtils.ACTIVE_ACCOUNT_BODY, infos[0] + " "
			// + userId);
			// if (infos.length > 2) {
			//
			// login.putExtra(NetworkUtils.ACTIVE_ACCOUNT_NUMBER_PHONE,
			// infos[1]);
			// GameData.shareData().activePhone = infos[1];
			// GameData.shareData().activeUserNotifyMsg = infos[2];
			// GameData.shareData().activeSystax = infos[0] + " " + userId;
			// }
			//
			// }

			if (ss.length > 1) {
				login.putExtra(NetworkUtils.MESSAGE_INFO_2, ss[1]);
			}
			// Test test test
			// login.putExtra(
			// NetworkUtils.MESSAGE_INFO_2,
			// "Ván bài của bạn trong phòng 9444 chưa kết thúc. Hãy chiến tiếp bạn nhé!\u00014\u00019444");
		} else {

			String ss[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);

			// Log.d("MessageService", msg)

			/*
			 * check if address of server changed
			 */

			Log.d("MessageService",
					new StringBuffer("ss length: ").append(ss.length)
							.toString());

			if (ss.length > 1) {
				// TODO
				// save new ip address of server to preference and notify user
				// know it or re-login
				return;
			}

			String values[] = NetworkUtils.stringSplit(ss[0],
					NetworkUtils.ELEMENT_SEPERATOR);

			if (values.length > 1) {
				GameData.shareData().activeUserNotifyMsg = values[0];
				GameData.shareData().activeSystax = values[1].substring(0,
						values[1].lastIndexOf(' '));
				GameData.shareData().activePhone = values[1]
						.substring(values[1].lastIndexOf(' ') + 1);
				login.putExtra("NeedActive", true);
				login.putExtra(NetworkUtils.MESSAGE_INFO, values[0]);
			} else {
				login.putExtra(NetworkUtils.MESSAGE_INFO, msg);
			}
		}

		broadcastManager.sendBroadcast(login);
	}

	private void processLogoutMessage(String content, boolean status) {
	}

	private void processRegisterMessage(String content, boolean status) {

		Intent register = new Intent(INTENT_REGISTER);
		register.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		if (status) {

			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ELEMENT_SEPERATOR);
			/*
			 * Check if require active account first
			 */
			if (values.length > 0) {

				register.putExtra(NetworkUtils.ACTIVE_ACCOUNT_NUMBER_PHONE,
						values[1].substring(values[1].lastIndexOf(' ') + 1));
				register.putExtra(NetworkUtils.ACTIVE_ACCOUNT_BODY,
						values[1].substring(0, values[1].lastIndexOf(' ')));
			} else {

				register.putExtra(NetworkUtils.MESSAGE_INFO,
						"Đăng ký thành công");
			}
		} else {

			register.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(register);
	}

	private void processAdsMessage(String content, boolean status) {

		Intent intent = new Intent(INTENT_ADVERTISEMENT);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetRewardListMessage(String content, boolean status) {
		Intent getRewardList = new Intent(INTENT_REWARD_LIST);

		getRewardList.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);

			if (values.length > 0) {

				ArrayList<RewardItemData> arrayList = new ArrayList<RewardItemData>();
				String[] items;
				for (int i = 0; i < values.length; i++) {
					items = NetworkUtils.stringSplit(values[i],
							NetworkUtils.ELEMENT_SEPERATOR);

					if (items.length < 2) {
						continue;
					}

					// if (Integer.parseInt(items[0]) == 1) {
					// continue;
					// }

					RewardItemData itemData = new RewardItemData(i + 1,
							Integer.parseInt(items[0]), items[1]);
					arrayList.add(itemData);

					// getRewardList.putStringArrayListExtra(NetworkUtils.MESSAGE_INFO,
					// arrayList);
				}

				getRewardList.putParcelableArrayListExtra(
						NetworkUtils.MESSAGE_INFO, arrayList);
				items = null;
				arrayList = null;

			}
		} else {
			getRewardList.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(getRewardList);
	}

	private void processGetFriendListBlogMessage(String content, boolean status) {
		Intent getFriendList = new Intent(INTENT_GET_FRIENDS_LIST_BLOG);

		getFriendList.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);

			if (values.length > 1) {

				String[] players = NetworkUtils.stringSplit(values[0],
						NetworkUtils.ARRAY_SEPERATOR);

				ArrayList<Player> playersList = new ArrayList<Player>();
				String[] params;
				for (int i = 0; i < players.length; i++) {
					params = NetworkUtils.stringSplit(players[i],
							NetworkUtils.ELEMENT_SEPERATOR);

					Player player = new Player();
					player.id = Long.parseLong(params[0]);
					player.character = params[1];
					player.cash = Long.parseLong(params[2]);
					player.isOnline = Integer.parseInt(params[4]) == 1 ? true
							: false;
					playersList.add(player);

				}

				params = null;

				getFriendList.putParcelableArrayListExtra(
						NetworkUtils.MESSAGE_INFO, playersList);
				playersList = null;
				players = null;
				values = null;
			}
		} else {
			getFriendList.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(getFriendList);

	}

	private void processGetFriendOnlineListBlogMessage(String content,
			boolean status) {
		Intent getFriendList = new Intent(INTENT_GET_FRIENDS_ONLINE_LIST_BLOG);

		getFriendList.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);

			if (values.length > 1) {

				String[] players = NetworkUtils.stringSplit(values[0],
						NetworkUtils.ARRAY_SEPERATOR);

				ArrayList<Player> playersList = new ArrayList<Player>();
				String[] params;
				for (int i = 0; i < players.length; i++) {
					params = NetworkUtils.stringSplit(players[i],
							NetworkUtils.ELEMENT_SEPERATOR);

					Player player = new Player();
					player.id = Long.parseLong(params[0]);
					player.character = params[1];
					player.cash = Long.parseLong(params[2]);
					player.isOnline = Integer.parseInt(params[4]) == 1 ? true
							: false;
					if (player.isOnline) {
						playersList.add(player);
					}
				}

				params = null;

				getFriendList.putParcelableArrayListExtra(
						NetworkUtils.MESSAGE_INFO, playersList);
				playersList = null;
				players = null;
				values = null;
			}
		} else {
			getFriendList.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(getFriendList);

	}

	private void processGetPlayerFreeMessage(String content, boolean status) {
		Intent getFriendList = new Intent(INTENT_GET_PLAYERS_FREE);

		getFriendList.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);

			if (values.length > 0) {

				String[] players = NetworkUtils.stringSplit(values[0],
						NetworkUtils.ARRAY_SEPERATOR);

				ArrayList<Player> playersList = new ArrayList<Player>();
				String[] params;
				for (int i = 0; i < players.length; i++) {
					params = NetworkUtils.stringSplit(players[i],
							NetworkUtils.ELEMENT_SEPERATOR);

					Player player = new Player();
					player.id = Long.parseLong(params[0]);
					player.character = params[1];
					player.cash = Long.parseLong(params[2]);
					playersList.add(player);

				}

				params = null;

				getFriendList.putParcelableArrayListExtra(
						NetworkUtils.MESSAGE_INFO, playersList);
				playersList = null;
				players = null;
				values = null;
			}
		} else {
			getFriendList.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(getFriendList);

	}

	public void processGetFriendRequests(String content, boolean status) {
		Intent getFriendReqs = new Intent(INTENT_GET_FRIEND_REQUESTS);
		getFriendReqs.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);

			if (values[0].length() < 0) {

			} else {
				String[] requestStrings = NetworkUtils.stringSplit(values[0],
						NetworkUtils.ARRAY_SEPERATOR);

				ArrayList<FriendRequestItemData> result = new ArrayList<FriendRequestItemData>();
				String[] params;

				for (int i = 0; i < requestStrings.length; i++) {
					params = NetworkUtils.stringSplit(requestStrings[i],
							NetworkUtils.ELEMENT_SEPERATOR);

					FriendRequestItemData object = new FriendRequestItemData(
							Long.parseLong(params[0]), params[1]);
					result.add(object);

					params = null;
				}

				params = null;

				getFriendReqs.putParcelableArrayListExtra(
						NetworkUtils.MESSAGE_INFO, result);

				requestStrings = null;
				result = null;
			}

			values = null;

		} else {
			getFriendReqs.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(getFriendReqs);
	}

	private void processGetAvatarPlayer(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_AVATAR);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ELEMENT_SEPERATOR);

			if (values.length > 1) {
				long playerId = Long.parseLong(values[0]);
				intent.putExtra(NetworkUtils.PLAYER_ID, playerId);
				byte[] data = Base64.decode(values[2], Base64.DEFAULT);
				intent.putExtra(NetworkUtils.MESSAGE_INFO, data);
				data = null;
			}

			values = null;

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetSMSChargingList(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_SMS_CHARGING_LIST);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] chargings = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<ChargingItemData> results = new ArrayList<ChargingItemData>();

			for (int j = 0; j < chargings.length; j++) {
				String[] params = NetworkUtils.stringSplit(chargings[j],
						NetworkUtils.ELEMENT_SEPERATOR);

				results.add(new ChargingItemData(params[0], params[2],
						ChargingItemType.values()[Integer.parseInt(params[3])],
						params[1]));
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					results);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetEventsList(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_EVENTS_LIST);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {

			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<EventItemData> results = new ArrayList<EventItemData>();
			String[] params;
			for (int i = 0; i < values.length; i++) {

				params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);
				EventItemData itemData = new EventItemData(i + 1, params[1],
						Integer.parseInt(params[0]));
				results.add(itemData);

				itemData = null;
				params = null;

			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					results);

			values = null;
			results = null;

		} else {

			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetEventDetail(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_EVENT_DETAILS);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {

			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ELEMENT_SEPERATOR);
			intent.putExtra(NetworkUtils.MESSAGE_INFO, values[1]);

			values = null;

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);

	}

	private void procesGetListAchievement(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_LIST_ACHIEVEMENT);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);

			if (values.length > 0) {
				ArrayList<AchievementItem> result = new ArrayList<AchievementItem>();

				String[] items;
				AchievementItem item;

				for (int i = 0; i < values.length; i++) {
					items = NetworkUtils.stringSplit(values[i],
							NetworkUtils.ELEMENT_SEPERATOR);
					if (items.length < 2) {
						continue;
					}

					item = new AchievementItem(Integer.parseInt(items[0]),
							items[1]);
					result.add(item);

				}

				intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
						result);

				item = null;
				items = null;
				result = null;

			}
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetTopPlayer(String content, boolean status,
			int messageId) {
		Intent intent = new Intent(INTENT_GET_TOP_PLAYER);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);

			// ArrayList<TopPlayerItem> result = new ArrayList<TopPlayerItem>();
			ArrayList<Player> playerList = new ArrayList<Player>();
			// TopPlayerItem item = null;

			for (int i = 0; i < values.length; i++) {
				String[] params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				Player player = new Player();
				player.id = Long.parseLong(params[0]);
				player.character = params[1];
				player.cash = Long.parseLong(params[4]);
				player.sex = "true".equalsIgnoreCase(params[6]);
				player.isOnline = false; // TODO

				playerList.add(player);

				// if (messageId == MessagesID.GET_RICHESTS) {
				// item = new TopPlayerItem(i + 1, params[1],
				// Long.parseLong(params[4]), "Xu");
				// } else if (messageId == MessagesID.GET_BEST_PLAYER) {
				// item = new TopPlayerItem(i + 1, params[1],
				// Long.parseLong(params[4]), "exp");
				// }
				//
				// item.playerId = Long.parseLong(params[0]);
				//
				// result.add(item);
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					playerList);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetListDetailAchievement(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_TOP_PLAYER);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);

			ArrayList<TopPlayerItem> result = new ArrayList<TopPlayerItem>();

			String[] params;
			TopPlayerItem item = null;

			for (int i = 0; i < values.length; i++) {
				params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				if (params.length < 3) {
					item = new TopPlayerItem(i + 1, Long.parseLong(params[0]),
							params[1], "");
				} else {
					item = new TopPlayerItem(i + 1, Long.parseLong(params[0]),
							params[1], params[2]);
				}

				result.add(item);
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					result);

			item = null;
			params = null;
			result = null;
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processInviteNewGame(String content, boolean status) {
		Intent intent = new Intent(INTENT_INVITE_PLAY_NEW_GAME);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		// boolean isInvite;
		//
		// if (status) {
		// String[] values = NetworkUtils.stringSplit(content,
		// NetworkUtils.ELEMENT_SEPERATOR);
		//
		// if (values.length > 5) {
		// isInvite = true;
		//
		// long invitingPlayerId = Long.parseLong(values[0]);
		// long minCash = Long.parseLong(values[3]);
		// int gameId = Integer.parseInt(values[2]);
		// long requestId = Long.parseLong(values[4]);
		// if (GameData.shareData().isRejectAllInvitaion) {
		// BusinessRequester.getInstance().answerInvitaion(requestId,
		// invitingPlayerId, false);
		// return;
		// }
		//
		// String inviteString = new StringBuffer(values[1])
		// .append(" mời bạn chơi game ")
		// .append(CommonUtils.getGameName(gameId)).toString();
		// InvitationItemData data = new InvitationItemData(
		// invitingPlayerId, gameId, inviteString, minCash,
		// requestId);
		// intent.putExtra(NetworkUtils.MESSAGE_INFO, data);
		// intent.putExtra(NetworkUtils.MESSAGE_INFO_2, inviteString);
		// intent.putExtra(NetworkUtils.MESSAGE_INFO_3, invitingPlayerId);
		// intent.putExtra(NetworkUtils.MESSAGE_INFO_4, requestId);
		// intent.putExtra(NetworkUtils.MESSAGE_INFO_5, gameId);
		// inviteString = null;
		// data = null;
		// values = null;
		//
		// } else {
		// isInvite = false;
		// }
		// } else {
		// isInvite = false;
		// intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		// }
		//
		// if (isInvite) {
		// intent.setAction(INTENT_INVITE_PLAY_NEW_GAME);
		// } else {
		// intent.setAction(INTENT_SEND_INVITATION_SUCCESSFUL);
		// }

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetShopItems(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_SHOP_ITEMS);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<ShopItemData> shopItems = new ArrayList<ShopItemData>();

			for (int i = 0; i < values.length; i++) {
				String[] params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				ShopItemData shopItem = new ShopItemData();
				shopItem.setId(params[0]);
				shopItem.setValue(Float.parseFloat(params[2])
						* Integer.parseInt(params[3]));
				shopItem.setPrice(Float.parseFloat(params[4]));
				shopItem.setDescription(params[5]);

				if (params.length > 6) {
					shopItem.setBonus(Float.parseFloat(params[6]));
				}

				shopItems.add(shopItem);
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					shopItems);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processGetCashShopItemsMessage(String content, boolean status) {
		Intent intent = new Intent(INTENT_GET_CASH_SHOP_ITEMS);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			String[] values = NetworkUtils.stringSplit(content,
					NetworkUtils.ARRAY_SEPERATOR);
			ArrayList<CashShopItemData> shopItems = new ArrayList<CashShopItemData>();

			for (int i = 0; i < values.length; i++) {
				String[] params = NetworkUtils.stringSplit(values[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				CashShopItemData shopItem = new CashShopItemData(params[0],
						params[1], params[2], Long.parseLong(params[3]),
						params[4]);

				shopItems.add(shopItem);
			}

			intent.putParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO,
					shopItems);
		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, content);
		}

		broadcastManager.sendBroadcast(intent);
	}

	private void processPurchaseShopItem(String content, boolean status) {
		Intent intent = new Intent(INTENT_PURCHASE_SHOP_ITEM);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		intent.putExtra(NetworkUtils.MESSAGE_INFO, content);

		broadcastManager.sendBroadcast(intent);
	}

}
