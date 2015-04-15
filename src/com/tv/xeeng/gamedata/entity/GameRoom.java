package com.tv.xeeng.gamedata.entity;

public abstract class GameRoom {

	protected long roomId;
	protected int numPlayer;
	protected long betCash;
	protected int maxPlayer;
	protected int roomType;

	public GameRoom() {

	}

	public int getRoomType() {
		return roomType;
	}

	public long getRoomId() {

		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public long getBetCash() {

		return betCash;
	}

	public int getNumPlayer() {
		return numPlayer;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}
}
