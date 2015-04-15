package com.tv.xeeng.game.elements.card;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.GroupEntity;
import com.tv.xeeng.game.elements.common.XeengText;
import com.tv.xeeng.game.elements.common.XeengTextOptions;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;

public class DutyHolder extends GroupEntity {

	private ITextureRegion mStarTR;
	private XeengText mNameTxt, mMultiTxt;
	private Sprite mStarSprite;

	public DutyHolder(float pX, float pY, ITextureRegion pStarTR,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pVertexBufferObjectManager);
		mStarTR = pStarTR;
		init();
		hidden();
	}

	private void init() {

		mNameTxt = new XeengText(0, 0, BaseXeengGame.mediumRegularFont,
				"Phom abcde", "Phom abcde".length(), new XeengTextOptions(
						HorizontalAlign.RIGHT), mVertexBufferObjectManager);
		attachChild(mNameTxt);
		mNameTxt.setColor(1, 1, 0);
		mStarSprite = new Sprite(0, 0, mStarTR, mVertexBufferObjectManager);
		attachChild(mStarSprite);
		mMultiTxt = new XeengText(0, 0, BaseXeengGame.mediumRegularFont, "", 1,
				mVertexBufferObjectManager);
		attachChild(mMultiTxt);
		mMultiTxt.setColor(1, 0, 0);
	}

	public void show(String pName, String pMulti) {

		if (pName.length() > 10 || pMulti.length() > 1)
			return;

		setIgnoreUpdate(false);
		setVisible(true);

		mNameTxt.setText(pName);
		mMultiTxt.setText(pMulti);
		updatePosition();
	}

	private void updatePosition() {

		setSize(mStarSprite.getWidth() + mNameTxt.getBiengWidth()
				+ BaseXeengGame.SPACING, mStarSprite.getHeight());
		mNameTxt.setPosition(mNameTxt.getBiengWidth() / 2, getHeight() / 2);
		mStarSprite.setPosition(mNameTxt.getBiengWidth()
				+ BaseXeengGame.SPACING + mStarSprite.getWidth() / 2,
				getHeight() / 2);
		mMultiTxt.setPosition(mStarSprite);
	}

	public void hidden() {

		setIgnoreUpdate(true);
		setVisible(false);
	}
}
