package com.tv.xeeng.gamedata.entity;

public class GameTable {

	public static final long INVALID_TABLE_ID = -1;

	protected long tableId;
	protected long minBet;
	protected long currentBet;
	protected int order;
	protected int activePlayer;
	protected int maxPlayer;
	protected String title;
	protected float percent;
	protected String fraction;
	protected int zoneId;

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public long getId() {

		return tableId;
	}

	public long getMinBetCash() {

		return minBet;
	}

	public int getOrder() {

		return order;
	}

	public int getActivePlayer() {

		return activePlayer;
	}

	public int getMaxPlayer() {

		return maxPlayer;
	}

	public String getTitle() {

		return title;
	}

	public String getFraction() {

		return fraction;
	}

	public float getPercent() {

		return percent;
	}

	public long getCurrentBet() {
		return currentBet;
	}

	public void setCurrentBet(long currentBet) {
		this.currentBet = currentBet;
	}
}
