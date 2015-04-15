package com.tv.xeeng.gamedata.entity;

import java.util.Vector;

public class TLMNPlayer extends BaseCardGamePlayer {

	public enum SortType {
		HORIZONTAL, VERTICAL;
	}

	public boolean isSkipThisTurn;
	public boolean isTasked;
	public SortType sortType;

	public TLMNPlayer() {

		super();
		numHand = 13;
		isSkipThisTurn = false;
		isTasked = false;
		sortType = SortType.HORIZONTAL;
	}

	public TLMNPlayer(long _id, String _name, long _cash, boolean _istablemaster) {

		super(_id, _name, _cash, _istablemaster);
		numHand = 13;
		isSkipThisTurn = false;
		isTasked = false;
		sortType = SortType.HORIZONTAL;

	}

	@Override
	public void resetForNewMatch() {
		// TODO Auto-generated method stub
		cardList.clear();
		numHand = 13;
		isSkipThisTurn = false;
		isTasked = false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		isReady = false;
		isTableMaster = false;
		cardList.clear();
		numHand = 13;
		isSkipThisTurn = false;
		isTasked = false;
	}

	@Override
	public Vector<Card> getCardList() {

		return (Vector<Card>) cardList;
	}
}
