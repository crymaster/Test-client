package com.tv.xeeng.gamedata.entity;

import java.util.Comparator;

public class TLMNCardComparator implements Comparator<Card> {

	@Override
	public int compare(Card lhs, Card rhs) {
		// TODO Auto-generated method stub
		return lhs.compareInTLMN(rhs);
	}

}
