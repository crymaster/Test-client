package com.tv.xeeng.dataLayer.game.actions;

import com.tv.xeeng.dataLayer.game.BaseGame.KickOutReason;

public class XEShowMainPlayerIsKickedAction extends BaseXEGameAction {
	// ===========================================================
	// Constants
	// ===========================================================
	private KickOutReason reason;
	private String message;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public XEShowMainPlayerIsKickedAction(KickOutReason reason, String message) {
		super(ACTION_SHOW_MAIN_PLAYER_IS_KICKED);
		this.reason = reason;
		this.message = message;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public KickOutReason getReason() {
		return reason;
	}

	public String getMessage() {
		return message;
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
