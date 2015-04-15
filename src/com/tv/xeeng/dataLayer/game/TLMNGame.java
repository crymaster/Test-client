package com.tv.xeeng.dataLayer.game;

import android.content.Intent;
import android.util.Log;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.game.actions.BaseXEGameAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowPlayerJoinedAction;
import com.tv.xeeng.dataLayer.game.actions.XETLMNShowTurnAction;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.TLMNGameActivity;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Card;
import com.tv.xeeng.gamedata.entity.CardGameTable;
import com.tv.xeeng.gamedata.entity.Player.PlayerState;
import com.tv.xeeng.gamedata.entity.TLMNCardComparator;
import com.tv.xeeng.gamedata.entity.TLMNPlayer;
import com.tv.xeeng.manager.Logger;

import java.util.*;

public class TLMNGame extends BaseCardGame {

	public enum WinType {
		NORMAL, TOI_TRANG, TU_QUY_HAI, DOI_THONG_5, DOI_THONG_6, SANH_RONG;

		@Override
		public String toString() {
			switch (this) {
			case TU_QUY_HAI:
				return "Tứ quý 2";
			case DOI_THONG_5:
				return "5 đôi thông";
			case DOI_THONG_6:
				return "6 đôi thông";
			case SANH_RONG:
				return "Sảnh rồng";
			default:
				return "";
			}
		}
	}

	public enum MissionType {

		NOTHING(-1, "", ""), WIN_X2(0, "Win", "2"), A_X2(1, "A", "2"), AA_X2(2,
				"AA", "2"), AAA_X3(3, "AAA", "3"), ABC_X2(4, "ABC", "2"), ABCD_X2(
				5, "ABCD", "2"), ABCDE_X3(6, "ABCDE", "3"), ABCDEF_X3(7,
				"ABCDEF", "3");

		private final String PRE = "Hoàn thành nhiệm vụ đánh bài dạng \"";
		private final String MID = " \" để được nhân ";
		private final String SUF = " số tiền khi thắng";
		private int mvalue;
		private String mName;
		private String mMulti;
		private String mDescription;

		private MissionType(int pValue, String pName, String pMulti) {

			mvalue = pValue;
			mName = pName;
			mMulti = pMulti;
			if (pName.equalsIgnoreCase("win")) {
				mDescription = "Nhân 2 số tiền khi dành chiến thắng";
			} else {
				mDescription = PRE + mName + MID + mMulti + SUF;
			}
		}

		public int toValue() {
			return mvalue;
		}

		public String getName() {

			return mName;
		}

		public String getMulti() {

			return mMulti;
		}

		public String getDescription() {

			return mDescription;
		}
	}

	public enum FightCardType {
		NOTHING(-1, 0), DOI_THONG_3(0, 6), DOI_THONG_4(1, 8), DOI_THONG_5(2, 10), DOI_THONG_6(
				3, 12), TU_QUY(4, 4);

		private int mValue;
		private int mTotalCard;

		private FightCardType(int pValue, int pTotal) {
			mValue = pValue;
			mTotalCard = pTotal;
		}

		public int toValue() {
			return mValue;
		}

		public int getTotalCard() {
			return mTotalCard;
		}
	}

	public static final int NULL_CARD = 0;
	public static final int SINGLE_CARD = 1;
	public static final int PAIR = 2;
	public static final int THREE_CARDS = 3;
	public static final int FOUR_CARDS = 4;
	public static final int SERIAL_CARDS = 5;
	public static final int COUPLE_SERIAL_CARDS = 6;

	public Vector<Vector<Card>> lastCards = new Vector<Vector<Card>>(); // nhom
																		// bao
																		// doi
																		// phuong
																		// danh
																		// ra
	public int dutyType;
	public String dutyStr;
	public int currentTurn;
	public WinType mWinType = WinType.NORMAL;
	public long mWinPlayerId = -1;
	public MissionType mMissionType;
	public boolean isHideCard;
	public long mNextPlayerId = -1, prevPlayerId;
	public Map<String, Long> endMatchFightCase;
	public FightCardType endMatchType;
	protected int mNumHandOfPlayer = 13;

	public final static String IS_SKIP = "isSkip";
	public final static String IS_NEW_ROUND = "isNewRound";
	public final static String NEXT_PLAYER_ID = "nextPlayerId";
	public final static String PREV_PLAYER_ID = "prev_player_id";

	public final static String BE_FIGHTEN_ID = "be_Fighten_Id";
	public final static String FIGHT_ID = "fight_id";
	public final static String PRE_BE_FIGHTEN_ID = "pre_be_fighten_id";
	public final static String LOST_MONEY = "lost_money";
	public final static String GAIN_MONEY = "gain_money";
	public final static String OLD_MONEY = "oldMoney";
	public final static String FIGHT_CARD_TYPE = "fight_card_type";

