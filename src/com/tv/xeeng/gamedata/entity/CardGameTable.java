package com.tv.xeeng.gamedata.entity;

public class CardGameTable extends GameTable {

	public static final String HIDE_POCKER_KEY = "hide.pocker";
	public static final String AN_PHOM_KEY = "an.phom";
	public static final String TAI_PHOM_KEY = "tai.phom";

	public String name;

	public CardGameTable(int _index) {

		order = _index;
		title = "" + order + 1;
		tableId = INVALID_TABLE_ID;
	}

	public void update(long tableid, int numplayer, int maxplayer,
			long pCurrentbetcash, long pMinBet) {

		tableId = tableid;
		activePlayer = numplayer;
		maxPlayer = maxplayer;
		minBet = pMinBet;
		currentBet = pCurrentbetcash;
		if (minBet == 0 || minBet > currentBet) {

			minBet = currentBet;
		}
	}

	public void update(long tableId, int numPlayer, long currentBetCash) {
		this.update(tableId, numPlayer, maxPlayer, currentBetCash, minBet);
	}

	public void update(long tableid, int numplayer, int maxplayer,
			long pCurrentbetcash, long pMinBet, String title, int zoneId) {

		tableId = tableid;
		activePlayer = numplayer;
		maxPlayer = maxplayer;
		minBet = pMinBet;
		currentBet = pCurrentbetcash;
		this.title = title;
		this.zoneId = zoneId;
		if (minBet == 0 || minBet > currentBet) {

			minBet = currentBet;
		}
	}

	public void changeSetting(long pBetCash, int pMaxPlayer) {

		currentBet = pBetCash;
		maxPlayer = pMaxPlayer;
	}
}
