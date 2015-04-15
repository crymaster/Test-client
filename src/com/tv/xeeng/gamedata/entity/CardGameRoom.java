package com.tv.xeeng.gamedata.entity;

import com.tv.xeeng.gamedata.GameData;

public class CardGameRoom extends BaseGameRoom {

	public String name;

	public CardGameRoom(CardGameRoom pRoom) {

		roomId = pRoom.getRoomId();
		numPlayer = pRoom.getNumPlayer();
		maxPlayer = pRoom.getMaxPlayer();
		betCash = pRoom.getBetCash();
		name = pRoom.name;
	}

	public CardGameRoom(int mRoomType, int betType, long id, int numPlayer) {

		roomId = id;
		this.numPlayer = numPlayer;
		roomType = mRoomType;
		computeMinBetCashByType(betType);
		computeNameByType(roomType);
		computeMaxPlayerByGameId(GameData.shareData().gameId);
	}

	private void computeMaxPlayerByGameId(long _roomId) {
		
	}

	private void computeNameByType(int roomType) {

		if (roomType > 3) {

			name = "VIP";
		} else if (roomType > 1) {

			name = "ĐẠI GIA";
		} else if (roomType > -1) {

			name = "TẬP SỰ";
		}
	}

	private void computeMinBetCashByType(int betType) {

		if (betType > 3) {

			betCash = 10000;
		} else if (betType > 1) {

			betCash = 2000;
		} else if (betType > -1) {

			betCash = 50;
		}
	}
}