	public TLMNGame() {
		super();
		activityClass = TLMNGameActivity.class;
		gameName = "TIẾN LÊN MIỀN NAM";
	}

	/**
	 * TLMN logic game
	 */
	public void removeCardFromListOfMainPlayer(ArrayList<Card> removedList) {

		for (Card card : removedList) {
			Iterator<Card> iter = getMainPlayer().getCardList().iterator();
			while (iter.hasNext()) {
				Card pcard = iter.next();
				if (card.serverValue == pcard.serverValue) {

					iter.remove();
					break;
				}
			}
		}
	}

	public boolean isValidTurn(List<Card> cards) {

		if (isValidGroup(cards) > 0) {

			return true;
		} else {

			return false;
		}
	}

	public boolean isValidFight(List<Card> turnCards, List<Card> prevCards) {
		if (prevCards == null || prevCards.size() == 0) {

			Logger.getInstance().warn(this, "Main player is first fighter");
			return isValidTurn(turnCards);
		}

		if (turnCards == null || turnCards.size() == 0) {

			Logger.getInstance().error(this,
					"Fighted card list is null or 0 element");
			return false;
		}

		for (Card card : prevCards) {
			Log.d("TLMNGame",
					new StringBuffer("before prevCards: ").append(card.value)
							.toString());
		}

		for (Card card : turnCards) {
			Log.d("TLMNGame",
					new StringBuffer("before turnCards: ").append(card.value)
							.toString());
		}

		Collections.sort(turnCards, new TLMNCardComparator());
		Collections.sort(prevCards, new TLMNCardComparator());

		for (Card card : prevCards) {
			Log.d("TLMNGame", new StringBuffer("prevCards: ")
					.append(card.value).toString());
		}

		for (Card card : turnCards) {
			Log.d("TLMNGame", new StringBuffer("turnCards: ")
					.append(card.value).toString());
		}

		// Chat cung cap, so quan 2 bo bang nhau
		if (turnCards.size() == prevCards.size()) {

			if (isValidGroup(turnCards) != isValidGroup(prevCards)) {

				return false;
			} else {

				Card turnCardMax = turnCards.get(turnCards.size() - 1);
				Card prevCardMax = prevCards.get(prevCards.size() - 1);

				if (turnCardMax.compareInTLMN(prevCardMax) > 0) {
					turnCardMax = null;
					prevCardMax = null;
					return true;
				} else {
					turnCardMax = null;
					prevCardMax = null;
					return false;
				}
			}
		} else {

			if (isValidGroup(turnCards) == FOUR_CARDS) { // Chat bang tu quy
				// Tu quy chat dc 1 heo, 2 heo, 3 doi thong bat ky hoac tu quy
				// nho hon
				Card prevCardMax = prevCards.get(prevCards.size() - 1);
				if (isValidGroup(prevCards) <= PAIR
						&& prevCardMax.getCardValueInTLMN() == 12) { // Chat 1
					// heo
					// hoac
					// 2 heo
					return true;
				} else if (isValidGroup(prevCards) == COUPLE_SERIAL_CARDS
						&& prevCards.size() == 6) { // Chat 3 doi thong bat ky
					return true;
				} else {
					return false;
				}
			} else if (isValidGroup(turnCards) == COUPLE_SERIAL_CARDS) {
				Card prevCardMax = prevCards.get(prevCards.size() - 1);
				switch (turnCards.size()) {

				case 6: // Ba doi thong
					// Chat dc 1 heo
					if (prevCards.size() == 1
							&& prevCardMax.getCardValueInTLMN() == 12) {
						return true;
					} else {
						return false;
					}
				case 8: // Bon doi thong
					if (isValidGroup(prevCards) <= PAIR
							&& prevCardMax.getCardValueInTLMN() == 12) { // Chat
						// dc
						// 1
						// heo
						// hoac
						// 2
						// heo
						return true;
					} else if (isValidGroup(prevCards) == COUPLE_SERIAL_CARDS
							&& prevCards.size() == 6) {
						return true;
					} else if (isValidGroup(prevCards) == FOUR_CARDS) {
						return true;
					}

				default:
					return false;
				}
			} else {
				return false;
			}
		}
	}

