package com.tv.xeeng.dataLayer.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.game.actions.BaseXEGameAction;
import com.tv.xeeng.dataLayer.game.actions.XEPlayerKickedAction;
import com.tv.xeeng.dataLayer.game.actions.XEPlayerLeftAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowLeaveMatchFailedAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowMainPlayerIsKickedAction;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.player.BasePlayerHolder;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.BaseGameRoom;
import com.tv.xeeng.gamedata.entity.CardGameTable;
import com.tv.xeeng.gamedata.entity.ChatItemData;
import com.tv.xeeng.gamedata.entity.GameTable;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gamedata.entity.Player.PlayerState;
import com.tv.xeeng.manager.Logger;

public abstract class BaseGame {

	private KickOutReason kickedReason = KickOutReason.NONE;

	public KickOutReason getKickedReason() {
		return kickedReason;
	}

	public void setKickedReason(KickOutReason reason) {
		this.kickedReason = reason;
	}

	private String kickedMessage;

	public String getKickedMessage() {
		return kickedMessage;
	}

	public void setKickedMessage(String msg) {
		kickedMessage = msg;
	}

	private boolean _hasPreviousMatch = false;

	public void setHasPreviousMatch(boolean val) {
		_hasPreviousMatch = val;
	}

	public boolean hasPreviousMatch() {
		return _hasPreviousMatch;
	}

	public enum GameState {

		NOTHING, WAITING, PLAYING, RESULT
	}

	public enum KickOutReason {

		TABLE_MASTER_KICK("Bạn đã bị chủ bàn đá ra ngoài"), BANKRUPT(
				"Số tiền của bạn không đủ để chơi."), SERVER_MESSAGE(""), TIME_OUT_TABLE_MASTER_ROLE(
				"Bạn bị đẩy ra ngoài vì lâu không bắt đầu"), TIME_OUT_READY_ROLE(
				"Bạn bị đẩy ra ngoài vì lâu không sẵn sàng"), NONE("null state");

		final String message;

		KickOutReason(String msg) {
			message = msg;
		}

		public String toString() {
			return message;
		}
	}

	private BaseXeengGame mgameActivity = null;
	protected ArrayList<? extends BaseGameRoom> roomList;
	protected ArrayList<GameTable> tableList;
	protected volatile Vector<? extends Player> playerList;
	protected ArrayList<ChatItemData> chatList;
	protected long matchId;
	protected long currentBetCash;
	protected LocalBroadcastManager mLocalBroadcastManager;
	public GameState state;
	public Player mainPlayer;
	protected BaseGameRoom currentRoom;
	protected GameTable currentTable;
	protected static BaseGame instance;
	public boolean isTrainingMode;
	public boolean isRejectConfirmStopTraining;
	public Class<?> activityClass;
	public String gameName;

	protected Queue<BaseXEGameAction> mActionQueue;
	protected Queue<Runnable> runnableQueue; // Upgraded version of action queue

	private static final int MAX_ACTION_QUEUE_SIZE = 100;

	protected BaseGame() {
		try {
			mActionQueue = new ArrayBlockingQueue<BaseXEGameAction>(
					MAX_ACTION_QUEUE_SIZE);
			runnableQueue = new ArrayBlockingQueue<Runnable>(
					MAX_ACTION_QUEUE_SIZE);

			GameData.shareData().setGame(this);
			GameData.shareData().isReadyGameTimeOut = false;
			isTrainingMode = false;
			isRejectConfirmStopTraining = false;
			mLocalBroadcastManager = LocalBroadcastManager
					.getInstance(CustomApplication.shareApplication());
		} catch (Exception e) {
		}
	}

	public void resetActionQueue() {
		mActionQueue.clear();
	}

	public boolean enqueue(BaseXEGameAction action) {
		return mActionQueue.offer(action);
	}

	public BaseXEGameAction dequeue() {
		return mActionQueue.poll();
	}

	public int getQueueLength() {
		return mActionQueue.size();
	}
	
	public Queue<Runnable> getRunnableQueue() {
		return runnableQueue;
	}

	/*
	 * process specific message of specific game
	 */
	public abstract void processSpecificMessage(int mId, String msg,
			boolean status);

	public abstract void processEnterZoneMessage(String msg, boolean status);

	public abstract void processGetTableMessage(String msg, boolean status);

	public abstract void processJoinedMatchMessage(String msg, boolean status);

	public abstract void processReadyMatchMessage(String msg, boolean status);

	public abstract void processEndMatchMessage(String msg, boolean status);

	public abstract void processReconnectionMessage(String msg, boolean status);

	// public abstract void processBaoSamMessage(String msg, boolean status);

