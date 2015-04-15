package com.tv.xeeng.gamedata.entity;

import java.util.ArrayList;
import java.util.Vector;

public abstract class BaseCardGamePlayer extends Player {

	public BaseCardGamePlayer() {

	}

	public BaseCardGamePlayer(long _id, String _name, long _cash,
			boolean _istablemaster) {
		super(_id, _name, _cash, _istablemaster);

	}

	public Vector<? extends Card> cardList; // array of player's cards
	public int numHand; // number of card in player's hand

	public boolean containCards(ArrayList<Card> pList) {

		boolean result = true;
		boolean flag = false;
		for (Card pcard : pList) {

			for (Card cd : cardList) {

				flag = false;
				if (pcard.serverValue == cd.serverValue) {

					flag = true;
					break;
				}
			}

			if (!flag) {

				result = false;
				break;
			}
		}

		return result;
	}

	public Vector<Card> getCardFromList(Vector<Card> pList) {

		Vector<Card> list = null;
		if (pList == null || pList.size() == 0) {

			return null;
		}

		list = new Vector<Card>();
		for (Card pcard : pList) {

			for (Card card : cardList) {

				if (pcard.serverValue == card.serverValue) {

					list.add(card);
					break;
				}
			}
		}
		return list;
	}

	public abstract void resetForNewMatch();

	public abstract void reset();

	public abstract Vector<? extends Card> getCardList();

}
