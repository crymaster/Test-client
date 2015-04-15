package com.tv.xeeng.game.elements.card.tlmn;

import com.tv.xeeng.game.BaseCardGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.TLMNGameActivity;
import com.tv.xeeng.game.elements.common.PlayerSprite.PlayerLocation;
import com.tv.xeeng.gamedata.entity.Card;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.ease.EaseQuintIn;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.EaseStrongOut;

import java.util.ArrayList;
import java.util.List;

public class FightingCardHolder extends Entity {

	private float mstartPoint[];
	private float mendPoint[];
	private ArrayList<Sprite> mcardList;
	private PlayerLocation mPlayerLoc;

	private final float SCALE = TLMNGameActivity.MIN_CARD_SCALE;

	public FightingCardHolder(List<Card> pTurnedCardList, float pStartPoint[],
			float pEndPoint[], PlayerLocation pPlayerLoc) {

		super(-5000, -5000);
		mstartPoint = pStartPoint;
		mendPoint = pEndPoint;
		mPlayerLoc = pPlayerLoc;
		mcardList = new ArrayList<Sprite>();

		float width = 0, height = 0;
		height = BaseXeengGame.CARD_HEIGHT * SCALE;
		width = ((pTurnedCardList.size() - 1) * BaseXeengGame.CARD_WIDTH / 2 + BaseXeengGame.CARD_WIDTH)
				* SCALE;
		setSize(width, height);

		for (int i = 0; i < pTurnedCardList.size(); i++) {
			Card cd = pTurnedCardList.get(i);
			Sprite cardSprite = new Sprite(i * BaseXeengGame.CARD_WIDTH * SCALE
					/ 2, getHeight() / 2,
					BaseCardGameActivity.tiledCardsTTR
							.getTextureRegion(cd.serverValue - 1),
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			cardSprite.setSize(BaseXeengGame.CARD_WIDTH,
					BaseXeengGame.CARD_HEIGHT);
			cardSprite.setScale(SCALE);
			attachChild(cardSprite);
			cardSprite.setAnchorCenterX(0);
			mcardList.add(cardSprite);
		}
		setVisible(false);
	}

	public FightingCardHolder(ArrayList<Card> pData) {
		this(pData, null, null, null);
	}

	public void showNow(float endPoint[]) {
		setPosition(endPoint[0], endPoint[1]);
		setVisible(true);
	}

	public void fly() {
		if (mstartPoint == null && mendPoint == null && mPlayerLoc == null)
			return;
		setVisible(true);
		ScaleModifier scaleIn = new ScaleModifier(0.1f, 1, 1.4f,
				EaseStrongOut.getInstance());
		ScaleModifier scaleOut = new ScaleModifier(0.2f, 1.4f, 1,
				EaseStrongIn.getInstance());
		this.setIgnoreUpdate(false);
		this.registerEntityModifier(new ParallelEntityModifier(
				new SequenceEntityModifier(scaleIn, scaleOut),
				new MoveModifier(0.3f, mstartPoint[0], mstartPoint[1],
						mendPoint[0], mendPoint[1], EaseQuintIn.getInstance())));
	}

	public void blurry() {

		for (int i = 0; i < getChildCount(); i++) {

			getChildByIndex(i).setColor(180f / 255f, 180f / 255f, 180f / 255f);
		}
	}

	public void disappear() {

		setVisible(false);
		BaseXeengGame.INSTANCE.runOnUpdateThread(new Runnable() {
			public void run() {

				detachSelf();
			}
		});
	}

	public ArrayList<Sprite> getCardList() {
		return mcardList;
	}

	@Override
	public void setVisible(boolean pVisible) {

		for (int i = 0; i < getChildCount(); i++) {

			getChildByIndex(i).setVisible(true);
		}
		super.setVisible(pVisible);
	}
}