	protected int isValidGroup(List<Card> cards) {
		if (cards == null || cards.size() == 0) {
			return NULL_CARD;
		} else if (cards.size() == 1) {
			return SINGLE_CARD;
		} else {
			Card first;
			Card second;

			for (int i = 0; i < cards.size() - 1; i++) {
				first = cards.get(i);
				second = cards.get(i + 1);

				if (first.getCardValueInTLMN() != second.getCardValueInTLMN()) {
					if (cards.size() > 2) {
						if (isSerialCards(cards)) {
							return SERIAL_CARDS;
						} else if (isSerialCoupleCards(cards)) {
							return COUPLE_SERIAL_CARDS;
						} else {
							return -1;
						}
					} else {

						return -1;
					}
				}
			}

			switch (cards.size()) {
			case 2:
				return PAIR;

			case 3:
				return THREE_CARDS;

			case 4:
				return FOUR_CARDS;

			default:
				return -1;
			}

		}
	}

	protected boolean isSerialCards(List<Card> cards) {

		if (cards.size() == 0 || cards.size() < 3) {
			return false;
		}

		// Trong sanh khong bao gio chua quan 2 tru sanh rong
		for (Card card : cards) {
			if (card.getCardValueInTLMN() == 12
					&& cards.size() != mNumHandOfPlayer) {
				return false;
			}
		}

		// ArrayList<Card> temp = Collections.sort(list, comparator);
		Collections.sort(cards, new TLMNCardComparator());

		int minValue, nextValue;
		minValue = cards.get(0).getCardValueInTLMN();
		for (int i = 1; i < cards.size(); i++) {

			nextValue = cards.get(i).getCardValueInTLMN();
			if (nextValue == minValue + 1) {

				minValue = nextValue;
			} else {

				return false;
			}
		}

		return true;
	}

	protected boolean isSerialCoupleCards(List<Card> cards) {
		if (cards == null || cards.size() < 6 || cards.size() % 2 != 0) {
			return false;
		}

		for (Card card : cards) {
			if (card.getCardValueInTLMN() == 12) {
				return false;
			}
		}

		Collections.sort(cards, new TLMNCardComparator());

		Card first;
		Card second;
		Card third;

		for (int i = 0; i < cards.size() - 3; i += 2) {
			first = cards.get(i);
			second = cards.get(i + 1);
			third = cards.get(i + 2);
			if (!(first.getCardValueInTLMN() == second.getCardValueInTLMN() && second
					.getCardValueInTLMN() + 1 == third.getCardValueInTLMN())) {
				return false;
			}
		}

		first = cards.get(cards.size() - 2);
		second = cards.get(cards.size() - 1);

		if (first.getCardValueInTLMN() != second.getCardValueInTLMN()) {
			return false;
		}

		first = null;
		second = null;
		third = null;

		return true;
	}

	public List<Card> sortCardList(List<Card> pCardList) {

		Collections.sort(pCardList, compareByValue);
		return pCardList;
	}

	public List<Card> sortCardListWithHorizontalType(List<Card> cards) {

		Collections.sort(cards, new TLMNCardComparator());
		return cards;
	}

	public List<Card> sortCardListWithVerticalType(List<Card> cards) {

		Collections.sort(cards, new TLMNCardComparator());
		Card minCard = cards.get(0);
		List<Card> result = new Vector<Card>();
		result.add(minCard);
		cards.remove(0);

		int i = 0;
		Card card;
		while (cards.size() > 0) {
			if (cards.size() == 0) {
				break;
			}

			if (i >= cards.size()) {
				i = 0;
				minCard = cards.get(0);
				result.add(minCard);
				cards.remove(0);
			}

			if (cards.size() == 0) {
				break;
			}

			card = cards.get(i);
			if (card.getCardValueInTLMN() == minCard.getCardValueInTLMN() + 1) {
				result.add(card);
				cards.remove(card);
				minCard = result.get(result.size() - 1);
				i = 0;
			} else {
				i++;
			}
		}

		return result;

	}

	@Override
	public void processSpecificMessage(final int mId, final String msg,
			final boolean status) {

		super.processSpecificMessage(mId, msg, status);

		switch (mId) {
		case MessagesID.GET_POKER:

			processGetPokerMessage(msg, status);
			break;
		case MessagesID.MATCH_TURN:

			processMatchTurnMesasge(msg, status);
			break;
		default:
			break;
		}
	}

	@Override
	protected void createNewTable(String msg) {
		prepareForNewGame();

		String[] gameTableParams = NetworkUtils.stringSplit(msg,
				NetworkUtils.ELEMENT_SEPERATOR);

		GameData.shareData().getMyself().cash = Long
				.parseLong(gameTableParams[gameTableParams.length - 1]);
		mainPlayer = addPlayerToList(GameData.shareData().getMyself().id,
				GameData.shareData().getMyself().character, GameData
						.shareData().getMyself().cash, true);

		logPlayerList();
	}