	public void processSettingMatchMessage(String msg, boolean status) {
		getGameActivity().showSettingChanged(status, msg);
	}

	public void processCreateNewTableMessage(String msg, boolean status) {
		Intent intent = new Intent(MessageService.INTENT_CREATE_NEW_TABLE);
		intent.putExtra(NetworkUtils.MESSAGE_STATUS, status);

		if (status) {
			createNewTable(msg);
			// update match info
			String matchInfo[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			matchId = Long.parseLong(matchInfo[0]);
			currentBetCash = Long.parseLong(matchInfo[1]);

			String[] tokens = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);

			GameData.shareData().currentTableNumber = Integer
					.parseInt(tokens[0]);
			GameData.shareData().minBetCash = Long.parseLong(tokens[1]);

			int maxPlayers = currentTable == null ? getMaxPlayers()
					: currentTable.getMaxPlayer();

			CardGameTable table = new CardGameTable(0);
			table.update(matchId, 1, maxPlayers, currentBetCash,
					GameData.shareData().minBetCash);
			setCurrentTable(table);
			state = GameState.WAITING;

			GameSocket.shareSocket().changeTimeToPing(true);
		} else {
			GameData.shareData().getGame().setCurrentTable(null);
			intent.putExtra(NetworkUtils.MESSAGE_INFO, msg);
		}

		mLocalBroadcastManager.sendBroadcast(intent);
	}

	protected abstract void createNewTable(String msg);

	public void processJoinMatchMessage(String msg, boolean status) {

		if (status) {
			GameData.shareData().currentTableNumber = Integer
					.parseInt(NetworkUtils.stringSplit(msg,
							NetworkUtils.ELEMENT_SEPERATOR)[0]);
			GameData.shareData().minBetCash = Long.parseLong(NetworkUtils
					.stringSplit(msg, NetworkUtils.ELEMENT_SEPERATOR)[1]);
			GameSocket.shareSocket().changeTimeToPing(true);

			int maxPlayers = currentTable == null ? getMaxPlayers()
					: currentTable.getMaxPlayer();

			CardGameTable table = new CardGameTable(0);
			table.update(matchId, 1, maxPlayers, currentBetCash,
					GameData.shareData().minBetCash);
			setCurrentTable(table);
		}
		// Need override to process message
	}

	public void processUserLeaveMessage(String msg, boolean status) {

		if (status) {

			String values[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.DIFF_ARRAY_SEPERATOR);
			String leave[] = NetworkUtils.stringSplit(values[0],
					NetworkUtils.ELEMENT_SEPERATOR);
			long userId = Long.parseLong(leave[0]);
			if (userId == GameData.shareData().getMyself().id) {

				// i quit game, reset game data
				final BaseXeengGame gameAct = BaseXeengGame.INSTANCE;
				if (gameAct != null) {
					BasePlayerHolder playerHolder = gameAct.getPlayer(userId);
					if (playerHolder != null)
						playerHolder.playerSprite.stopTimer();

					switch (kickedReason) {
					case TABLE_MASTER_KICK:
					case BANKRUPT:
					case TIME_OUT_TABLE_MASTER_ROLE:
					case SERVER_MESSAGE:
						// case TIME_OUT_READY_ROLE:
						gameAct.showMainPlayerIsKickedDialog(kickedReason,
								kickedMessage);
						break;
					default:
						gameAct.finish();
						break;
					}
				} else {
					enqueue(new XEShowMainPlayerIsKickedAction(kickedReason,
							kickedMessage));
				}
				kickedReason = KickOutReason.NONE;
				kickedMessage = "";
				cleanWhenLeaveMatch();
				GameSocket.shareSocket().changeTimeToPing(false);
				prepareForEnterRoom();
			} else {

				// first, remove leftPlayer out playerlist
				removePlayer(userId);

				// check if table master was changed
				long newUserId = Long.parseLong(leave[1]);
				if (newUserId > 0) {

					updateTableMaster(newUserId);
				}
				logPlayerList();

				if (BaseXeengGame.INSTANCE != null
						&& BaseXeengGame.INSTANCE.isReadyShow()) {
					BaseXeengGame.INSTANCE.showPlayerLeft(userId);
				} else {
					enqueue(new XEPlayerLeftAction(userId));
				}
			}
		} else {
			if (BaseXeengGame.INSTANCE != null
					&& BaseXeengGame.INSTANCE.isReadyShow()) {
				BaseXeengGame.INSTANCE.showLeaveMatchFailed(msg);
			} else {
				enqueue(new XEShowLeaveMatchFailedAction(msg));
			}
		}
	}

