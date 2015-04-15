package com.tv.xeeng.dataLayer.game;

import android.content.Intent;
import android.util.Log;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.game.actions.XEShowPlayerReadyAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowStartGameFailedAction;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.*;
import com.tv.xeeng.manager.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class BaseCardGame extends BaseGame {

	@Override
	public void processSpecificMessage(final int mId, final String msg,
			final boolean status) {
		switch (mId) {
		case MessagesID.MATCH_START:
			// processStartGameMessage(msg, status);
			if (!status) {
				Log.d("BaseCardGame", "Fail to start game with msg: " + msg);

				if (BaseXeengGame.INSTANCE != null
						|| !BaseXeengGame.INSTANCE.isReadyShow()) {
					BaseXeengGame.INSTANCE.showStartGameFailed(msg);
				} else {
					enqueue(new XEShowStartGameFailedAction(msg));
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void processCreateNewTableMessage(String msg, boolean status) {
		super.processCreateNewTableMessage(msg, status);
	}

	@Override
	public void processJoinMatchMessage(String msg, boolean status) {
		super.processJoinMatchMessage(msg, status);
	}

	@Override
	public void processReadyMatchMessage(String msg, boolean status) {

		if (status) {

			String playerInfo[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			final long id = Long.parseLong(playerInfo[0]);
			boolean isReady = "1".equalsIgnoreCase(playerInfo[1]);
			setReadyForPlayer(id, isReady);
			// setReadyForPlayer(id, true);

			logPlayerList();

			if (BaseXeengGame.INSTANCE != null
					&& BaseXeengGame.INSTANCE.isReadyShow()) {
				Log.d("BaseCardGame", "Show player ready called");
				BaseXeengGame.INSTANCE.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						BaseXeengGame.INSTANCE.showPlayerReady(id);
					}
				});
			} else {
				Log.d("BaseCardGame", "Show player ready enqueued");
				enqueue(new XEShowPlayerReadyAction(id));
			}
		} else {

			// nothing to do
		}
	}

	@Override
	public void processEnterZoneMessage(String msg, boolean status) {

		Log.i("CardGame", "processEnterZoneMessage");

		Intent enterZone = new Intent(MessageService.INTENT_ENTER_ZONE);
		enterZone.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		if (status) {

			String value[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ARRAY_SEPERATOR);
			prepareForEnterZone();
			for (String roomStr : value) {

				String roomInfo[] = NetworkUtils.stringSplit(roomStr,
						NetworkUtils.ELEMENT_SEPERATOR);

				Log.i("CardGame", "roomInfo length: " + roomInfo.length);

				if (roomInfo.length < 2) {
					continue;
				}

				CardGameRoom room = new CardGameRoom(
						Integer.parseInt(roomInfo[2]) - 1,
						Integer.parseInt(roomInfo[2]) - 1,
						Long.parseLong(roomInfo[0]),
						Integer.parseInt(roomInfo[1]));

				getRoomList().add(room);
			}
		} else {

			enterZone.putExtra(NetworkUtils.MESSAGE_INFO, msg);
		}

		mLocalBroadcastManager.sendBroadcast(enterZone);
	}

	@Override
	public void processGetTableMessage(String msg, boolean status) {

		Log.d("BaseCardGame", "Content: " + msg);
		Intent getTable = new Intent(MessageService.INTENT_GET_TABLES);
		getTable.putExtra(NetworkUtils.MESSAGE_STATUS, status);
		if (status) {

			prepareForEnterRoom();
			String values[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ARRAY_SEPERATOR);

			GameData.shareData().getGame().tableList.clear();

			for (String value : values) {

				String tableInfo[] = NetworkUtils.stringSplit(value,
						NetworkUtils.ELEMENT_SEPERATOR);
				long tableId = Long.parseLong(tableInfo[3]);
				int index = Integer.parseInt(tableInfo[0]);
				int activePlayer = Integer.parseInt(tableInfo[1]);
				int maxPlayer = Integer.parseInt(tableInfo[4]);
				long minBet = Long.parseLong(tableInfo[2]);
				String title = tableInfo[6];
				int zoneId = Integer.parseInt(tableInfo[7]);
				CardGameTable table = new CardGameTable(index);
				table.update(tableId, activePlayer, maxPlayer, minBet,
						GameData.shareData().minBetCash, title, zoneId);

				GameData.shareData().getGame().tableList.add(table);
			}
		} else {

			getTable.putExtra(NetworkUtils.MESSAGE_INFO, msg);
		}

		mLocalBroadcastManager.sendBroadcast(getTable);
	}

	@Override
	public void processSettingMatchMessage(String msg, boolean status) {

		if (status) {

			String settingInfo[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			long minCash = Long.parseLong(settingInfo[0]);
			int max = Integer.parseInt(settingInfo[1]);
			((CardGameTable) currentTable).changeSetting(minCash, max);
			setWaitForAllPlayer();
		} else {

		}

		super.processSettingMatchMessage(msg, status);
	}

	public Vector<? extends Card> parseCardsArray(String cardStr) {
		String[] cardValues = NetworkUtils.stringSplit(cardStr, "#");
		Vector<Card> result = new Vector<Card>();

		for (int i = 0; i < cardValues.length; i++) {
			Card card = new Card(Integer.parseInt(cardValues[i]));
			result.add(card);
		}

		return result;
	}

	public static String buildCardStr(List<Card> cards) {

		if (cards == null || cards.size() == 0) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		Card card;
		for (int i = 0; i < cards.size(); i++) {

			card = cards.get(i);
			if (i < cards.size() - 1) {

				buffer.append(card.serverValue).append("#");
			} else {

				buffer.append(card.serverValue);
			}
		}

		return buffer.toString();
	}

	protected void processReconnectionError(String errorMsg) {

	}

	@Override
	public boolean isValidStartGame() {

		if (playerList.size() < 2)
			return false;
		boolean isValid = true;
		for (Player pd : playerList) {

			if (pd.isTableMaster)
				continue;
			if (!pd.isReady) {

				isValid = false;
				break;
			}
		}

		return isValid;
	}

	@Override
	protected void prepareForEnterZone() {

		roomList = new ArrayList<CardGameRoom>();
	}

	@Override
	protected void prepareForEnterRoom() {

		tableList = new ArrayList<GameTable>();
		// CardGameTable atable;
		// for(int i = 0; i < GameData.MAX_CAPACITY; i++) {
		//
		// atable = new CardGameTable(i);
		// getTableList().add(atable);
		// }
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<CardGameTable> getTableList() {

		return (ArrayList<CardGameTable>) super.getTableList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<CardGameRoom> getRoomList() {

		return (ArrayList<CardGameRoom>) roomList;
	}

	@Override
	public CardGameRoom getCurrentRoom() {

		return (CardGameRoom) currentRoom;
	}

	@Override
	public void setCurrentRoom(BaseGameRoom aroom) {

		currentRoom = new CardGameRoom((CardGameRoom) aroom);
	}

	@Override
	public CardGameTable getCurrentTable() {

		return (CardGameTable) currentTable;
	}

	@Override
	public void setCurrentTable(GameTable atable) {

		currentTable = atable;
	}

	public void printCardListMainPlayer() {

		@SuppressWarnings("unchecked")
		Vector<Card> list = (Vector<Card>) ((BaseCardGamePlayer) getMainPlayer())
				.getCardList();
		printCardList(list);
	}

	public void printCardList(Vector<Card> pList) {

		if (pList != null) {

			for (int i = 0; i < pList.size(); i++) {

				Logger.getInstance().info(
						this,
						"Card with value: " + pList.get(i).value + "  type: "
								+ pList.get(i).type);
			}
		}
	}

}
