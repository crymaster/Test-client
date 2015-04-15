package com.tv.xeeng.dataLayer.game.actions;

public class BaseXEGameAction {
	// Common Actions
	public static final int ACTION_PLAYER_LEFT = 200;
	public static final int ACTION_PLAYER_KICKED = 201;
	public static final int ACTION_SHOW_END_MATCH = 202;
	public static final int ACTION_SHOW_PLAYER_JOINED = 203;
	public static final int ACTION_SHOW_PLAYER_READY = 204;
	public static final int ACTION_SHOW_MAIN_PLAYER_IS_KICKED = 205;
	public static final int ACTION_SHOW_START_GAME_FAILED = 206;
	public static final int ACTION_SHOW_LEAVE_MATCH_FAILED = 208;

	// Phom Actions
	public static final int ACTION_PHOM_SHOW_AN_BAI = 100;
	public static final int ACTION_PHOM_SHOW_HA_BAI = 101;
	public static final int ACTION_PHOM_SHOW_GUI_CARD = 102;
	public static final int ACTION_PHOM_SHOW_BOC_BAI = 103;
	public static final int ACTION_PHOM_SHOW_TURN = 104;
	public static final int ACTION_PHOM_SHOW_GET_POCKER = 105;

	// TLMN Actions
	public static final int ACTION_TLMN_SHOW_TURN = 106;
	public static final int ACTION_TLMN_SHOW_GET_POCKER = 107;

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public BaseXEGameAction(int t) {
		type = t;
	}
}
