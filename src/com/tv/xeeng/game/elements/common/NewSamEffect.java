package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseCardGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;

public class NewSamEffect extends Entity {
	// private static final int CAMERA_WIDTH = 720;
	// private static final int CAMERA_HEIGHT = 480;

	float spriteSize = 43.0f;
	private RotationModifier yourModifier;
	private Sprite animatorSprite;
	LoopEntityModifier animation;

	public NewSamEffect(float x, float y) {
		super(x, y);
		animatorSprite = new Sprite(0, 0, BaseCardGameActivity.animatorRegion,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		Sprite labelSprite = new Sprite(0, 0, BaseCardGameActivity.labelRegion,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		animatorSprite.setSize(spriteSize, spriteSize);
		labelSprite.setSize(spriteSize, spriteSize);
		setSize(spriteSize, spriteSize);
		this.attachChild(animatorSprite);
		this.attachChild(labelSprite);

		yourModifier = new RotationModifier(3, 0, 360) {
			@Override
			protected void onModifierStarted(IEntity pItem) {
				super.onModifierStarted(pItem);
			}

			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
			}
		};
		animation = new LoopEntityModifier(yourModifier);
		runAnimation();
	}

	public void runAnimation() {
		try {
			// stopAnimation();
			animatorSprite.registerEntityModifier(animation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopAnimation() {
		try {
			animatorSprite.unregisterEntityModifier(animation);
		} catch (Exception e) {

		}
	}
}