	public void processUserIsKickedMessage(String msg, boolean status) {
		Log.v("BaseGame", " kick out: " + msg + " || " + status);
		long id = -1;
		kickedReason = KickOutReason.NONE;
		kickedMessage = "";

		if (status) {
			String kickedPlayerInfo[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			id = Long.parseLong(kickedPlayerInfo[0]);
			if (id != GameData.shareData().getMyself().id) {

				removePlayer(id);
				kickedMessage = kickedPlayerInfo[2];
				logPlayerList();
			} else {

				// i was quit
				cleanWhenLeaveMatch();
				GameSocket.shareSocket().changeTimeToPing(false);
				if (Integer.parseInt(kickedPlayerInfo[3]) == 0) {

					kickedReason = KickOutReason.TABLE_MASTER_KICK;
				} else {

					if (Integer.parseInt(kickedPlayerInfo[5]) == 0) {

						kickedReason = KickOutReason.BANKRUPT;
					} else {

						kickedReason = KickOutReason.SERVER_MESSAGE;
						kickedMessage = kickedPlayerInfo[2];
					}
				}
				Log.d("BaseGame", "ShowPlayerKicked");
				// BusinessRequester.getInstance().leaveGame(this.getMatchId());
			}

			if (BaseXeengGame.INSTANCE != null
					&& BaseXeengGame.INSTANCE.isReadyShow()) {
				BaseXeengGame.INSTANCE.showPlayerKicked(id, kickedReason,
						kickedMessage);
				Log.d("BaseGame", "ShowPlayerKicked called");
			} else {
				enqueue(new XEPlayerKickedAction(id, kickedReason,
						kickedMessage));
				Log.d("BaseGame", "ShowPlayerKicked enqueued");
			}
			kickedReason = KickOutReason.NONE;
			kickedMessage = "";
		}
	}

	public void processChatMessage(String msg, boolean status) {

		if (status) {

			String chatInfo[] = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);
			long chatPlayerId = Long.parseLong(chatInfo[2]);
			String content = chatInfo[0];
			Player pd = getPlayer(chatPlayerId);
			String playerName = "player";
			if (pd != null) {

				playerName = pd.character;
			}
			getChatList().add(
					new ChatItemData(chatPlayerId, playerName, content));
			getGameActivity().showPlayerChat(chatPlayerId, content);
		}
	}

	/**
	 * @return exactly player object for a specific game
	 */
	public abstract Player addPlayerToList(long id, String name, long cash,
			boolean istablemaster);

	/**
	 * Note: when user enter a specific game, need initialize game
	 */
	protected abstract void prepareForEnterZone();

	/**
	 * @return exactly player object for a specific game
	 */
	public abstract Player getMainPlayer();

	public abstract Vector<? extends Player> getPlayerList();

	public abstract ArrayList<? extends BaseGameRoom> getRoomList();

	/**
	 * @return exactly object type for specific game
	 */
	public abstract BaseGameRoom getCurrentRoom();

	/**
	 * @return exactly object type for specific game
	 */
	public abstract GameTable getCurrentTable();

	public abstract void setCurrentRoom(BaseGameRoom aroom);

	public abstract void setCurrentTable(GameTable atable);

	protected abstract void prepareForNewMatch();

	public synchronized ArrayList<ChatItemData> getChatList() {

		return chatList;
	}

	public GameState getState() {
		return state;
	}

	public boolean isValidWatchDogTimer() {

		boolean result = true;

		if (playerList.size() < 2) {

			result = false;
		} else {

			result = isValidStartGame();
		}

		return result;
	}

	public long getMatchId() {

		return matchId;
	}

	public ArrayList<ChatItemData> cloneHistoryChatList() {

		ArrayList<ChatItemData> cloneChatList = new ArrayList<ChatItemData>();
		synchronized (chatList) {

			for (ChatItemData item : chatList) {
				cloneChatList.add(new ChatItemData(item));
			}
		}
		return cloneChatList;
	}

	public Player getPlayer(long id) {

		if (playerList == null) {
			Logger.getInstance().error(this, "playerList is null");
			return null;
		}
		if (id < 1) {
			Logger.getInstance().error(this, "player id is invalid");
			return null;
		}

		for (Player player : playerList) {
			if (id == player.id) {
				return player;
			}
		}
		return null;
	}

	public Player getTableMaster() {

		for (Player player : playerList) {
			if (player.isTableMaster) {
				return player;
			}
		}
		return null;
	}

	public boolean isValidStartGame() {
		for (Player player : playerList) {
			if (!player.isReady) {
				return false;
			}
		}
		return true;
	}

