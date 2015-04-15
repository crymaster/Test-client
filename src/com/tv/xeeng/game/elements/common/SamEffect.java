package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseCardGameActivity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;

public class SamEffect {

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	public BaseCardGameActivity baseGame;
	public Sprite sprite[] = new Sprite[2];

	float spriteSize = 43.0f;

	public SamEffect(BaseCardGameActivity activity) {
		this.baseGame = activity;
	}

	public void loadResource() {

	}

	public void initialize() {
		Sprite animatorSprite = new Sprite(CAMERA_WIDTH / 2
				- BaseCardGameActivity.animator.getWidth() / 2, CAMERA_HEIGHT
				/ 2 - BaseCardGameActivity.animator.getHeight() / 2,
				BaseCardGameActivity.animatorRegion,
				baseGame.getVertexBufferObjectManager());
		Sprite labelSprite = new Sprite(CAMERA_WIDTH / 2
				- BaseCardGameActivity.label.getWidth() / 2, CAMERA_HEIGHT / 2
				- BaseCardGameActivity.label.getHeight() / 2,
				BaseCardGameActivity.labelRegion,
				baseGame.getVertexBufferObjectManager());
		sprite[0] = animatorSprite;
		sprite[1] = labelSprite;

		sprite[0].setSize(spriteSize, spriteSize);
		sprite[1].setSize(spriteSize, spriteSize);

		RotationModifier yourModifier = new RotationModifier(3, 0, 360) {
			@Override
			protected void onModifierStarted(IEntity pItem) {
				super.onModifierStarted(pItem);
				// Your action after starting modifier
			}

			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				// Your action after finishing modifier
			}
		};

		// animatorSprite.setRotationCenter(-50, -50);
		// animatorSprite.registerEntityModifier(new
		// LoopEntityModifier(yourModifier));
	}

	public Sprite[] getSprite() {
		return sprite;
	}

	public float getSamWidth() {
		return spriteSize;
	}

}
