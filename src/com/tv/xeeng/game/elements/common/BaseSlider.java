package com.tv.xeeng.game.elements.common;

import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class BaseSlider extends GroupEntity {

	private ClipEntity mActiveClipEntity;
	private Sprite mBg, mActive, mIndicator;
	private float mProgress;

	public BaseSlider(float pX, float pY, ITextureRegion pBgTR,
			ITextureRegion pActiveTR, ITextureRegion pIndicatorTR,
			VertexBufferObjectManager pVertexBufferObjectManager) {

		super(pX, pY, pVertexBufferObjectManager);
		setSize(pBgTR.getWidth(), pBgTR.getHeight());

		mBg = new Sprite(0, 0, pBgTR, mVertexBufferObjectManager);
		mBg.setAnchorCenter(0, 0);
		attachChild(mBg);

		mActiveClipEntity = new ClipEntity(0, getHeight() / 2, getWidth(),
				getHeight());
		mActive = new Sprite(0, getHeight() / 2, pActiveTR,
				mVertexBufferObjectManager);
		mActive.setAnchorCenterX(0);
		mActiveClipEntity.setAnchorCenterX(0);
		mActiveClipEntity.attachChild(mActive);
		attachChild(mActiveClipEntity);

		mIndicator = new Sprite(pIndicatorTR.getWidth() / 2, getHeight() / 2,
				pIndicatorTR, mVertexBufferObjectManager);
		attachChild(mIndicator);
	}

	public void setProgress(float pProgress) {

		mProgress = pProgress;
		float activeWidth = pProgress * getWidth();
		mIndicator.setX(activeWidth);
		mActiveClipEntity.setWidth(activeWidth);
	}

	public float getProgress() {

		return mProgress;
	}

	public abstract void onSliderChange(BaseSlider pSlider);

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {

		if (mIsEnabled) {

			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {

				mProgress = pTouchAreaLocalX / getWidth();
				setProgress(mProgress);
				mIndicator.setX(pTouchAreaLocalX);
				onSliderChange(this);
			} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_MOVE) {

				if (pTouchAreaLocalX <= getWidth() && pTouchAreaLocalX >= 0) {

					mProgress = pTouchAreaLocalX / getWidth();
					setProgress(mProgress);
					mIndicator.setX(pTouchAreaLocalX);
					onSliderChange(this);
				}
			}
			return true;
		}

		return false;
	}
}
