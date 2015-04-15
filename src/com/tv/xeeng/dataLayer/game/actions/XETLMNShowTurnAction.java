package com.tv.xeeng.dataLayer.game.actions;

import com.tv.xeeng.dataLayer.game.TLMNGame.FightCardType;
import com.tv.xeeng.gamedata.entity.Card;

import java.util.HashMap;
import java.util.Vector;

public class XETLMNShowTurnAction extends BaseXECardGameAction {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private boolean isNewRound;
	private boolean isSkip;
	private HashMap<String, Long> fightMaps;
	private FightCardType fightType;
	private Vector<Card> lastCard;

	// ===========================================================
	// Constructors
	// ===========================================================
	public XETLMNShowTurnAction(boolean isNewRound, boolean isSkip,
			HashMap<String, Long> fightMaps, FightCardType fightType,
			long prevPlayerId, long currentPlayerId, Vector<Card> lastCard) {
		super(ACTION_TLMN_SHOW_TURN, prevPlayerId, currentPlayerId);

		this.isNewRound = isNewRound;
		this.isSkip = isSkip;
		this.fightMaps = fightMaps;
		this.fightType = fightType;
		this.lastCard = lastCard;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public boolean isNewRound() {
		return isNewRound;
	}

	public boolean isSkip() {
		return isSkip;
	}

	public HashMap<String, Long> getFightMaps() {
		return fightMaps;
	}

	public FightCardType getFightType() {
		return fightType;
	}

	public Vector<Card> getLastCard() {
		return lastCard;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
