package com.tv.xeeng.dataLayer.game.actions;

import com.tv.xeeng.dataLayer.game.BaseGame.KickOutReason;

public class XEPlayerKickedAction extends BaseXEGameAction {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private long userId;
	private KickOutReason reason;
	private String message;

	// ===========================================================
	// Constructors
	// ===========================================================
	public XEPlayerKickedAction(long userId, KickOutReason reason,
			String message) {
		super(ACTION_PLAYER_KICKED);
		this.userId = userId;
		this.reason = reason;
		this.message = message;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public long getUserId() {
		return userId;
	}

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
