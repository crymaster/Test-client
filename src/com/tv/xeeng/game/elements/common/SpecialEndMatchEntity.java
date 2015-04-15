package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

public class SpecialEndMatchEntity extends Entity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private Sprite specialText;
	private Sprite secondSpecialText;

	private Text normalWinningText;
	private Text normalLosingText;

	private Sprite imgGlowLight;
	private Sprite imgGlowDark;

	private float normalTextScale = 1f;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SpecialEndMatchEntity() {
		super(BaseXeengGame.DESIGN_WINDOW_WIDTH_PIXELS / 2,
				BaseXeengGame.DESIGN_WINDOW_HEIGHT_PIXELS / 2,
				BaseXeengGame.DESIGN_WINDOW_WIDTH_PIXELS,
				BaseXeengGame.DESIGN_WINDOW_HEIGHT_PIXELS);
		this.normalWinningText = new Text(getWidth() / 2, getHeight() / 2,
				BaseXeengGame.fontStrokeLight, "+-0123456789.",
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		this.normalLosingText = new Text(getWidth() / 2, getHeight() / 2,
				BaseXeengGame.fontStrokeDark, "+-0123456789.",
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

		if (BaseXeengGame.SCREEN_RATIO <= 1.5f) {
			normalTextScale = 0.5f;
		} else {
			normalTextScale = 0.6f;
		}

		normalWinningText.setScale(normalTextScale);
		normalLosingText.setScale(normalTextScale);

		Rectangle background = new Rectangle(getX(), getY(), getWidth(),
				getHeight(),
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		background.setColor(0f, 0f, 0f, 0.8f);

		imgGlowLight = new Sprite(getWidth() / 2, getHeight() / 2,
				BaseXeengGame.imgGlowLightTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

		imgGlowDark = new Sprite(getWidth() / 2, getHeight() / 2,
				BaseXeengGame.imgGlowDarkTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

		attachChild(background);
		attachChild(imgGlowLight);
		attachChild(imgGlowDark);
		attachChild(normalWinningText);
		attachChild(normalLosingText);

		setZIndex(0);
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
	public void show(ITextureRegion specialText, long cash) {
		show(specialText, CommonUtils.formatRewardCash(cash), cash >= 0);
	}

	public void show(ITextureRegion specialText, CharSequence text,
			boolean isVictory) {
		show(text, isVictory);

		float width = specialText.getWidth();
		float height = specialText.getHeight();
		float d = -5f;

		if (BaseXeengGame.SCREEN_RATIO > 1.5f) {
			// HD
			width /= 2;
			height /= 2;

			d = 5f;
		}

		this.specialText = new Sprite(getWidth() / 2, getHeight() / 2 + height
				/ 2 + d, width, height, specialText,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		this.specialText.setZIndex(20);

		normalWinningText.setY(getHeight() / 2 - 15);
		normalLosingText.setY(normalWinningText.getY());

		attachChild(this.specialText);
		sortChildren();
	}

	public void show(ITextureRegion specialText,
			ITextureRegion secondSpecialText, CharSequence text,
			boolean isVictory) {
		show(text, isVictory);

		float width = secondSpecialText.getWidth();
		float height = secondSpecialText.getHeight();
		float d = 0;

		if (BaseXeengGame.SCREEN_RATIO > 1.5f) {
			// HD
			width /= 2;
			height /= 2;
			d = -5f;
		}

		this.secondSpecialText = new Sprite(getWidth() / 2, getHeight() / 2
				+ secondSpecialText.getHeight() / 2 + d, width, height,
				secondSpecialText,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		this.secondSpecialText.setZIndex(20);

		width = specialText.getWidth();
		height = specialText.getHeight();

		d = 15f;

		if (BaseXeengGame.SCREEN_RATIO > 1.5f) {
			// HD
			width /= 2;
			height /= 2;
			d = 0;
		}

		this.specialText = new Sprite(getWidth() / 2, getHeight() / 2
				+ secondSpecialText.getHeight() + d, width, height,
				specialText,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		this.specialText.setZIndex(20);

		normalWinningText.setY(getHeight() / 2 - 15);
		normalLosingText.setY(normalWinningText.getY());

		attachChild(this.specialText);
		attachChild(this.secondSpecialText);

		sortChildren();
	}

	/**
	 * For showing a special end match without special text attached
	 * 
	 * @param text
	 * @param isVictory
	 */
	public void show(CharSequence text, boolean isVictory) {
		normalLosingText.setIgnoreUpdate(isVictory);
		normalLosingText.setVisible(!isVictory);
		imgGlowDark.setIgnoreUpdate(isVictory);
		imgGlowDark.setVisible(!isVictory);
		imgGlowDark.setZIndex(10);

		normalWinningText.setIgnoreUpdate(!isVictory);
		normalWinningText.setVisible(isVictory);
		imgGlowLight.setIgnoreUpdate(!isVictory);
		imgGlowLight.setVisible(isVictory);
		imgGlowLight.setZIndex(10);

		if (isVictory) {
			imgGlowLight.registerEntityModifier((new LoopEntityModifier(
					new SequenceEntityModifier(new FadeOutModifier(0.75f),
							new FadeInModifier(0.75f)), 2)));
		} else {
			imgGlowDark.registerEntityModifier((new LoopEntityModifier(
					new SequenceEntityModifier(new FadeOutModifier(0.75f),
							new FadeInModifier(0.75f)), 2)));
		}

		normalLosingText.setText(text);
		normalWinningText.setText(text);
		normalLosingText.setZIndex(30);
		normalWinningText.setZIndex(30);

		if (this.specialText != null) {
			normalWinningText.setY(getHeight() / 2);
			normalLosingText.setY(normalWinningText.getY());

			this.specialText.detachSelf();
			this.specialText.dispose();
			specialText = null;
		}

		if (this.secondSpecialText != null) {
			this.secondSpecialText.detachSelf();
			this.secondSpecialText.dispose();
			secondSpecialText = null;
		}

		setVisible(true);
		sortChildren();
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		setVisible(false);
		return true;
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
