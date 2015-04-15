package com.tv.xeeng.game.elements.common;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.tv.xeeng.game.BaseXeengGame;

public class RadioButton extends TiledSprite {
	private Text text;
	private boolean isChecked;
	private int checkedTileIndex = 0;

	private int selectedColor = Color.WHITE_ARGB_PACKED_INT;
	private int normalColor = android.graphics.Color.parseColor("#6d6c6c");
	//6d6c6c
	//  a80000

	public RadioButton(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion, CharSequence text,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				pVertexBufferObjectManager);
		init(text);
	}

	public RadioButton(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion, CharSequence text,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		init(text);
	}

	private void init(CharSequence text) {
		this.isChecked = false;
		this.text = new Text(getWidth() / 2, getHeight() / 2,
				BaseXeengGame.smallRegularFont, "abcdefghijklmnopqrstuvwxyz",
				getVertexBufferObjectManager());
		this.setText(text);
		this.attachChild(this.text);
	}
	
	public void setCheckedTileIndex(int index) {
		this.checkedTileIndex = index;
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		this.setCurrentTileIndex(isChecked ? checkedTileIndex : 1 - checkedTileIndex);
		this.text.setColor(isChecked ? selectedColor : normalColor);
	}

	public void setText(CharSequence cs) {
		this.text.setText(cs);
	}

	public void setSelectedColor(int color) {
		this.selectedColor = color;
	}

	public void setNormalColor(int color) {
		this.normalColor = color;
	}

	public boolean isChecked() {
		return isChecked;
	}
}
