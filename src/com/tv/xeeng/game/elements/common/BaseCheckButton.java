package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class BaseCheckButton extends GroupEntity {

	private TiledSprite mCheckSprite;
	private XeengText mTitleTxt;
	private boolean mIsChecked;

	public BaseCheckButton(float pX, float pY, ITiledTextureRegion mCheckTTR,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pVertexBufferObjectManager);

		mCheckSprite = new TiledSprite(mCheckTTR.getWidth(0) / 2,
				mCheckTTR.getHeight() / 2, mCheckTTR,
				mVertexBufferObjectManager);
		mTitleTxt = new XeengText(mCheckSprite.getWidth(), mCheckSprite.getY(),
				BaseXeengGame.smallRegularFont, "", 150,
				pVertexBufferObjectManager);
		mTitleTxt.setAnchorCenterX(0);
		attachChild(mCheckSprite);
		attachChild(mTitleTxt);
		mCheckSprite.setCurrentTileIndex(3);
		mIsChecked = false;
		setOnClickListener(mOnClickListener);
	}

	public void setTitle(String pString) {

		mTitleTxt.setText(pString);
		setSize(mCheckSprite.getWidth() + mTitleTxt.getBiengWidth(),
				mCheckSprite.getHeight());
	}

	public void check(boolean ischecked) {

		mIsChecked = ischecked;
		mCheckSprite.setCurrentTileIndex(ischecked ? 2 : 3);
	}

	public abstract void onCheckChange(BaseCheckButton pCheckButton);

	public boolean isChecked() {

		return mIsChecked;
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(TouchableEntity pTouchableEntity) {

			BaseCheckButton.this.check(!mIsChecked);
			onCheckChange(BaseCheckButton.this);
		}
	};
}
