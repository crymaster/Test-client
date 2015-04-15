package com.tv.xeeng.game.elements.card;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;

import com.tv.xeeng.game.BaseCardGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.XeengText;

public class CardHolder extends Entity {

	private TiledSprite mbgCard;
	private XeengText cardCountText;
	private Sprite bgCardCount;

	public CardHolder(float pX, float pY) {
		super(pX, pY);
		mbgCard = new TiledSprite(0, 0,
				BaseCardGameActivity.tiledCardsTTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		mbgCard.setSize(BaseXeengGame.CARD_WIDTH, BaseXeengGame.CARD_HEIGHT);
		attachChild(mbgCard);
		setSize(BaseXeengGame.CARD_WIDTH, BaseXeengGame.CARD_HEIGHT);
		mbgCard.setPosition(getWidth() / 2, getHeight() / 2);

		bgCardCount = new Sprite(
				getWidth() / 2,
				getHeight() / 2,
				((BaseCardGameActivity) BaseXeengGame.INSTANCE).cardPileNumberTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		attachChild(bgCardCount);

		cardCountText = new XeengText(getWidth() / 2, getHeight() / 2 - 10,
				BaseXeengGame.mediumRegularFont, "0123456789", new TextOptions(
						HorizontalAlign.CENTER),
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		cardCountText.setHeight(cardCountText.getBiengHeight());
		cardCountText.setColor(102f / 255f, 0, 0);
		cardCountText.setAnchorCenterX(0.5f);
		cardCountText.setAnchorCenterY(0);
		attachChild(cardCountText);
	}
	
	public void setBackCardIndex(int backCardIndex) {
		mbgCard.setCurrentTileIndex(backCardIndex);
	}

	public void setNumHand(int pNumHand) {

		if (pNumHand < 0) {
			// numHandTxt.setText("??");
			cardCountText.setVisible(false);
		} else {
			cardCountText.setVisible(true);
			cardCountText.setText("" + pNumHand);
		}
	}
}
