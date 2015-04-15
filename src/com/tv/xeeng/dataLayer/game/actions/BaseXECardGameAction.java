package com.tv.xeeng.dataLayer.game.actions;

public class BaseXECardGameAction extends BaseXEGameAction {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private long prevPlayerId;
	private long currentPlayerId;

	// ===========================================================
	// Constructors
	// ===========================================================
	public BaseXECardGameAction(int t, long prevPlayerId, long currentPlayerId) {
		super(t);
		this.prevPlayerId = prevPlayerId;
		this.currentPlayerId = currentPlayerId;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public long getPrevPlayerId() {
		return prevPlayerId;
	}

	public long getCurrentPlayerId() {
		return currentPlayerId;
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
