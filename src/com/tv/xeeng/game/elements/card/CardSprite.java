package com.tv.xeeng.game.elements.card;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class CardSprite extends Sprite {

	public CardSprite(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
	}

	public void recyle() {

	}

	public void fly(float startPoint[], float endPoint[], boolean isAnimated) {
		if (isAnimated) {

		} else {

		}
	}
}