	public void setWaitForAllPlayer() {

		for (Player pd : playerList) {

			if (pd.isTableMaster)
				continue;
			pd.isReady = false;
		}
	}

	public void setReadyForPlayer(long id, boolean isready) {

		Player target = getPlayer(id);
		if (target != null) {

			target.isReady = isready;
			Logger.getInstance().info(
					this,
					"Set ready for player ID: " + target.id + " NAME: "
							+ target.character);
			// getGameActivity().checkGameStart();
		}
	}

	public void setRoomList(ArrayList<? extends BaseGameRoom> list) {

		if (list == roomList)
			return;

		roomList = list;
	}

	public BaseXeengGame getGameActivity() {

		return mgameActivity;
	}

	public void setGameActivity(BaseXeengGame pGameActivity) {

		mgameActivity = pGameActivity;
	}

	public ArrayList<? extends GameTable> getTableList() {

		return tableList;
	}

	public long getCurrentBetCash() {
		return currentBetCash;
	}

	public void clean() {

		instance = null;
	}

	public void cleanWhenLeaveMatch() {

		playerList.clear();
		if (chatList != null)
			chatList.clear();
		state = GameState.NOTHING;
		isTrainingMode = false;
		// override if need
	}

	/**
	 * @description Call when user click continuos button at result scene
	 */
	public boolean removePlayerIsKickIfHave() {

		boolean result = true;
		if (playerList == null)
			return false;
		@SuppressWarnings("unchecked")
		Iterator<Player> iter = (Iterator<Player>) playerList.iterator();
		while (iter.hasNext()) {

			Player pd = iter.next();
			if (pd.isOut || pd.isBankrupt) {

				if (pd.id == GameData.shareData().getMyself().id) {

					cleanWhenLeaveMatch();
					getGameActivity().showPlayerKicked(pd.id,
							KickOutReason.BANKRUPT, null);
					return false;
				}
				iter.remove();
			}
		}

		logPlayerList();
		return result;
	}

	public void prepareForNewGame() {

		GameData.shareData().isReadyGameTimeOut = false;
		state = GameState.WAITING;
		chatList = new ArrayList<ChatItemData>();

		// override if need
	}

	public void prepareForNextMatch() {
		state = GameState.WAITING;
		if (playerList != null) {

			for (Player pd : playerList) {
				pd.state = PlayerState.WAITING;
			}
			logPlayerList();
		}
	}

	public boolean changeStatePlaying() {

		if (state == GameState.PLAYING) {
			Log.d("RedrawGame", "Return because GameState is PLAYING");
			return false;
		}
		for (Player pd : playerList) {
			Log.d("RedrawGame", "Set player state to PLAYING " + pd.id);
			if (pd.state != PlayerState.OBSERVING) {
				pd.state = PlayerState.PLAYING;
			}
		}

		if (state != GameState.PLAYING) {

			prepareForNewMatch();
			logPlayerList();
		}
		state = GameState.PLAYING;
		return true;
	}

	protected void prepareForEnterRoom() {

		// default, do nothing
	}

	protected void removePlayer(long id) {

		synchronized (playerList) {
			for (int i = 0; i < playerList.size(); i++) {

				Player pd = playerList.get(i);
				if (id == pd.id) {

					playerList.remove(i);
					break;
				}
			}
		}
	}

	protected void updateTableMaster(long newTableMaster) {

		if (newTableMaster < 1)
			return;

		for (Player player : playerList) {

			if (newTableMaster == player.id) {
				player.isTableMaster = true;
			} else {
				player.isTableMaster = false;
			}
		}
	}

	public void updateMainPlayer() {

		GameData.shareData().getMyself().cash = mainPlayer.cash;
	}

	public int getPlayerIndex(long playerId) {
		Player player;
		for (int i = 0; i < playerList.size(); i++) {
			player = playerList.get(i);
			if (player.id == playerId) {
				player = null;
				return i;
			}
		}

		player = null;
		return -1;
	}

	protected void logPlayerList() {

		Logger.getInstance()
				.warn(this,
						"################################### player list ###################################");
		if (playerList != null) {

			ArrayList<Player> temp = new ArrayList<Player>(playerList);
			for (Player pd : temp) {

				Logger.getInstance().info(
						this,
						"Player with ID: " + pd.id + " NAME: " + pd.character
								+ " CASH: " + pd.cash + " TABLE_MASTER: "
								+ pd.isTableMaster + " READY: " + pd.isReady
								+ " STATE: " + pd.state.toString());
			}
		}
		// MyPagerAdapter ID_ZONE_PIKACHU
		Logger.getInstance()
				.warn(this,
						"###################################################################################");
	}

	public abstract int getMaxPlayers();
}
