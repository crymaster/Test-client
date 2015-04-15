package com.tv.xeeng.gamedata.entity;

import java.util.Comparator;

public class DefaultCardComparator implements Comparator<Card> {

	@Override
	public int compare(Card lhs, Card rhs) {
		// TODO Auto-generated method stub
		return lhs.compareDefault(rhs);
	}

}
