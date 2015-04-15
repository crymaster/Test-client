package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.color.Color;

public abstract class ActionButton extends BaseGrowButton {
	private Text text;

	public ActionButton(float pX, float pY, ITextureRegion pTextureRegion) {
		this(pX, pY, pTextureRegion, BaseXeengGame.mediumRegularFont, "");
	}

	public ActionButton(float pX, float pY, ITextureRegion pTextureRegion,
			Font font, CharSequence text) {
		super(pX, pY, pTextureRegion);
		initText(font, text);
	}

	public ActionButton(float pX, float pY, ITiledTextureRegion pTileTR,
			Font font, CharSequence text) {
		super(pX, pY, pTileTR);
		initText(font, text);
	}

	private void initText(Font font, CharSequence text) {
		this.text = new Text(getWidth() / 2, getHeight() / 2, font, text,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		attachChild(this.text);
	}

	public void setText(CharSequence cs) {
		text.setText(cs);
	}

	public void setEnabled(boolean enabled) {
		this.mIsEnabled = enabled;
	}

	public boolean isEnabled() {
		return this.mIsEnabled;
	}

	public void setTextColor(Color color) {
		text.setColor(color);
	}

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

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
