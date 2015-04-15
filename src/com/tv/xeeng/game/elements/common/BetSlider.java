package com.tv.xeeng.game.elements.common;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

public class BetSlider extends GroupEntity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private Sprite thumb;
	private Sprite background;
	private Sprite indicator;
	private Text maxBetText;
	private Text currentBetText;

	private IOnProgressChangeListener listener;

	// ===========================================================
	// Constructors
	// ===========================================================
	public BetSlider(float x, float y, float width, float height, Font font,
			ITextureRegion backgroundTR, ITextureRegion thumbTR,
			ITextureRegion indicatorTR, VertexBufferObjectManager vbom) {
		super(x, y, vbom);

		super.setWidth(width);
		super.setHeight(height);

		thumb = new Sprite(width / 2, 30, thumbTR, vbom);
		background = new Sprite(width / 2, height / 2, width, height,
				backgroundTR, vbom);

		indicator = new Sprite(thumb.getWidth() / 2, thumb.getY()
				+ thumb.getHeight(), indicatorTR, vbom);

		maxBetText = new XeengText(getWidth() / 2, height - 20, font,
				"0123456789 KMB", vbom);
		currentBetText = new XeengText(width / 2,
				indicator.getHeight() / 2 + 5, font, "0123456789 KMB", vbom);

		maxBetText.setColor(Color.YELLOW);
		currentBetText.setColor(Color.YELLOW);

		attachChild(background);
		attachChild(maxBetText);
		attachChild(thumb);

		thumb.attachChild(indicator);
		indicator.attachChild(currentBetText);
	}

	public BetSlider(float x, float y, float width, float height, Font font,
			ITextureRegion backgroundTR, ITextureRegion sliderTR,
			ITextureRegion indicatorTR, VertexBufferObjectManager vbom,
			IOnProgressChangeListener listener) {
		this(x, y, width, height, font, backgroundTR, sliderTR, indicatorTR,
				vbom);
		this.listener = listener;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public void setProgress(float progress) {
		thumb.setY(progress * (getHeight() - 30 - 50) + 30);
	}

	public float getProgress() {
		return (thumb.getY() - 30) / (getHeight() - 30 - 50);
	}

	public void setMaxBetText(CharSequence text) {
		maxBetText.setText(text);
	}

	public void setCurrentBetText(CharSequence text) {
		currentBetText.setText(text);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		float y = pTouchAreaLocalY;
		if (y < 30) {
			y = 30;
		}
		if (y > getHeight() - 50) {
			y = getHeight() - 50;
		}
		thumb.setY(y);
		if (listener != null) {
			listener.onProgressChange(getProgress());
		}

		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	public static interface IOnProgressChangeListener {
		public void onProgressChange(float progress);
	}
}
