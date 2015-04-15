package com.tv.xeeng.gamedata.entity;

import android.os.Parcel;

public abstract class MiniGamePlayer extends Player {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public MiniGamePlayer() {
		super();
	}

	public MiniGamePlayer(long _id, String _name, long _cash,
			boolean _istablemaster) {
		super(_id, _name, _cash, _istablemaster);
	}

	public MiniGamePlayer(Parcel in) {
		super(in);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public abstract int getScore();

	public abstract float getMaximumScores();
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