	@Override
	public void processJoinMatchMessage(String msg, boolean status) {

		super.processJoinMatchMessage(msg, status);
		Intent intent = new Intent(MessageService.INTENT_JOIN_MATCH);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			prepareForNewGame();
			String[] infos = NetworkUtils.stringSplit(msg,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);

			// first string is gameInfo
			String[] gameInfo = NetworkUtils.stringSplit(infos[0],
					NetworkUtils.ELEMENT_SEPERATOR);
			matchId = Long.parseLong(gameInfo[0]);
			currentBetCash = Long.parseLong(gameInfo[1]);
			isHideCard = "1".equalsIgnoreCase(gameInfo[3]);
			CardGameTable table = new CardGameTable(
					GameData.shareData().currentTableNumber);
			table.update(matchId, 1, getMaxPlayers(), currentBetCash,
					GameData.shareData().minBetCash);
			setCurrentTable(table);

			if (gameInfo.length > 4) {

				mNextPlayerId = Long.parseLong(gameInfo[5]);
			}

			boolean isPlaying = "1".equalsIgnoreCase(gameInfo[2]);
			if (isPlaying) {

				state = GameState.PLAYING;
			} else {

				state = GameState.WAITING;
			}

			String[] playersStr = NetworkUtils.stringSplit(infos[1],
					NetworkUtils.ARRAY_SEPERATOR);
			String[] playerParams;
			TLMNPlayer player;

			for (int i = 0; i < playersStr.length; i++) {
				playerParams = NetworkUtils.stringSplit(playersStr[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				player = new TLMNPlayer();
				player.id = Long.parseLong(playerParams[0]);
				player.character = playerParams[1];
				player.avatarId = Integer.parseInt(playerParams[2]);
				player.cash = Long.parseLong(playerParams[3]);
				player.isReady = "1".equalsIgnoreCase(playerParams[4]);
				player.vipId = Integer.parseInt(playerParams[6]);
				// player.isReady = true;

				if (i == 0) {
					player.isTableMaster = true;
					player.isReady = false;
				} else {
					player.isTableMaster = false;
				}

				boolean isObserver = "1".equalsIgnoreCase(playerParams[5]);

				if (isPlaying) {
					if (isObserver) {
						player.state = PlayerState.OBSERVING;
					} else {
						player.state = PlayerState.PLAYING;
					}
				} else {
					player.state = PlayerState.WAITING;
				}

				if (playerParams.length > 7) {
					player.numHand = Integer.parseInt(playerParams[7]);
				}

				getPlayerList().add(player);

				if (player.id == GameData.shareData().getMyself().id) {
					mainPlayer = player;
				}
			}
			logPlayerList();

			if (gameInfo.length > 4) {
				dutyType = Integer.parseInt(gameInfo[4]);
				setDutyDescription();
			}

			if (gameInfo.length > 6) {
				lastCards.add(parseCardsArray(gameInfo[6]));
			}

		} else {
			intent.putExtra(NetworkUtils.MESSAGE_INFO, msg);
		}

		mLocalBroadcastManager.sendBroadcast(intent);

	}

	@Override
	public void processJoinedMatchMessage(String msg, boolean status) {
		if (status) {

			String[] joinedUsers = NetworkUtils.stringSplit(msg,
					NetworkUtils.ARRAY_SEPERATOR);

			String[] params;
			TLMNPlayer player;
			for (int i = 0; i < joinedUsers.length; i++) {

				params = NetworkUtils.stringSplit(joinedUsers[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				player = new TLMNPlayer();
				player.isTableMaster = false;
				player.id = Long.parseLong(params[0]);
				player.character = params[1];
				player.avatarId = Integer.parseInt(params[2]);
				player.cash = Long.parseLong(params[3]);
				player.vipId = Integer.parseInt(params[4]);

				player.state = PlayerState.WAITING;
				player.isReady = false;
				// player.isReady = true;
				getPlayerList().add(player);

				Log.d("TLMNGame", "Show player joined");
				if (BaseXeengGame.INSTANCE != null
						&& BaseXeengGame.INSTANCE.isReadyShow()) {
					Log.d("TLMNGame", "Show player joined called");
					BaseXeengGame.INSTANCE.showPlayerJoined(player.id);
				} else {
					Log.d("TLMNGame", "Show player joined enqueued");
					enqueue(new XEShowPlayerJoinedAction(player.id));
				}
			}

			logPlayerList();
			player = null;
			params = null;
			joinedUsers = null;
		}
	}

	public void processGetPokerMessage(String msg, boolean status) {

		if (status) {

			// force change state of main player to PLAYING
			getMainPlayer().isReady = true;
			changeStatePlaying();

			String[] values = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			long beginUserId = Long.parseLong(values[0]);
			dutyType = Integer.parseInt(values[2]);

			setDutyDescription();

			currentTurn = getPlayerIndex(beginUserId);
			mNextPlayerId = beginUserId;

			getMainPlayer().cardList = parseCardsArray(values[1]);

			for (TLMNPlayer player : getPlayerList()) {
				player.numHand = mNumHandOfPlayer;
			}

			Log.d("TLMNGame", "ShowGetPocker");
			if (BaseXeengGame.INSTANCE != null
					&& BaseXeengGame.INSTANCE.isReadyShow()) {
				Log.d("TLMNGame", "ShowGetPocker called");
				((TLMNGameActivity) BaseXeengGame.INSTANCE).showGetPocker();
			} else {
				Log.d("TLMNGame", "ShowGetPocker enqueued");
				enqueue(new BaseXEGameAction(
						BaseXEGameAction.ACTION_TLMN_SHOW_GET_POCKER));
			}
		}
	}

	@Override
	public void processEndMatchMessage(String msg, boolean status) {

		state = GameState.RESULT;
		if (status) {

			String[] values = NetworkUtils.stringSplit(msg,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);
			String[] first = NetworkUtils.stringSplit(values[0],
					NetworkUtils.ELEMENT_SEPERATOR);

			mWinPlayerId = Long.parseLong(first[0]);
			int perfectType = Integer.parseInt(first[1]);

			if (perfectType > 0) {

				switch (perfectType) {
				case 7:
					mWinType = WinType.TU_QUY_HAI;
					break;
				case 9:
					mWinType = WinType.DOI_THONG_5;
					break;
				case 10:
					mWinType = WinType.DOI_THONG_6;
					break;
				case 11:
					mWinType = WinType.SANH_RONG;
					break;
				default:
					mWinType = WinType.TOI_TRANG;
					break;
				}
			} else {
				mWinType = WinType.NORMAL;
			}

			// get perfect type string
			String[] roomParams = NetworkUtils.stringSplit(values[1],
					NetworkUtils.ARRAY_SEPERATOR);
			String[] userResult;

			// Initialize variable outside for loop to decrease memory
			// collector
			long userId = -1;
			boolean isOut = false;
			boolean isBankrupt = false;
			TLMNPlayer player;
			long money = 0;
			String cardStr = null;

			for (int i = 0; i < roomParams.length; i++) {

				userResult = NetworkUtils.stringSplit(roomParams[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				userId = Long.parseLong(userResult[0]);
				isOut = "1".equalsIgnoreCase(userResult[4]);
				isBankrupt = "1".equalsIgnoreCase(userResult[5]);
				money = Long.parseLong(userResult[1]);
				long playerCash = Long.parseLong(userResult[6]);

				player = (TLMNPlayer) getPlayer(userId);
				if (isBankrupt || isOut) {

					player.isOut = true;
					if (mainPlayer.id == userId) {
						mainPlayer.isOut = true;
					}
				}

				if (userId != mWinPlayerId) {
					cardStr = userResult[3];
				} else {
					if (perfectType == 0) {
						player.numHand = 0;
					}
				}

				if (cardStr != null) {
					player.cardList = parseCardsArray(cardStr);
				}

				player.state = PlayerState.RESULT;
				player.cardList = parseCardsArray(userResult[3]);
				player.rewardCash = money;
				player.cash = playerCash < 0 ? 0 : playerCash;
				player.isReady = false;
				// player.isReady = true;
				player.numHand = player.cardList.size();

				if (player.id == GameData.shareData().getMyself().id) {
					GameData.shareData().getMyself().cash = playerCash < 0 ? 0
							: playerCash;
				}
			}

			if (first.length > 3) {

				// Third element of first array is last turn cards
				if (first[3] != null && first[3].length() > 0) {

					Vector<Card> cards = parseCardsArray(first[3]);
					TLMNPlayer winner = (TLMNPlayer) getPlayer(mWinPlayerId);
					if (mWinPlayerId != mainPlayer.id) {

						if (winner != null) {

							winner.numHand -= cards.size();
						} else {

							Log.e(getClass().getSimpleName(), new StringBuffer(
									"Can not find the winner with id: ")
									.append(mWinPlayerId).toString());
							logPlayerList();
						}
					}

					lastCards.add(cards);

					endMatchType = FightCardType.NOTHING;
					if (cards != null) {

						final int fightTypeValue = cards.size();
						if (fightTypeValue == FightCardType.DOI_THONG_3
								.getTotalCard()) {

							endMatchType = FightCardType.DOI_THONG_3;
						} else if (fightTypeValue == FightCardType.DOI_THONG_4
								.getTotalCard()) {

							endMatchType = FightCardType.DOI_THONG_4;
						} else if (fightTypeValue == FightCardType.DOI_THONG_5
								.getTotalCard()) {

							endMatchType = FightCardType.DOI_THONG_5;
						} else if (fightTypeValue == FightCardType.DOI_THONG_6
								.getTotalCard()) {

							endMatchType = FightCardType.DOI_THONG_6;
						} else if (fightTypeValue == FightCardType.TU_QUY
								.getTotalCard()) {

							endMatchType = FightCardType.TU_QUY;
						}
					}
				}
			}

			if (first.length > 4) {
				long newTableMasterId = Long.parseLong(first[4]);
				if (newTableMasterId > 0) {
					updateTableMaster(newTableMasterId);
				}
			}

			if (BaseXeengGame.INSTANCE != null
					&& BaseXeengGame.INSTANCE.isReadyShow()) {
				getGameActivity().showEndMatch(true);
			} else {
				enqueue(new BaseXEGameAction(
						BaseXEGameAction.ACTION_SHOW_END_MATCH));
			}
		}
	}

	@Override
	public void processSettingMatchMessage(String msg, boolean status) {

		if (status) {

			String settingInfo[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			long minCash = Long.parseLong(settingInfo[0]);
			int max = Integer.parseInt(settingInfo[1]);
			if (settingInfo.length > 2) {

				isHideCard = "1".equalsIgnoreCase(settingInfo[2]);
			}
			((CardGameTable) currentTable).changeSetting(minCash, max);
			setWaitForAllPlayer();
		} else {

		}

		getGameActivity().showSettingChanged(status, msg);
	}

	@Override
	public void processReconnectionMessage(String msg, boolean status) {

		if (status) {

			prepareForNewGame();

			String[] values = NetworkUtils.stringSplit(msg,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);
			String[] first = NetworkUtils.stringSplit(values[0],
					NetworkUtils.ELEMENT_SEPERATOR);

			boolean isPlaying = "1".equalsIgnoreCase(first[1]);
			long _minBet = Long.parseLong(first[0]);
			long currentMatchId = Long.parseLong(first[7]);

			state = isPlaying ? GameState.PLAYING : GameState.WAITING;
			currentBetCash = _minBet;
			matchId = currentMatchId;
			GameData.shareData().currentTableNumber = (int) matchId;

			int maxPlayers = currentTable == null ? getMaxPlayers()
					: currentTable.getMaxPlayer();
			GameData.shareData().currentTableNumber = currentTable == null ? 0
					: (int) currentTable.getId();
			GameData.shareData().minBetCash = currentTable == null ? 0
					: currentTable.getMinBetCash();

			CardGameTable table = new CardGameTable(
					GameData.shareData().currentTableNumber);

			table.update(matchId, 1, maxPlayers, currentBetCash,
					GameData.shareData().minBetCash);

			setCurrentTable(table);

			String[] roomParams = NetworkUtils.stringSplit(values[1],
					NetworkUtils.ARRAY_SEPERATOR);
			String[] userParams;
			long userId = -1;
			boolean isObserver = false;

			for (int i = 0; i < roomParams.length; i++) {

				userParams = NetworkUtils.stringSplit(roomParams[i],
						NetworkUtils.ELEMENT_SEPERATOR);

				TLMNPlayer player = new TLMNPlayer();
				userId = Long.parseLong(userParams[0]);
				player.id = userId;
				player.character = userParams[1];
				player.avatarId = Integer.parseInt(userParams[2]);
				player.cash = Long.parseLong(userParams[3]);
				player.isReady = "1".equalsIgnoreCase(userParams[4]);
				// player.isReady = true;
				if (i == 0) {

					player.isTableMaster = true;
					player.isReady = false;
				}

				isObserver = "1".equalsIgnoreCase(userParams[5]);
				player.vipId = Integer.parseInt(userParams[6]);

				if (isObserver) {
					player.state = PlayerState.WAITING;
				} else {
					if (state == GameState.PLAYING) {
						player.state = PlayerState.PLAYING;
					} else {
						player.state = PlayerState.WAITING;
					}
				}

				if (userParams.length > 7) {
					player.numHand = Integer.parseInt(userParams[7]);
				}

				if (userId == GameData.shareData().getMyself().id) {
					if (first.length > 6) {
						Logger.getInstance().info(this,
								"card list Str: " + first[6]);
						if (!first[6].equalsIgnoreCase("null")) {

							player.cardList = parseCardsArray(first[6]);
							player.numHand = player.cardList.size();
						}
					}

					mainPlayer = player;
				}

				getPlayerList().add(player);
			}

			logPlayerList();

			if (first.length > 2) {
				/*
				 * try {
				 * 
				 * dutyType = Integer.parseInt(first[3]); setDutyDescription();
				 * } catch (NumberFormatException nfe) {
				 * 
				 * Logger.getInstance().error(this,
				 * "Value of dutyType not a number"); }
				 */
				lastCards.clear();
				if (!"0".equalsIgnoreCase(first[5])) { // isNewRound
					Vector<Card> cards = parseCardsArray(first[5]);
					if (cards != null) {

						lastCards.add(cards);
					}
				}

				long nextUserId = Long.parseLong(first[4]);
				mNextPlayerId = nextUserId;
				currentTurn = getPlayerIndex(nextUserId);
				//Compare my id with next player id to enable/disable button
				if(GameData.shareData().getMyself().id == mNextPlayerId){
					getGameActivity().enableFight();
				} else {
					getGameActivity().disableTurnFunc();
				}
			}

			if (getGameActivity() != null) {
				getGameActivity().showReconnection(status);
			} else {
				Intent intent = new Intent(MessageService.INTENT_RECONNECTION);
				intent.putExtra(NetworkUtils.MESSAGE_INFO, msg);
				intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

				mLocalBroadcastManager.sendBroadcast(intent);
			}
		}
	}

	@Override
	public TLMNPlayer addPlayerToList(long id, String name, long cash,
			boolean istablemaster) {

		TLMNPlayer player = new TLMNPlayer(id, name, cash, istablemaster);

		getPlayerList().add(player);

		return player;
	}

	@Override
	public TLMNPlayer getMainPlayer() {

		return (TLMNPlayer) mainPlayer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vector<TLMNPlayer> getPlayerList() {

		return (Vector<TLMNPlayer>) playerList;
	}

	@Override
	public void prepareForNewGame() {

		super.prepareForNewGame();
		playerList = new Vector<TLMNPlayer>();
		lastCards.clear();
		mWinType = WinType.NORMAL;
		mWinPlayerId = -1;
		mMissionType = MissionType.NOTHING;
		isHideCard = false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vector<Card> parseCardsArray(String cardStr) {
		return (Vector<Card>) super.parseCardsArray(cardStr);
	}

	@Override
	protected void prepareForNewMatch() {

		lastCards.clear();
		mWinType = WinType.NORMAL;
		mWinPlayerId = -1;
		mMissionType = MissionType.NOTHING;
		endMatchFightCase = null;
		endMatchType = null;
		mNextPlayerId = -1;
		prevPlayerId = -1;
	}

	@Override
	public TLMNGameActivity getGameActivity() {
		return (TLMNGameActivity) super.getGameActivity();
	}

	private void processMatchTurnMesasge(String msg, boolean status) {

		if (status) {

			String[] values = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			final boolean isSkip = "1".equalsIgnoreCase(values[4]);
			long currentUserId = Long.parseLong(values[0]);
			long nextUserId = Long.parseLong(values[1]);
			final boolean isNewRound = "1".equalsIgnoreCase(values[5]);
			HashMap<String, Long> fightMaps = null;
			FightCardType fightType = FightCardType.NOTHING;

			TLMNPlayer currentPlayer = (TLMNPlayer) getPlayer(currentUserId);
			if (currentPlayer != null) {
				// currentPlayer.isTasked = "1".equalsIgnoreCase(values[3]);
				currentPlayer.isTasked = false;
				if (currentPlayer.state != PlayerState.PLAYING) {
					currentPlayer.state = PlayerState.PLAYING;
				}
			}

			if (isNewRound) {

				lastCards.clear();
			}

			Vector<Card> turnCards = null;
			if (!isSkip) {

				turnCards = parseCardsArray(values[2]);
				lastCards.add((Vector<Card>) sortCardList(turnCards));

				if (currentPlayer != null) {

					if (currentPlayer.id != mainPlayer.id) {

						currentPlayer.numHand -= turnCards.size();
					} else {

						currentPlayer.numHand -= turnCards.size();
						for (Card card : turnCards) {

							currentPlayer.cardList.remove(card);
						}
					}
				}
			}

			prevPlayerId = currentUserId;
			mNextPlayerId = nextUserId;

			if (values.length > 6) {

				fightMaps = new HashMap<String, Long>();

				long be_fighten_id = Long.parseLong(values[6]);
				long fight_id = Long.parseLong(values[7]);

				String moneyInfo[] = values[8].split("#");

				long gainMoney = Long.parseLong(values[8].split("#")[0]);
				long lostMoney = gainMoney;

				if (moneyInfo.length > 1) {
					lostMoney = Long.parseLong(values[8].split("#")[1]);
				}
				long pre_be_fighten_id = -1;
				long oldMoney = -1;

				if ("1".equalsIgnoreCase(values[9])) {
					pre_be_fighten_id = Long.parseLong(values[10]);
					oldMoney = Long.parseLong(values[11]);
				}

				fightMaps.put(TLMNGame.BE_FIGHTEN_ID, be_fighten_id);
				fightMaps.put(TLMNGame.FIGHT_ID, fight_id);
				fightMaps.put(TLMNGame.LOST_MONEY, lostMoney);
				fightMaps.put(TLMNGame.GAIN_MONEY, gainMoney);
				if (pre_be_fighten_id > -1) {

					fightMaps
							.put(TLMNGame.PRE_BE_FIGHTEN_ID, pre_be_fighten_id);
					fightMaps.put(TLMNGame.OLD_MONEY, oldMoney);
				}

				if (turnCards != null) {

					final int fightTypeValue = turnCards.size();
					if (fightTypeValue == FightCardType.DOI_THONG_3
							.getTotalCard()) {

						fightType = FightCardType.DOI_THONG_3;
					} else if (fightTypeValue == FightCardType.DOI_THONG_4
							.getTotalCard()) {

						fightType = FightCardType.DOI_THONG_4;
					} else if (fightTypeValue == FightCardType.DOI_THONG_5
							.getTotalCard()) {

						fightType = FightCardType.DOI_THONG_5;
					} else if (fightTypeValue == FightCardType.DOI_THONG_6
							.getTotalCard()) {

						fightType = FightCardType.DOI_THONG_6;
					} else if (fightTypeValue == FightCardType.TU_QUY
							.getTotalCard()) {

						fightType = FightCardType.TU_QUY;
					}

				}
			}

			final HashMap<String, Long> finalFightMaps = fightMaps;
			final FightCardType finalFightType = fightType;

			currentTurn = getPlayerIndex(nextUserId);

			// Log.d("TLMNGame", "ShowTurn");
			if (BaseXeengGame.INSTANCE != null
					&& BaseXeengGame.INSTANCE.isReadyShow()) {
				((TLMNGameActivity) BaseXeengGame.INSTANCE).showTurn(
						isNewRound, isSkip, finalFightMaps, finalFightType,
						turnCards);
			} else {
				enqueue(new XETLMNShowTurnAction(isNewRound, isSkip, fightMaps,
						fightType, prevPlayerId, mNextPlayerId, turnCards));
				Log.d("RedrawGame", "ShowTurn enqueued");
			}

		}
	}

	private void setDutyDescription() {

		switch (dutyType) {
		case 1:
			mMissionType = MissionType.WIN_X2;
			break;
		case 2:
			mMissionType = MissionType.A_X2;
			break;
		case 3:
			mMissionType = MissionType.AA_X2;
			break;
		case 4:
			mMissionType = MissionType.ABC_X2;
			break;
		case 5:
			mMissionType = MissionType.ABCD_X2;
			break;
		case 6:
			mMissionType = MissionType.AAA_X3;
			break;
		case 7:
			mMissionType = MissionType.ABCDE_X3;
			break;
		case 8:
			mMissionType = MissionType.ABCDEF_X3;
			break;
		default:
			break;
		}
	}

	protected Comparator<Card> compareByValue = new Comparator<Card>() {

		@Override
		public int compare(Card lhs, Card rhs) {

			if (lhs.getCardValueInTLMN() < rhs.getCardValueInTLMN()) {
				return -1;
			} else if (lhs.getCardValueInTLMN() > rhs.getCardValueInTLMN()) {
				return 1;
			} else {
				if (lhs.getCardTypeInTLMN() < rhs.getCardTypeInTLMN()) {
					return -1;
				} else if (lhs.getCardTypeInTLMN() > rhs.getCardTypeInTLMN()) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	};

	protected Comparator<Card> compareByType = new Comparator<Card>() {

		@Override
		public int compare(Card o1, Card o2) {

			if (o1.getCardTypeInTLMN() < o2.getCardTypeInTLMN()) {

				return -1;
			} else if (o1.getCardTypeInTLMN() > o2.getCardTypeInTLMN()) {

				return 1;
			} else {

				if (o1.getCardValueInTLMN() < o2.getCardValueInTLMN()) {

					return -1;
				} else {

					return 1;
				}
			}
		}
	};

	@Override
	public int getMaxPlayers() {
		return 4;
	}
}
