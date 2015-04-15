package com.tv.xeeng.game.elements.card;

import com.tv.xeeng.gamedata.entity.Card;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TileCardSprite extends TiledSprite {

	public TileCardSprite(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);

	}

	public void show(Card pCard) {

		this.setCurrentTileIndex(pCard.serverValue - 1);
		this.setIgnoreUpdate(false);
		this.setVisible(true);
	}
}
