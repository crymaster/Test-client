package com.tv.xeeng.game.elements.common;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.tv.xeeng.game.BaseXeengGame;

public class RoundedRectProgressTimer extends ProgressTimer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public RoundedRectProgressTimer(float x, float y, int _width, int _height,
			int timeout, BaseXeengGame _gactivity) {
		super(x, y, _width, _height, timeout, _gactivity);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	protected void drawRoundedCorners(Canvas pCanvas) {
		int w = mwidth * mcounter / maxCount;
		int radius = 4;

		pCanvas.drawRoundRect(new RectF(0, 0, mwidth - w, mheight), radius,
				radius, paint);

		// if (w >= radius && w <= mwidth - radius) {
		// pCanvas.drawRect(new RectF(new Rect(radius, 0, mwidth - w,
		// 6)), paint);
		// } else if (w < radius) {
		// int r1 = radius - w;
		// pCanvas.drawRoundRect(new RectF(new Rect(radius, 0, mwidth
		// - w, 6)), r1, r1, paint);
		// }
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
