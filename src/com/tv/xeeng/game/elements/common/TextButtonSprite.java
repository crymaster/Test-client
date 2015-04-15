package com.tv.xeeng.game.elements.common;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

public class TextButtonSprite extends ButtonSprite {

	private XeengText mTitle;

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITextureRegion pNormalTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager);
		initTitle(pTitle, pFont);
	}

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITextureRegion pNormalTextureRegion,
			ITextureRegion pPressedTextureRegion,
			ITextureRegion pDisabledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			OnClickListener pOnClickListener) {
		super(pX, pY, pNormalTextureRegion, pPressedTextureRegion,
				pDisabledTextureRegion, pVertexBufferObjectManager,
				pOnClickListener);
		initTitle(pTitle, pFont);
	}

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITextureRegion pNormalTextureRegion,
			ITextureRegion pPressedTextureRegion,
			ITextureRegion pDisabledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pNormalTextureRegion, pPressedTextureRegion,
				pDisabledTextureRegion, pVertexBufferObjectManager);
		initTitle(pTitle, pFont);
	}

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITextureRegion pNormalTextureRegion,
			ITextureRegion pPressedTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			OnClickListener pOnClickListener) {
		super(pX, pY, pNormalTextureRegion, pPressedTextureRegion,
				pVertexBufferObjectManager, pOnClickListener);
		initTitle(pTitle, pFont);
	}

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITextureRegion pNormalTextureRegion,
			ITextureRegion pPressedTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pNormalTextureRegion, pPressedTextureRegion,
				pVertexBufferObjectManager);
		initTitle(pTitle, pFont);
	}

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITextureRegion pNormalTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			OnClickListener pOnClickListener) {
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager,
				pOnClickListener);
		initTitle(pTitle, pFont);
	}

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			OnClickListener pOnClickListener) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager,
				pOnClickListener);
		initTitle(pTitle, pFont);
	}

	public TextButtonSprite(float pX, float pY, String pTitle, Font pFont,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		initTitle(pTitle, pFont);
	}

	private void initTitle(String pTitle, Font pFont) {

		mTitle = new XeengText(getWidth() / 2, getHeight() / 2, pFont, pTitle,
				getVertexBufferObjectManager());
		attachChild(mTitle);
	}

	public void setColorTitle(Color pColor) {

		mTitle.setColor(pColor);
	}

	@Override
	public void setIgnoreUpdate(boolean pIgnoreUpdate) {

		super.setIgnoreUpdate(pIgnoreUpdate);
		mTitle.setIgnoreUpdate(pIgnoreUpdate);
	}

	@Override
	public void setVisible(boolean pVisible) {

		super.setVisible(pVisible);
		mTitle.setVisible(pVisible);
	}

	public XeengText getTitle() {
		return mTitle;
	}
}
